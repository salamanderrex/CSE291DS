#run the server and client
docker run -t --volumes-from myvolume  --net=my-bridge-network -v $PWD/hw1:/opt/hw1/ --name myclient hw1-client  bash '/opt/hw1/runClient.sh'