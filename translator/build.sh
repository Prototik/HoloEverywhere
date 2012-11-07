#!/bin/bash
die() {
	echo $1
	exit 1
}

[ ! -d "$ANDROID_HOME/platforms/android-16" ] && die "You must specify ANDROID_HOME env"

build() {
	cat "resources/$1" | python -mjson.tool > "$1"
	java -jar translator.jar -f "$1" -o "../$2/res" -v 16 -s "$ANDROID_HOME"
	rm -f "$1"
}

build library.json library
build demo.json demo
