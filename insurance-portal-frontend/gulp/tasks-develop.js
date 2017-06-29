/**
 * Gulp build file for development time tasks
 */

var nunjucksRender = require('gulp-nunjucks-render'),
    sass = require('gulp-sass'),
    moduleImporter = require('sass-module-importer'),
    eslintify = require('eslintify'),
    watchifyBrowserify = require('gulp-watchify-browserify'),
    plumber = require('gulp-plumber'),
    filesExist = require('files-exist'),
    sourcemaps = require('gulp-sourcemaps'),
    addsrc = require('gulp-add-src'),
    concat = require('gulp-concat');

/*
 * TODO: Favicons: https://www.npmjs.com/package/gulp-favicons
 * TODO: HTML5 validator/lint & cleaner (in that order)
 */

/**
 * Nunjucks filters and globals
 */
var manageEnvironment = function(environment) {
	var globalKey = 'controllerName';
	environment.addFilter('setController', function(controllerName) {
		environment.addGlobal(globalKey, controllerName);
		return controllerName;
	});
	environment.addFilter('getController', function(str) {
		var controllerName = environment.getGlobal(globalKey);
		return ((undefined!=controllerName)?controllerName:'')+(undefined!=str?str:'');
	});
}

// Export the tasks
module.exports = function (gulp, opts) {
	/**
	 * Template all of the HTML
	 * TODO: repeat the template for each language pattern
	 * TODO: turn this into a watcher
	 * TODO: add data
	 * https://zellwk.com/blog/nunjucks-with-gulp/
	 * https://mozilla.github.io/nunjucks/templating.html
	 */
	gulp.task('template-html', function() {
		return gulp.src(opts.srcDir+'/html/**/*.njk')
			.pipe(nunjucksRender({
				path: [opts.srcDir+'/templates'],
				envOptions: {
					tags: {
						blockStart: '<%',
						blockEnd: '%>',
						variableStart: '<$',
						variableEnd: '$>',
						commentStart: '<#',
						commentEnd: '#>'
					}
				},
				manageEnv: manageEnvironment
			}))
			.pipe(gulp.dest(opts.destDir));
	});

	/**
	 * Merge all of the CSS. Since this is compile time, no minification
	 * TODO: compress on live:
	 * https://www.npmjs.com/package/gulp-clean-css
	 * TODO: Fix the sourcemaps, they seem to not separate the files anymore
	 */
	gulp.task('compile-css', function() {
		return gulp.src(filesExist([opts.srcDir+'/scss/app.scss']))
			.pipe(sass({importer: moduleImporter()}).on('error', sass.logError))
			.pipe(addsrc.append('node_modules/angular/angular-csp.css'))
			.pipe(sourcemaps.init())
			.pipe(concat('app.css'))
			.pipe(sourcemaps.write('.', {includeContent: true, sourceRoot: '.'}))
			.pipe(gulp.dest(opts.destDir+'/css'));
	});

	/**
	 * Merge all of the JS, both from the application and the
	 * dependencies.
	 * TODO: uglify:
	 * https://github.com/simbo/gulp-watchify-browserify/blob/master/demo/gulpfile.js
	 */
	gulp.task('compile-js', function(done) {
		var src = opts.srcDir+'/js',
			dest = opts.destDir+'/js';

		var options = {
			watch: false, // using the gaulp watcher to feed
			cwd: src,
			browserify: {
				paths: [
					'node_modules'
				],
				debug: true,
				transform: [
				// In live mode, put a uglify call in here to minify 
					eslintify
				]
			}
		};

		function streamHandler(stream) {
			return stream
				.pipe(plumber())
				.pipe(sourcemaps.init({loadMaps: true}))
				.pipe(sourcemaps.write('.', {includeContent: true, sourceRoot: '.'}))
				.pipe(gulp.dest(dest));
		}

		watchifyBrowserify('app.js', options, streamHandler.bind(this), done);
	});

	/**
	 * Copy fonts to the output directory
	 */
	gulp.task('copy-fonts', function(done) {
		return gulp.src(['node_modules/font-awesome/fonts/*', 'node_modules/bootstrap-sass/assets/fonts/**/*'])
			.pipe(gulp.dest(opts.destDir+'/fonts'));
	});

	/**
	 * Copy images to the output directory
	 * TODO: SVG spritemaps
	 * TODO: Preflight images
	 * TODO: App resources
	 */
	gulp.task('copy-images', function(done) {
		return gulp.src([opts.srcDir+'/img/**/*png'])
			.pipe(gulp.dest(opts.destDir+'/img'));
		done();
	});

	/**
	 * Base build task. Run once then watch.
	 */
	gulp.task('phonegap-serve', ['template-html', 'compile-js', 'compile-css', 'copy-fonts', 'copy-images'], function(done) {
		gulp.watch([opts.srcDir+'/html/**/*.njk', opts.srcDir+'/templates/**/*.njk'], ['template-html']);
		gulp.watch([opts.srcDir+'/js/**/*.js'], ['compile-js']);
		gulp.watch([opts.srcDir+'/img/**/*.png'], ['copy-images']);
		gulp.watch([opts.srcDir+'/scss/**/*.scss'], ['compile-css']);
	});
};
