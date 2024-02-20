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
import org.voltdb.types.TimestampType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/GetAndLockUser")

public class GetAndLockUser extends VoltAPIProcedure {

  // @formatter:off

    public static final SQLStmt getUser = new SQLStmt("SELECT * FROM user_table WHERE userid = ?;");

    public static final SQLStmt getAllTxn = new SQLStmt("SELECT * "
        + "FROM user_recent_transactions "
        + "WHERE userid = ? ORDER BY txn_time, user_txn_id;");

	public static final SQLStmt getUserUsage = new SQLStmt(
			"SELECT * FROM user_usage_table WHERE userid = ? ORDER BY sessionid;");

    public static final SQLStmt upsertUserLock = new SQLStmt("UPDATE user_table "
        + "SET user_softlock_sessionid = ? "
        + "   ,user_softlock_expiry = DATEADD(MILLISECOND,?,?) "
        + "WHERE userid = ?;");
    
    public static final SQLStmt getUserBalance = new SQLStmt("SELECT * FROM user_balance WHERE userid = ?;");


    // @formatter:on

    /**
     * Gets all the information we have about a user, while adding an expiring
     * timestamp and an internally generated lock id that is used to do updates.
     *
     * @param userId
     * @return lockid (accessible via ClientStatus.getAppStatusString())
     * @throws VoltAbortException
     */
    @POST
    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @Operation(summary = "GetAndLockUser", description = "GetAndLockUser", tags = { "chargingdemoprocs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESPONSE_200, description = "Locked", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = UserObject.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = "No Such User", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    public VoltTable[] run(long userId) throws VoltAbortException {

        voltQueueSQL(getUser, userId);

        VoltTable[] userRecord = voltExecuteSQL();

        // Sanity check: Does this user exist?
        if (!userRecord[0].advanceRow()) {
            throw new VoltAbortException("User " + userId + " does not exist");
        }

        final TimestampType currentTimestamp = new TimestampType(this.getTransactionTime());
        final TimestampType lockingSessionExpiryTimestamp = userRecord[0]
                .getTimestampAsTimestamp("user_softlock_expiry");

        byte statusCode;
        long lockingSessionId = -1;

        // If somebody has locked this session and the lock hasn't expired complain...
        if (lockingSessionExpiryTimestamp != null && lockingSessionExpiryTimestamp.compareTo(currentTimestamp) > 0) {

            lockingSessionId = userRecord[0].getLong("user_softlock_sessionid");
            statusCode = ReferenceData.STATUS_RECORD_ALREADY_SOFTLOCKED;
            this.setAppStatusString("User " + userId + " has already been locked by session " + lockingSessionId);

        } else {
            // 'Lock' record
            lockingSessionId = getUniqueId();
            statusCode = ReferenceData.STATUS_RECORD_HAS_BEEN_SOFTLOCKED;

            // Note how we pass the lock ID back...
            this.setAppStatusString("" + lockingSessionId);
            voltQueueSQL(upsertUserLock, getUniqueId(), ReferenceData.LOCK_TIMEOUT_MS, currentTimestamp, userId);
        }

        this.setAppStatusCode(statusCode);

        voltQueueSQL(getUser, userId);
        voltQueueSQL(getUserUsage, userId);
        voltQueueSQL(getUserBalance, userId);
        voltQueueSQL(getAllTxn, userId);


        UserObject u = new UserObject(userId, statusCode, lockingSessionId, voltExecuteSQL(true));

        return castObjectToVoltTableArray(u, statusCode, "Locked");

    }
}
