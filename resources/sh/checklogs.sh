#!/bin/bash

wc -l /home/selen???/geogametester.log

for f in /home/selen???/geogametester.log
do
  echo $f
  tail -1 $f
done