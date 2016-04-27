cd /home/proj1/project1
javac -nowarn -d bin  -cp RMI pingPongTest/*.java
java -cp bin:RMI pingPongTest.PingPongTestServer