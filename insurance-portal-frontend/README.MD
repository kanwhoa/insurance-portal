# PhoneGap/Cordova example application & buid system

## What is this project

This is an example PhoneGap/Cordova project to demonstrate a basic use of
the API and a supporting front-end build system.

## Running

### First time setup

After downloading, you will need to install the appropriate NPM modules. In order to do so, run:

```sh
./bin/update.sh
```

### Test Mode

In order to start the project in streaming mode to a PhoneGap container,
execute 

```sh
./node_modules/gulp/bin/gulp.js
```

Or 

```sh
./node_modules/gulp/bin/gulp.js phongegap-serve
```

This will start a listener on the configured PhoneGap port on your
machine. You can then point a browser or the PhoneGap app container at the
listener to view the output. Note that the output will be non-minified.

### Create a distributable app
TODO

