#!/bin/bash
set -e

rm -rf build/
mkdir -p build/

inkscape -e build/launcher_icon-mdpi.png -a 15:15:93:93 -w 48 -h 48 launcher_icon.svg
inkscape -e build/launcher_icon-hdpi.png -a 15:15:93:93 -w 72 -h 72 launcher_icon.svg
inkscape -e build/launcher_icon-xhdpi.png -a 15:15:93:93 -w 96 -h 96 launcher_icon.svg
inkscape -e build/launcher_icon-xxhdpi.png -a 15:15:93:93 -w 144 -h 144 launcher_icon.svg
inkscape -e build/launcher_icon-xxxhdpi.png -a 15:15:93:93 -w 192 -h 192 launcher_icon.svg

cp launcher_icon.svg build/launcher_icon-web.svg
inkscape --select=circle --verb=RemoveFilter --verb=FileSave --verb=FileQuit build/launcher_icon-web.svg
inkscape -e build/launcher_icon-web.png -a 18:18:90:90 -w 512 -h 512 build/launcher_icon-web.svg
rm build/launcher_icon-web.svg

cp launcher_icon.svg build/launcher_icon_foreground.svg
inkscape --select=circle --select=circle-edge-top --select=circle-edge-bottom --verb=EditDelete --select=circle-clip-group --verb=SelectionUnGroup --verb=FileSave --verb=FileQuit build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon_foreground-mdpi.png -w 108 -h 108 build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon_foreground-hdpi.png -w 162 -h 162 build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon_foreground-xhdpi.png -w 216 -h 216 build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon_foreground-xxhdpi.png -w 324 -h 324 build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon_foreground-xxxhdpi.png -w 432 -h 432 build/launcher_icon_foreground.svg
inkscape -e build/launcher_icon-play.png -a 18:18:90:90 -b '#4caf50' -w 512 -h 512 build/launcher_icon_foreground.svg
rm build/launcher_icon_foreground.svg
