#! /usr/bin/env bash

set -v

eval $(docker-machine env default)

docker rm -f ${PWD##*/}_travellerdebug_1
docker rmi -f ${PWD##*/}_travellerdebug

docker-compose up -d travellerdebug
sleep 60