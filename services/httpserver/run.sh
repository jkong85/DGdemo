#!/bin/sh
set -x
sudo docker rm -f dghttpserver
sleep 5
sudo mvn clean install -Pdocker-build-image
sleep 5
#sudo docker run -it --name dghttpserver dg-httpserver:latest -- /bin/sh

