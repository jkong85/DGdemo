#!/bin/sh

sleep 2

ifconfig eth0 | awk '/inet addr/{print substr($2,6)}' >> ipaddress

sleep 1

java -jar /opt/dg/starter.jar

