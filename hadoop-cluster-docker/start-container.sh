#!/bin/bash

# run N slave containers
N=$1

# the defaut node number is 5
if [ $# = 0 ]
then
	N=5
fi


# delete old master container and start new master container
docker pull jiaxiangchen/hadoop-master
docker pull jiaxiangchen/hadoop-slave
docker rm -f master &> /dev/null
echo "start master container..."
docker run -v $PWD/jar/:/root/jar/ -d -t --dns 127.0.0.1 -P --name master -h master.tuan.com -w /root jiaxiangchen/hadoop-master &> /dev/null

# get the IP address of master container
FIRST_IP=$(docker inspect --format="{{.NetworkSettings.IPAddress}}" master)

# delete old slave containers and start new slave containers
i=1
while [ $i -lt $N ]
do
	docker rm -f slave$i &> /dev/null
	echo "start slave$i container..."
	docker run -d -t --dns 127.0.0.1 -P --name slave$i -h slave$i.tuan.com -e JOIN_IP=$FIRST_IP jiaxiangchen/hadoop-slave &> /dev/null
	i=$(( $i + 1 ))
done


# create a new Bash session in the master container
docker exec -it master bash
