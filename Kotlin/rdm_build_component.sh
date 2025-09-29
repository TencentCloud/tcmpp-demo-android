#!/bin/sh
rm -rf ./bin
mkdir ./bin

# Build
chmod 777 gradlew

echo "$1=$1"

case $1 in
	true)
		./gradlew :app:assembleDebug --stacktrace --refresh-dependencies
		;;
	false)
		./gradlew :app:assembleRelease --stacktrace --refresh-dependencies
		;;
esac

cd ..


