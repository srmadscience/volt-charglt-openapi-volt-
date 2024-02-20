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

@Path("/delUser")
public class DelUser extends VoltAPIProcedure {

    // @formatter:off

	public static final SQLStmt delUser = new SQLStmt("DELETE FROM user_table WHERE userid = ?;");
	public static final SQLStmt delUserUsage = new SQLStmt("DELETE FROM user_usage_table WHERE userid = ?;");
	public static final SQLStmt delBalance = new SQLStmt("DELETE FROM user_balance WHERE userid = ?;");
	public static final SQLStmt delTxns = new SQLStmt("DELETE FROM user_recent_transactions WHERE userid = ?;");

	// @formatter:on

    /**
     * Deletes all information we have about a user.
     *
     * @param userId
     * @return
     * @throws VoltAbortException
     */
    @POST

    @Consumes({ "application/json;charset=utf-8" })
    @Produces({ "application/json;charset=utf-8" })
    @Operation(summary = "Delete User", description = "Delete User", tags = { "chargingdemoprocs" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "202", description = "Success", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Error", content = @Content(mediaType = "application/json;charset&#x3D;utf-8", schema = @Schema(implementation = Error.class))) })

    public VoltTable[] DelUser(
            @Parameter(in = ParameterIn.PATH, description = "User ID", required = true) @PathParam("userId") long userId)
            throws VoltAbortException {

        voltQueueSQL(delUser, userId);
        voltQueueSQL(delUserUsage, userId);
        voltQueueSQL(delBalance, userId);
        voltQueueSQL(delTxns, userId);

        VoltTable[] results = voltExecuteSQL(true);

        results[0].advanceRow();
        if (results[0].getLong(0) == 1) {
            return castObjectToVoltTableArray(null, 201, "User Deleted");
        }

        return castObjectToVoltTableArray(null, 400, "User Not Found");

    }
}
