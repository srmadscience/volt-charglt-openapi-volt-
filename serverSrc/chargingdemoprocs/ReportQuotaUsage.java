package chargingdemoprocs;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/ReportQuotaUsage")

public class ReportQuotaUsage extends VoltAPIProcedure {

    // @formatter:off


   public static final SQLStmt getUser = new SQLStmt(
			"SELECT userid FROM user_table WHERE userid = ?;");

    public static final SQLStmt removeOldestTransaction = new SQLStmt("DELETE "
              + "FROM user_recent_transactions "
              + "WHERE userid = ? "
              + "AND txn_time < DATEADD(MINUTE, -1,NOW) "
              + "ORDER BY userid, txn_time, user_txn_id LIMIT 2;");

    public static final SQLStmt getTxn = new SQLStmt("SELECT txn_time FROM user_recent_transactions "
            + "WHERE userid = ? AND user_txn_id = ?;");

	public static final SQLStmt getUserBalance = new SQLStmt("SELECT balance, CAST(? AS BIGINT) sessionid FROM user_balance WHERE userid = ?;");

	public static final SQLStmt getCurrentlyAllocated = new SQLStmt(
			"select nvl(sum(allocated_amount),0)  allocated_amount from user_usage_table where userid = ?;");

	public static final SQLStmt addTxn = new SQLStmt("INSERT INTO user_recent_transactions "
			+ "(userid, user_txn_id, txn_time, approved_amount,spent_amount,purpose,sessionid) VALUES (?,?,NOW,?,?,?,?);");

	public static final SQLStmt delOldUsage = new SQLStmt(
			"DELETE FROM user_usage_table WHERE userid = ? AND sessionid = ?;");

	public static final SQLStmt reportFinancialEvent = new SQLStmt(
			"INSERT INTO user_financial_events (userid,amount,user_txn_id,message) VALUES (?,?,?,?);");

	public static final SQLStmt createAllocation = new SQLStmt("INSERT INTO user_usage_table "
			+ "(userid, allocated_amount,sessionid, lastdate) VALUES (?,?,?,NOW);");


	// @formatter:on
    @POST
    @Operation(summary = "Spends Credit", description = "Spends Credit", tags = { "chargingdemoprocs" })

    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESPONSE_200, description = "Locked", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = ReportQuotaUsageStatus.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = "No Such User", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    /**
     * @param userId         - Identifies a user
     * @param unitsUsed      - How many units of credit were used. Initially this
     *                       will be zero, as we start by reserving credit.
     * @param unitsWanted    - How many units of credit the user is looking for.
     *                       Some or all of this may be granted.
     * @param inputSessionId - a Unique ID for a session. A negative number means a
     *                       new session.
     * @param txnId          - A unique ID for the network call. This is needed so
     *                       we can tell if a transaction completed, but didn't get
     *                       back to the client.
     * @return
     * @throws VoltAbortException
     */

    public VoltTable[] run(
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("userId") long userId,
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("unitsUsed") int unitsUsed,
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("unitsWanted") int unitsWanted,
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("inputSessionId") long inputSessionId,
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("txnId") String txnId)
            throws VoltAbortException {

        // Set session ID if needed.
        long sessionId = inputSessionId;

        if (sessionId == Long.MIN_VALUE) {
            sessionId = this.getUniqueId();
        }

        // See if this user is real or this transaction has already happened.
        // Get rid of old transaction records.
        voltQueueSQL(getUser, userId);
        voltQueueSQL(getTxn, userId, txnId);
        voltQueueSQL(removeOldestTransaction, userId);

        VoltTable[] results1 = voltExecuteSQL();
        VoltTable userTable = results1[0];
        VoltTable sameTxnTable = results1[1];

        // Sanity check: Does this user exist?
        if (!userTable.advanceRow()) {
            return castObjectToVoltTableArray("User " + userId + " does not exist", 1, "No User");
        }

        // Sanity Check: Is this a re-send of a transaction we've already done?
        if (sameTxnTable.advanceRow()) {
            this.setAppStatusCode(ReferenceData.STATUS_TXN_ALREADY_HAPPENED);
            return castObjectToVoltTableArray(
                    "Event already happened at " + results1[1].getTimestampAsTimestamp("txn_time").toString(), 2,
                    "Dup");
        }

        long amountSpent = unitsUsed * -1;
        String decision = "Spent " + amountSpent;

        // Delete old usage record
        voltQueueSQL(delOldUsage, userId, sessionId);
        voltQueueSQL(getUserBalance, sessionId, userId);
        voltQueueSQL(getCurrentlyAllocated, userId);

        // The first time we're called we won't have spent anything, we'll be reserving
        // credit.
        if (amountSpent != 0) {
            voltQueueSQL(reportFinancialEvent, userId, amountSpent, txnId, decision);
        }

        if (unitsWanted == 0) {
            voltQueueSQL(addTxn, userId, txnId, 0, amountSpent, decision, sessionId);
            voltQueueSQL(getUserBalance, sessionId, userId);
            voltQueueSQL(getCurrentlyAllocated, userId);

            this.setAppStatusCode(ReferenceData.STATUS_OK);
            return castObjectToVoltTableArray(new ReportQuotaUsageStatus(ReferenceData.STATUS_OK, voltExecuteSQL(true)),
                    0, "Called");
        }

        VoltTable[] results2 = voltExecuteSQL();

        VoltTable userBalance = results2[1];
        VoltTable allocated = results2[2];

        // Calculate how much money is actually available...

        userBalance.advanceRow();
        long availableCredit = userBalance.getLong("balance");

        if (allocated.advanceRow()) {
            availableCredit = availableCredit - allocated.getLong("allocated_amount");
        }

        long amountApproved = 0;

        byte statusCode = ReferenceData.STATUS_NO_MONEY;

        if (availableCredit < 0) {

            decision = decision + "; Negative balance: " + availableCredit;

        } else if (unitsWanted > availableCredit) {

            amountApproved = availableCredit;
            decision = decision + "; Allocated " + availableCredit + " units of " + unitsWanted + " asked for";
            statusCode = ReferenceData.STATUS_SOME_UNITS_ALLOCATED;

        } else {

            amountApproved = unitsWanted;
            decision = decision + "; Allocated " + unitsWanted;
            statusCode = ReferenceData.STATUS_ALL_UNITS_ALLOCATED;

        }

        voltQueueSQL(createAllocation, userId, amountApproved, sessionId);

        this.setAppStatusCode(statusCode);
        this.setAppStatusString(decision);
        // Note that transaction is now 'official'

        voltQueueSQL(addTxn, userId, txnId, amountApproved, amountSpent, decision, sessionId);
        voltQueueSQL(getUserBalance, sessionId, userId);
        voltQueueSQL(getCurrentlyAllocated, userId);

        return castObjectToVoltTableArray(new ReportQuotaUsageStatus(statusCode, voltExecuteSQL(true)), 0, "Called");

    }

}
