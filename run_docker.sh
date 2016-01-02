#! /usr/bin/env bash

set -v

eval $(docker-machine env dev)

docker-compose up -d --x-smart-recreate travellerdebug
sleep 60