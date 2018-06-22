#!/bin/sh

ifconfig eth0 | awk '/inet addr/{print substr($2,6)}' >> ipaddress

