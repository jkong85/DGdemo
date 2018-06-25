#!/bin/sh

sleep 3

ifconfig eth0 | awk '/inet addr/{print substr($2,6)}' >> ipaddress

#sleep 3
#java -jar /opt/dg/starter.jar

