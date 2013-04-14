#!/bin/bash
clear
echo "--> Lancement du batch d'envoi des absences"
username="batch"
password="batchPassword"

curl -d "username=${username}&password=${password}" -c cookie http://localhost:9000/auth
sleep 5s
curl -b cookie http://localhost:9000/launch/absences
rm cookie
echo ""
echo "--> Fin du batch d'envoi des absences"