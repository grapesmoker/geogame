#!/bin/bash

TESTUSER_INDICES_1="000 001 002 003 004 005 006 007 008 009 010 011 012 013 014 015 016 017"

for index in $TESTUSER_INDICES_1
do
TESTUSER=selen${index}

ssh $TESTUSER@agiorgitiko.cimds.ri.cmu.edu "/home/selenium/bin/runtester.sh  $TESTUSER $TESTUSER $*  >& ~/geogametester_$TESTUSER.log"  &
sleep 2
done
