#!/bin/bash
gen() {
	name=`basename "$1" .svg`
	png="../res/drawable-$2"
	if [ ! -e "$png" ]; then
		mkdir "$png"
	fi
	png="$png/$name.png"
	if [ ! -e "$png" ]; then
		inkscape -zCd $3 -e "$png" "$1" > /dev/null
		convert -antialias -strip "$png" "$png"
		optipng -quiet -o7 "$png"
	fi
}

for z in *.svg; do
	echo " Convert `basename "$z" .svg`"
	echo "  # LDPI"
	gen $z ldpi 60
	echo "  # MDPI"
	gen $z mdpi 90
	echo "  # HDPI"
	gen $z hdpi 120
	echo "  # XHDPI"
	gen $z xhdpi 180
	echo "  # XXHDPI"
	gen $z xxhdpi 270
done
