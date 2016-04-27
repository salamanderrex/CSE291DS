#ask an id to sun client 
if [ !  $# == 1 ]; then
	echo "give me a number, please"
	exit
fi 
echo "$1"


docker build -f DockerfileForServerClient -t hw1-server .  
docker build -f DockerfileForServerClient -t hw1-client .  

docker build -t hw1-sharedata .

docker run -d  --name myvolume hw1-sharedata /bin/sh -c "while true; do fake=1; sleep 1; done"
docker network create -d bridge my-bridge-network

#daemonize docker
echo "creating server container for you........."
docker run -d --volumes-from myvolume --net=my-bridge-network -v $PWD/../:/home/proj1/ --name myserver hw1-server  /bin/sh  /home/proj1/project1/startPingPongServer.sh 
echo "creating client container for you........."
docker run -it  --volumes-from myvolume  --net=my-bridge-network -v $PWD/../:/home/proj1/ --name myclient hw1-client /bin/sh /home/proj1/project1/startPingPongClient.sh $1



#docker ps




