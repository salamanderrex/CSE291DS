#cd appDockerfile
docker build -f DockerfileForServerClient -t hw1-server .  
docker build -f DockerfileForServerClient -t hw1-client .  
#cd ..
#create data volume
docker build -t hw1-sharedata .
#docker run -d -v $PWD/data:/data --name myvolume hw1-sharedata /bin/sh -c "while true; do fake=1; sleep 1; done"
docker run -d  --name myvolume hw1-sharedata /bin/sh -c "while true; do fake=1; sleep 1; done"
docker network create -d bridge my-bridge-network

#daemonize docker
#docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myserver hw1-server  /bin/sh -c "while true; do fake=1; sleep 1; done"
#docker run -d --volumes-from myvolume  --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myclient hw1-client /bin/sh -c "while true; do fake=1; sleep 1; done"

#not daemonize by loop
#docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myserver hw1-server   bash '/opt/hw1/runServer.sh'
docker run -t --volumes-from myvolume  --net=my-bridge-network  --name myserver hw1-server   java -cp /opt/hw1/bin com.p0.catserver /data/string.txt  2222 
#docker run -d --volumes-from myvolume  --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myclient hw1-client 

#run the server and client
#docker exec -t  myserver bash '/opt/hw1/runServer.sh'
#docker exec -t  myclient bash '/opt/hw1/runClient.sh'


docker ps




