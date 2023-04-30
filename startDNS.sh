#!/usr/bin/env bash

pkill -f DNSServer

cd src
javac DNSServer.java
for FILE in ../configs/*; do
    java DNSServer "$FILE" &
done

trap ctrl_c INT

ctrl_c () {
    pkill -f DNSServer
    exit
}

while true; do
    echo "DNS servers are running..."
    sleep 10
done