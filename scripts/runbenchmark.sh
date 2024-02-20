#!/bin/sh

# This file is part of VoltDB.
#  Copyright (C) 2008-2022 VoltDB Inc.
#
#  Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
#  "Software"), to deal in the Software without restriction, including
#  without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so, subject to
#  the following conditions:
#
#  The above copyright notice and this permission notice shall be
#  included in all copies or substantial portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
#  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
#  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
#  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
#  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
#  OTHER DEALINGS IN THE SOFTWARE.


. $HOME/.profile

ST=$1
MX=$2
INC=$3
USERCOUNT=$4
DURATION=600

if 	
	[ "$MX" = "" -o "$ST" = "" -o "$INC" = "" -o "$USERCOUNT" = "" ]
then
	echo Usage: $0 start_tps max_tps increment usercount

	exit 1
fi

cd
mkdir logs 2> /dev/null

cd voltdb-charglt/jars 

# silently kill off any copy that is currently running...
kill -9 `ps -deaf | grep ChargingDemoKVStore.jar  | grep -v grep | awk '{ print $2 }'` 2> /dev/null
kill -9 `ps -deaf | grep ChargingDemoTransactions.jar  | grep -v grep | awk '{ print $2 }'` 2> /dev/null

sleep 2 

CT=${ST}

while
	[ "${CT}" -le "${MX}" ]
do

	DT=`date '+%Y%m%d_%H%M%S'`
	echo "Starting a $DURATION second run at ${CT} Transactions Per Millisecond"
	echo `date` java ${JVMOPTS}  -jar ChargingDemoTransactions.jar `cat $HOME/.vdbhostnames`  ${USERCOUNT} ${CT} ${DURATION} 60 >> $HOME/logs/activity.log
	java ${JVMOPTS}  -jar ChargingDemoTransactions.jar `cat $HOME/.vdbhostnames`  ${USERCOUNT} ${CT} ${DURATION} 60 
	if 
		[ "$?" = "1" ]
	then
		break;
	fi

	CT=`expr $CT + ${INC}`
done


exit 0
