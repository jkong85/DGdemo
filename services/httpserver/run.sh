#!/bin/sh
set -x
sudo docker rm -f dgserver
sleep 5 
sudo mvn clean install -Pdocker-build-image
sleep 5
sudo docker run -it --name dgserver dg-httpserver:latest -- /bin/sh
