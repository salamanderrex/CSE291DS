#!/bin/bash
if [ !  $# == 1 ]; then
	echo "give me a id, please"
	exit
fi 
echo "$1"
java -cp bin:RMI pingPongTest.PingPongTestClient "$1"