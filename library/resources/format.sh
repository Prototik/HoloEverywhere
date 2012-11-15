#!/bin/bash
for i in *.json; do
	data=`cat "$i"`
	echo "$data" | python -mjson.tool > "`basename $i`"
done
