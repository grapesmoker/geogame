#!/bin/bash

HOWMANYUSERS=$1

let ind2=2
while [ $ind2 -le $HOWMANYUSERS ] ; do

    timing.sh $ind2
    let ind2=ind2+1

done

