#!/bin/bash
gen() {
	png="`basename "$1" .svg`.png"
	if [ ! -e "$png" ]; then
		inkscape -zCd $2 -e "$png" "$1" > /dev/null
		convert -antialias -strip "$png" "$png"
		optipng -quiet -o7 "$png"
	fi
}

for z in *.svg; do
	echo " Convert `basename "$z" .svg`"
	gen $z 90
done
