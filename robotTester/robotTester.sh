#!/bin/bash

manda_segnale(){
  curl -H "Content-Type: application/json" --data @robot.json //INSERISCI_URL
}

while [[ $INPUT != "stop" ]]
do
  manda_segnale
  sleep 5
done
