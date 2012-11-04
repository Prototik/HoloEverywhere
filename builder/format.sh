#!/bin/bash
mkdir new_resources
for i in resources/*.json; do
	cat "$i" | python -mjson.tool > "new_resources/`basename $i`"
done
rm -rf resources
mv new_resources resources
