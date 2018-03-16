#!/usr/bin/env bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
inkscape -z $SCRIPT_DIR/../resources/empty_basket.svg -e $SCRIPT_DIR/../app/src/main/res/drawable-mdpi/ic_empty_basket.png -w 176 -h 144
inkscape -z $SCRIPT_DIR/../resources/empty_basket.svg -e $SCRIPT_DIR/../app/src/main/res/drawable-hdpi/ic_empty_basket.png -w 264 -h 216
inkscape -z $SCRIPT_DIR/../resources/empty_basket.svg -e $SCRIPT_DIR/../app/src/main/res/drawable-xhdpi/ic_empty_basket.png -w 352 -h 288
inkscape -z $SCRIPT_DIR/../resources/empty_basket.svg -e $SCRIPT_DIR/../app/src/main/res/drawable-xxhdpi/ic_empty_basket.png -w 528 -h 432
