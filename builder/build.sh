#!/bin/sh
build() {
	java -jar builder.jar -s $1 -o $2
}
build resources/styles.json ../library/res/values/styles.xml
build resources/styles-v14.json ../library/res/values-v14/styles.xml
