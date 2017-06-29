/**
 * Gulp build file for clean tasks
 */

var del = require('del');

module.exports = function (gulp, opts) {
	/**
	 * Base clean task, clean everything
	 */
	gulp.task('clean', function(done) {
		del([ opts.destDir ], { force: true });
		done();
	});
};
