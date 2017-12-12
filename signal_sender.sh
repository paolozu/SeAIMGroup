#!/bin/bash

send_signal(){
  curl -H "Content-Type: application/json" --data @robot.json 127.0.0.1:8000
}

while [[ $INPUT != "stop" ]]
do
  send_signal
done
