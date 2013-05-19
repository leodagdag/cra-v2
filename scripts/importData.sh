#!/bin/bash
clear
echo "--> Lancement du batch d'import"
username="batch"
password="batchPassword"
curl -d "username=${username}&password=${password}" -c cookie http://localhost:9000/auth
sleep 1s
echo "Users..."
curl -b cookie -i -F file=@../data/managers.csv http://localhost:9000/import/users
curl -b cookie -i -F file=@../data/collaborateurs.csv http://localhost:9000/import/users
echo "Customers..."
curl -b cookie -i -F file=@../data/customers.csv http://localhost:9000/import/customers
echo "Missions..."
curl -b cookie -i -F file=@../data/missions.csv http://localhost:9000/import/missions
rm cookie
echo ""
echo "--> Fin du batch d'import"
