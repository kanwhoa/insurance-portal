#!/bin/sh

# Run this script to update dependencies.
NPM=$(which npm)
if [ "x${NPM}" == "x" ]; then
	echo "Install Node & NPM"
	exit 1
fi

echo "Installing / updating Node.js dependencies..."
${NPM} install

