#!/bin/bash
cd /home/proj1/project1
#if [ !  $# == 1 ]; then
#	echo "give me a id, please"
#	exit
#fi 
echo "en.. your luck number is $1"
echo "let me ping it to server........"
javac -nowarn -d  bin  -cp RMI pingPongTest/*.java
java -cp bin:RMI pingPongTest.PingPongTestClient "$1"