#!/bin/sh
set -x
sudo docker rm -f dgserver
sleep 2
sudo mvn clean install -Pdocker-build-image
sleep 2
#sudo mvn clean install -Pdocker-push-image
sudo docker run -it --name dgserver dg-server:latest
#mvn -Ddocker.username=jkong85 -Ddocker.password=19851208 docker:push


# For docker push
#docker login
#docker push jkong85/dg-speed
