cd /home/proj1/project1
mkdir bin
javac -nowarn -d bin  -cp RMI pingPongTest/*.java
java -cp bin:RMI pingPongTest.PingPongTestServer