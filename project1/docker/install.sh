docker build -f DockerfileForServerClient -t hw1-server .  
docker build -f DockerfileForServerClient -t hw1-client .  

docker build -t hw1-sharedata .

docker run -d  --name myvolume hw1-sharedata /bin/sh -c "while true; do fake=1; sleep 1; done"
docker network create -d bridge my-bridge-network

#daemonize docker
docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/../src/:/home/RMI/ --name myserver hw1-server  /bin/sh -c "while true; do fake=1; sleep 1; done"
docker run -d --volumes-from myvolume  --net=my-bridge-network -v $PWD/../src/:/home/RMI/ --name myclient hw1-client /bin/sh -c "while true; do fake=1; sleep 1; done"


docker ps




