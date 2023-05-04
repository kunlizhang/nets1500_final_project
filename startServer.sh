#!/usr/bin/env bash

pkill -f WebServer

cd src
javac WebServer.java

java WebServer 8100 ../servers/exampleServer &
java WebServer 5315 ../servers/nets1500Server &

trap ctrl_c INT

ctrl_c () {
    pkill -f WebServer
    exit
}

while true; do
    echo "Web servers are running..."
    sleep 10
done