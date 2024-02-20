package chargingdemoprocs;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Path("/getUser")

public class GetUser extends VoltAPIProcedure {

    // @formatter:off

	public static final SQLStmt getUser = new SQLStmt("SELECT * FROM user_table WHERE userid = ?;");
	public static final SQLStmt getUserUsage = new SQLStmt(
			"SELECT * FROM user_usage_table WHERE userid = ? ORDER BY sessionid;");
	public static final SQLStmt getUserBalance = new SQLStmt("SELECT * FROM user_balance WHERE userid = ?;");
	public static final SQLStmt getAllTxn = new SQLStmt("SELECT * FROM user_recent_transactions "
			+ "WHERE userid = ? ORDER BY txn_time, user_txn_id;");

	// @formatter:on

    /**
     * Gets all the information we have about a user.
     *
     * @param userId
     * @return
     * @throws VoltAbortException
     */
    @POST
    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @Operation(summary = "GetUser", description = "GetUser", tags = { "chargingdemoprocs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = RESPONSE_200, description = "Locked", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = UserObject.class))),
            @ApiResponse(responseCode = RESPONSE_400, description = "No Such User", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    public VoltTable[] GetUser(
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("userId") long userId)
            throws VoltAbortException {

        voltQueueSQL(getUser, userId);
        voltQueueSQL(getUserUsage, userId);
        voltQueueSQL(getUserBalance, userId);
        voltQueueSQL(getAllTxn, userId);

        UserObject u = new UserObject(userId, RESPONSE_VOLT_PROC_OK, -1, voltExecuteSQL(true));

        return castObjectToVoltTableArray(u, RESPONSE_VOLT_PROC_OK, "Locked");
    }

}
