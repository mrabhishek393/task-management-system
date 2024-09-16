#!/bin/bash

# Function to check if Kafka is up and running
echo "Waiting for Kafka to be ready..."
while ! echo "exit" | nc kafka1 9092; do sleep 3; done

# List of topics to create
TOPICS=("event-service-dlq", "notifications")  # Add more topics here

for TOPIC in "${TOPICS[@]}"; do
  # Check if the topic already exists
  EXISTING_TOPIC=$(docker exec kafka1 kafka-topics --bootstrap-server kafka1:9092 --list | grep $TOPIC)

  if [ -z "$EXISTING_TOPIC" ]; then
    echo "Creating Kafka topic: $TOPIC"
    docker exec kafka1 kafka-topics --bootstrap-server kafka1:9092 --create --topic $TOPIC --partitions 3 --replication-factor 1
  else
    echo "Kafka topic $TOPIC already exists"
  fi
done