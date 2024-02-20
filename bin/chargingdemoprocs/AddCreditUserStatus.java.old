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

import org.voltdb.SQLStmt;
import org.voltdb.VoltTable;

public class AddCreditUserStatus {

    public long userId;

    public long balance;

    public long currentlyAllocated;

    public AddCreditUserStatus(long userId, VoltTable[] results) {

        this.userId = userId;

        VoltTable userBalanceRow = results[results.length - 2];
        VoltTable currentlyAllocatedRow = results[results.length - 1];

        userBalanceRow.advanceRow();
        currentlyAllocatedRow.advanceRow();

        balance = userBalanceRow.getLong("BALANCE");
        currentlyAllocated = currentlyAllocatedRow.getLong("ALLOCATED_AMOUNT");

    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @return the balance
     */
    public long getBalance() {
        return balance;
    }

    /**
     * @return the currentlyAllocated
     */
    public long getCurrentlyAllocated() {
        return currentlyAllocated;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AddCreditUserStatus [userId=");
        builder.append(userId);
        builder.append(", balance=");
        builder.append(balance);
        builder.append(", currentlyAllocated=");
        builder.append(currentlyAllocated);
        builder.append("]");
        return builder.toString();
    }

}
