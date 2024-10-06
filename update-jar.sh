#!/bin/bash

jarfile="chainsaw.jar"
destination="/Users/marco/Desktop/Server/plugins"

echo "Building the project..."
mvn package
if [ $? -ne 0 ]; then
    echo "Maven build failed! Exiting..."
    exit 1
fi

echo
echo "Copying $jarfile to $destination"

# Copy new jar
echo "Copying new jar..."
cp -f "target/$jarfile" "$destination"

sh /Users/marco/Desktop/AWS/upload_plugin_google-home.sh

echo "Done."
