#!/bin/bash

echo "This script runs the tester without Xvfb so you can watch it operate."
echo "You must use 'ssh -X' to ssh into agiorgitiko with Xwindows forwarding."
echo "This will allow the firefox GUI to display on your home system."

cd /home/selenium/dist

# Note, here is where we add killing old Xvfb and starting a new one,
# and then setting our display to point to it.
ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill
ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill -9


echo $DISPLAY

echo RUNNING COMMAND java -jar GeoGameTester.jar --play $* 

java -jar GeoGameTester.jar --play $* 



