#start Server
docker exec -d  myserver bash '/opt/hw1/runServer.sh'
docker exec -t  myclient bash '/opt/hw1/runClient.sh'