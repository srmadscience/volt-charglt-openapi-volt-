/* This file is part of VoltDB.
 * Copyright (C) 2008-2024 VoltD Active Data Inc.
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

package chargingdemoprocs;

import java.util.Arrays;

import org.voltdb.VoltTable;
import org.voltdb.types.TimestampType;

public class UserObject {

    public long userId;
    public int statusCode;
    public long lockId = Long.MIN_VALUE;

    public TimestampType userLastSeen;
    public TimestampType userSoftlockExpiry;

    public long balance;

    public UserObjectUsage[] userUsage = new UserObjectUsage[0];

    public UserRecentTransactions[] recentTransactions = new UserRecentTransactions[0];

    public UserObject(long userId, int statusCode, long lockId, VoltTable[] voltTables) {
        super();
        this.userId = userId;
        this.statusCode = statusCode;
        this.lockId = lockId;

        VoltTable userTable = voltTables[voltTables.length - 4];
        VoltTable usageTable = voltTables[voltTables.length - 3];
        VoltTable balanceTable = voltTables[voltTables.length - 2];
        VoltTable transactionsTable = voltTables[voltTables.length - 1];

        userTable.advanceRow();
        userLastSeen = userTable.getTimestampAsTimestamp("USER_LAST_SEEN");
        userSoftlockExpiry = userTable.getTimestampAsTimestamp("USER_SOFTLOCK_EXPIRY");

        if (usageTable.getRowCount() > 0) {
            userUsage = new UserObjectUsage[usageTable.getRowCount()];
            int arrayLocation = 0;
            while (usageTable.advanceRow()) {
                userUsage[arrayLocation++] = new UserObjectUsage(usageTable.getLong("ALLOCATED_AMOUNT"),
                        usageTable.getLong("SESSIONID"), usageTable.getTimestampAsTimestamp("LASTDATE"));
            }
        }

        if (balanceTable.advanceRow()) {
            balance = balanceTable.getLong("BALANCE");
        }

        if (transactionsTable.getRowCount() > 0) {
            recentTransactions = new UserRecentTransactions[transactionsTable.getRowCount()];
            int arrayLocation = 0;
            while (transactionsTable.advanceRow()) {
                recentTransactions[arrayLocation++] = new UserRecentTransactions(
                        transactionsTable.getString("USER_TXN_ID"),
                        transactionsTable.getTimestampAsTimestamp("TXN_TIME"), transactionsTable.getLong("SESSIONID"),
                        transactionsTable.getLong("APPROVED_AMOUNT"), transactionsTable.getLong("SPENT_AMOUNT"),
                        transactionsTable.getString("PURPOSE"));
            }
        }

        transactionsTable.advanceRow();

    }

    public class UserObjectUsage {

        public long allocatedAmount;
        public long sessionId;
        public TimestampType lastSeen;

        public UserObjectUsage(long allocatedAmount, long sessionId, TimestampType lastSeen) {
            super();
            this.allocatedAmount = allocatedAmount;
            this.sessionId = sessionId;
            this.lastSeen = lastSeen;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("UserObjectUsage [allocatedAmount=");
            builder.append(allocatedAmount);
            builder.append(", sessionId=");
            builder.append(sessionId);
            builder.append(", lastSeen=");
            builder.append(lastSeen);
            builder.append("]");
            return builder.toString();
        }
    }

    public class UserRecentTransactions {

        public String txnId;
        public TimestampType txnTime;
        public long sessionId;
        public long approvedAmount;
        public long spentAmount;
        public String purpose;

        public UserRecentTransactions(String txnId, TimestampType txnTime, long sessionId, long approvedAmount,
                long spentAmount, String purpose) {
            super();
            this.txnId = txnId;
            this.txnTime = txnTime;
            this.sessionId = sessionId;
            this.approvedAmount = approvedAmount;
            this.spentAmount = spentAmount;
            this.purpose = purpose;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("UserRecentTransactions [txnId=");
            builder.append(txnId);
            builder.append(", txnTime=");
            builder.append(txnTime);
            builder.append(", sessionId=");
            builder.append(sessionId);
            builder.append(", approvedAmount=");
            builder.append(approvedAmount);
            builder.append(", spentAmount=");
            builder.append(spentAmount);
            builder.append(", purpose=");
            builder.append(purpose);
            builder.append("]");
            return builder.toString();
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserObject [userId=");
        builder.append(userId);
        builder.append(", statusCode=");
        builder.append(statusCode);
        builder.append(", lockId=");
        builder.append(lockId);
        builder.append(", userLastSeen=");
        builder.append(userLastSeen);
        builder.append(", userSoftlockExpiry=");
        builder.append(userSoftlockExpiry);
        builder.append(", balance=");
        builder.append(balance);
        builder.append(", userUsage=");
        builder.append(Arrays.toString(userUsage));
        builder.append(", recentTransactions=");
        builder.append(Arrays.toString(recentTransactions));
        builder.append("]");
        return builder.toString();
    }

}
