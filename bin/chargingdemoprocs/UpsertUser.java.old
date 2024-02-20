package chargingdemoprocs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/UpsertUser")

public class UpsertUser extends VoltAPIProcedure {

    // @formatter:off

	public static final SQLStmt getUser = new SQLStmt("SELECT userid FROM user_table WHERE userid = ?;");

	public static final SQLStmt getTxn = new SQLStmt("SELECT txn_time FROM user_recent_transactions "
			+ "WHERE userid = ? AND user_txn_id = ?;");

	public static final SQLStmt addTxn = new SQLStmt("INSERT INTO user_recent_transactions "
			+ "(userid, user_txn_id, txn_time, approved_amount,spent_amount,purpose) VALUES (?,?,NOW,?,?,?);");

	public static final SQLStmt insertUser = new SQLStmt(
			"INSERT INTO user_table (userid, user_json_object,user_last_seen) "
					+ "VALUES (?,?,?);");

	public static final SQLStmt reportAddcreditEvent = new SQLStmt(
			"INSERT INTO user_financial_events (userid,amount,user_txn_id,message) VALUES (?,?,?,?);");

	// @formatter:on

    @POST
    @Operation(summary = "UpsertUser", description = "UpsertUser", tags = { "chargingdemoprocs" })

    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESPONSE_200, description = "Locked", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = "No Such User", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    public VoltTable[] UpsertUser(long userId, long addBalance, String json, String purpose, TimestampType lastSeen,
            String txnId) throws VoltAbortException {

        long currentBalance = 0;

        voltQueueSQL(getUser, userId);
        voltQueueSQL(getTxn, userId, txnId);

        VoltTable[] results = voltExecuteSQL();

        byte statusCode;
        String statusString = "";

        if (results[1].advanceRow()) {

            statusString = "Event already happened at " + results[1].getTimestampAsTimestamp("txn_time").toString();
            statusCode = 2;

        } else {

            voltQueueSQL(addTxn, userId, txnId, 0, addBalance, "Upsert user");

            statusCode = 0;
            if (!results[0].advanceRow()) {

                statusString = "Created user " + userId + " with opening credit of " + addBalance;
                voltQueueSQL(insertUser, userId, json, lastSeen);
                voltQueueSQL(reportAddcreditEvent, userId, addBalance, txnId, "user created");

            } else {

                statusString = "Updated user " + userId + " - added credit of " + addBalance + "; balance now "
                        + currentBalance;

                voltQueueSQL(reportAddcreditEvent, userId, addBalance, txnId, "user upserted");

            }

        }
        voltExecuteSQL();
        return castObjectToVoltTableArray(statusString, statusCode, "");
    }
}
