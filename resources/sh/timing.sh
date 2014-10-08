#!/bin/bash

allkill.sh
cp -r ../owens/NetBeansProjects/GeoGameTester/dist/ .
runsome.sh $*
resetgame.sh --playercount $1
startgame.sh
sleep 620
allkill.sh
collectlogs.sh $1

