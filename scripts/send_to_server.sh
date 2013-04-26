#!/bin/bash
echo "--> Send to server"
scp $HOME/build/cra-v2/dist/*.zip genesis@cra.genesis-groupe.com:/home/genesis/releases/

