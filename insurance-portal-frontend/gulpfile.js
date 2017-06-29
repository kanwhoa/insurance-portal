'use strict';
/**
 * Gulp build file to build a PhoneGap/Cordova localized application
 */
var gulp = require('gulp'),
  runSequence = require('run-sequence');

/**
 * Global options
 */
var opts = {
	srcDir: "src",
	destDir: "phonegap/www"
};

/**
 * Included task files
 */
var cleanTasks = require('./gulp/tasks-clean.js')(gulp, opts),
    developTasks = require('./gulp/tasks-develop.js')(gulp, opts);

/**
 * The default gulp task
 */
gulp.task('default', ['clean'], function() {
	runSequence(
		'phonegap-serve'
	)
});
