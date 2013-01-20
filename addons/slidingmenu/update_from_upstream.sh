#!/bin/bash

die() {
	echo $@
	rm -rf upstream
	exit
}

fail() {
	die " [FAILED]"
}

success() {
	echo " [OK]"
}

perform() {
	echo -n $1
	$2 && success || fail
}

rm -rf upstream
git clone git@github.com:jfeinstein10/SlidingMenu.git upstream || die "Cannot fetch upstream version"

perform "Remove old sources..." "rm -rf src/com"
perform "Copy new sources..." "cp -r upstream/library/src/com src"
perform "Remove not needed SlidingActivities..." "rm -rf `ls -d src/com/slidingmenu/lib/app/* | grep -v Base | grep -v Helper`"
perform "Remove old resources..." "rm -rf res/*"
perform "Copy new resources..." "cp -r upstream/library/res ."

echo "Check on the correct build..."
mvn clean package || die "SlidingMenu build failed!"
