cd appDockerfile
docker build -t hw1-server .  
docker build -t hw1-client .  
cd ..
#create data volume
docker build -t hw1-sharedata .
docker run -d -v $PWD/data:/data --name myvolume hw1-sharedata /bin/sh -c "while true; do fake=1; sleep 1; done"
docker network create -d bridge my-bridge-network

#daemonize docker
#docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myserver hw1-server  /bin/sh -c "while true; do fake=1; sleep 1; done"
#docker run -d --volumes-from myvolume  --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myclient hw1-client /bin/sh -c "while true; do fake=1; sleep 1; done"

#not daemonize by loop
docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myserver hw1-server   bash '/opt/hw1/runServer.sh'
#docker run -d --volumes-from myvolume  --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myclient hw1-client 

#run the server and client
#docker exec -t  myserver bash '/opt/hw1/runServer.sh'
#docker exec -t  myclient bash '/opt/hw1/runClient.sh'


docker ps




