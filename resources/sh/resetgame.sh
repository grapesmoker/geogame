#!/bin/bash

cd /home/selenium/dist

# Note, here is where we add killing old Xvfb and starting a new one,
# and then setting our display to point to it.
# ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill
# ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill -9
#
# Xvfb :$UID -screen 0 1280x1024x24  &
# export DISPLAY=:$UID

# echo $DISPLAY

java -jar GeoGameTester.jar --reset test_admin test_admin $* >& ~/geogametester.log 


