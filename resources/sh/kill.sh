#!/bin/bash

echo $USER killing java processes;
ps uxwwww|fgrep java|fgrep -v fgrep
ps uxwwww|fgrep java|fgrep -v fgrep|cut -c 9-15|xargs kill
ps uxwwww|fgrep java|fgrep -v fgrep|cut -c 9-15|xargs kill -9

echo $USER killing Xvfb processes;
ps uxwwww|fgrep Xvfb|fgrep -v fgrep
ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill
ps uxwwww|fgrep Xvfb|fgrep -v fgrep|cut -c 9-15|xargs kill -9

echo $USER killing firefox processes;
ps uxwwww|fgrep firefox|fgrep -v fgrep
ps uxwwww|fgrep firefox|fgrep -v fgrep|cut -c 9-15|xargs kill
ps uxwwww|fgrep firefox|fgrep -v fgrep|cut -c 9-15|xargs kill -9
