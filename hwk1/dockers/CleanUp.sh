#clean up the messy
#stop docker
docker stop myserver
docker stop myclient
docker stop myvolume

#remove docker 
docker rm myserver
docker rm myclient
docker rm myvolume

#remove network
docker network rm my-bridge-network