#!/bin/bash
for i in *.json; do
	echo " # Format: " $i
	data=`cat "$i"`
	echo "$data" | python -mjson.tool > "`basename $i`"
done
