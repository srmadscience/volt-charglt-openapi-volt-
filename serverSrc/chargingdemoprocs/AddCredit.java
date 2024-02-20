package chargingdemoprocs;

import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

/* This file is part of VoltDB.
 * Copyright (C) 2008-2024 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import org.voltdb.SQLStmt;
import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/addCredit")

public class AddCredit extends VoltAPIProcedure {

    // @formatter:off

	public static final SQLStmt getUser = new SQLStmt(
			"SELECT userid FROM user_table WHERE userid = ?;");

	public static final SQLStmt getTxn = new SQLStmt("SELECT txn_time FROM user_recent_transactions "
			+ "WHERE userid = ? AND user_txn_id = ?;");

	public static final SQLStmt addTxn = new SQLStmt("INSERT INTO user_recent_transactions "
			+ "(userid, user_txn_id, txn_time, approved_amount,spent_amount,purpose) VALUES (?,?,NOW,?,?,?);");

	public static final SQLStmt reportFinancialEvent = new SQLStmt("INSERT INTO user_financial_events "
			+ "(userid,amount,user_txn_id,message) VALUES (?,?,?,?);");

	public static final SQLStmt getUserBalance = new SQLStmt("SELECT balance FROM user_balance WHERE userid = ?;");

	public static final SQLStmt getCurrrentlyAllocated = new SQLStmt(
			"select nvl(sum(allocated_amount),0)  allocated_amount from user_usage_table where userid = ?;");

    public static final SQLStmt getOldestTxn = new SQLStmt("SELECT user_txn_id, txn_time "
            + "FROM user_recent_transactions "
            + "WHERE userid = ? "
            + "ORDER BY txn_time,userid,user_txn_id LIMIT 1;");

   public static final SQLStmt deleteOldTxn = new SQLStmt("DELETE FROM user_recent_transactions "
            + "WHERE userid = ? AND user_txn_id = ?;");


	// @formatter:on

    private static final long FIVE_MINUTES_IN_MS = 1000 * 60 * 5;

    /**
     * A VoltDB stored procedure to add credit to a user in the chargingdemo demo.
     * It checks that the user exists and also makes sure that this transaction
     * hasn't already happened.
     *
     * @param userId
     * @param extraCredit
     * @param txnId
     * @return Balance and Credit info
     * @throws VoltAbortException
     */

    @POST

    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @Operation(summary = "Adds Credit", description = "Adds Credit", tags = { "chargingdemoprocs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESPONSE_200, description = "Added", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = AddCreditUserStatus.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = "No Such User", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    public VoltTable[] run(
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("userId") long userId,
            @Parameter(in = ParameterIn.PATH, description = "Credit Delta", required = true) @PathParam("extraCredit") long extraCredit,
            @Parameter(in = ParameterIn.PATH, description = "Transaction ID", required = true) @PathParam("txnId") String txnId)
            throws VoltAbortException {

        // See if we know about this user and transaction...
        voltQueueSQL(getUser, userId);
        voltQueueSQL(getTxn, userId, txnId);
        voltQueueSQL(getOldestTxn, userId);

        VoltTable[] userAndTxn = voltExecuteSQL();

        // Sanity Check: Is this a real user?
        if (!userAndTxn[0].advanceRow()) {
            return castObjectToVoltTableArray(null, 1, "User " + userId + " does not exist");
        }

        // Sanity Check: Has this transaction already happened?
        if (userAndTxn[1].advanceRow()) {

            this.setAppStatusCode(ReferenceData.STATUS_TXN_ALREADY_HAPPENED);
            this.setAppStatusString(
                    "Event already happened at " + userAndTxn[1].getTimestampAsTimestamp("txn_time").toString());
            voltQueueSQL(reportFinancialEvent, userId, extraCredit, txnId, "Credit already added");
            voltExecuteSQL(true);
            return castObjectToVoltTableArray(null, 2,
                    "Event already happened at " + userAndTxn[1].getTimestampAsTimestamp("txn_time").toString());

        } else {

            // Report credit add...
            this.setAppStatusCode(ReferenceData.STATUS_CREDIT_ADDED);
            this.setAppStatusString(extraCredit + " added by Txn " + txnId);

            // Insert a row into the stream for each user's financial events.
            // The view user_balances can then calculate actual credit
            voltQueueSQL(addTxn, userId, txnId, 0, extraCredit, "Add Credit");
            voltQueueSQL(reportFinancialEvent, userId, extraCredit, txnId, "Added " + extraCredit);
        }

        // Delete oldest record if old enough
        if (userAndTxn[2].advanceRow()) {
            TimestampType oldestTxn = userAndTxn[2].getTimestampAsTimestamp("txn_time");

            if (oldestTxn.asExactJavaDate().before(new Date(getTransactionTime().getTime() - FIVE_MINUTES_IN_MS))) {
                String oldestTxnId = userAndTxn[2].getString("user_txn_id");
                voltQueueSQL(deleteOldTxn, userId, oldestTxnId);
            }
        }

        voltQueueSQL(getUserBalance, userId);
        voltQueueSQL(getCurrrentlyAllocated, userId);

        return castObjectToVoltTableArray(new AddCreditUserStatus(userId,voltExecuteSQL(true)), 0, "Credit Added");
    }
}
