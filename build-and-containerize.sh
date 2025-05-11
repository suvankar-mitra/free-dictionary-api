#!/bin/bash

# filepath: free-dictionary-api/build-and-containerize.sh

# Exit immediately if a command exits with a non-zero status
set -e

# Define variables
APP_NAME="free-dictionary-api"

# Step 1: Clean and build the application JAR
if [ ! -f "./mvnw" ]; then
  echo "Error: Maven wrapper (mvnw) not found. Please ensure you are in the correct directory."
  exit 1
fi
if [ ! -d "target" ]; then
  echo "Error: Target directory not found. Please ensure the build has been run before."
  exit 1
fi
echo "Building the application JAR..."
./mvnw clean package

# Step 2: Check if the JAR file was created
JAR_FILE=$(ls target | grep 'SNAPSHOT.jar$' | head -n 1)
if [ -z "$JAR_FILE" ]; then
  echo "Error: No JAR file found in target directory."
  exit 1
fi
# Copy the JAR file to a known location
# This is a workaround for the Podman build context issue
cp target/"$JAR_FILE" target/app.jar

# Define image name
IMAGE_NAME="${APP_NAME}:latest"


# Check if Podman is installed
if ! command -v podman &> /dev/null; then
  echo "Podman could not be found. Please install Podman to continue."
  exit 1
fi
# Check if the Podman daemon is running
if ! podman info &> /dev/null; then
  echo "Podman daemon is not running. Please start the Podman daemon to continue."
  exit 1
fi

echo "Application JAR built successfully: $JAR_FILE"

# Step 3: Build the Podman container image
echo "Building the Podman container image..."
podman build -t "$IMAGE_NAME" .

# Step 4: Confirm the image was built
if podman images | grep -q "$APP_NAME"; then
  echo "Podman container image built successfully: $IMAGE_NAME"
else
  echo "Error: Podman container image build failed."
  exit 1
fi
