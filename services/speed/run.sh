#!/bin/sh
set -x
sudo docker rm -f dgspeed
sleep 5
sudo mvn clean install -Pdocker-build-image
sleep 5
#sudo docker run -it --name dgspeed dg-speed:latest -- /bin/sh
