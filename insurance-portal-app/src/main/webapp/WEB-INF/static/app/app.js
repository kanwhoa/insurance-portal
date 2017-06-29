/**
 * Application JavaScript
 */
var debugEnabled = true;
var serviceBaseUrl = '/socialtest/services';

/**
 * User related module
 */
angular.module('user', ['ui.bootstrap', 'ngAnimate'])

/**
 * Config for the module
 */
.config(['$compileProvider', '$logProvider', function ($compileProvider, $logProvider) {
  $compileProvider.debugInfoEnabled(debugEnabled);
  $logProvider.debugEnabled(debugEnabled);
}])

/**
 * Create a factory for functions common to multiple controllers
 */
.service('BootstrapService', function () {
	/**
	 * Link to bootstrap's validation
	 */
	this.getValidationState = function (form, field) {
		if (!(field in form)) {
			$log.warn("Invalid field name: "+field);
			return '';
		}
		if (form[field].$touched || form.$submitted) {
			return form[field].$valid?'has-success':'has-error';
		}
		return '';
	};
	this.getGlyphState = function(form, field) {
		if (!(field in form)) {
			$log.warn("Invalid field name: "+field);
			return '';
		}
		form[field].describedby = '';
		if (form[field].$touched || form.$submitted) {
			form[field].describedby = form[field].$valid?'has-success-desc':'has-error-desc';
			return form[field].$valid?'glyphicon-ok':'glyphicon-remove';
		}
		return '';		
	};
})

/**
 * Controller for home page
 */
.controller('home', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;
	$scope.loggedIn = false;

	// Get the user's logged in/out status from the backend
	var deferred = $q.defer();
	$http.get(serviceBaseUrl+'/user/status').
		then(function(response) {
			// Only once we have the user status, display the page
			$scope.loggedIn=true;
			$scope.user = response.data;
			$timeout(function() {
				deferred.resolve();
			}, 50);
		}, function(response) { // errors 
			$timeout(function() {
				deferred.resolve();
			}, 50);
		});
	deferred.promise.then(function() {
		$scope.dataLoaded = true;
	});

	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}])

/**
 * Controller for login pages
 */
.controller('login', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;

	// Default page state
	$scope.login = {};

	// Handler for login
	$scope.save = function(form, data, $event) {
		if (!form.$valid) {
			$event.stopPropagation();
			$event.preventDefault();
		}
	};
	
	// FIXME: call these direct
	$scope.getValidationState = function(form, field) {
		return BootstrapService.getValidationState(form, field);
	};
	$scope.getGlyphState = function(form, field) {
		return BootstrapService.getGlyphState(form, field);
	};
	
	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}])

/**
 * Controller for the logout function on the backend services
 */
.controller('header', ['$scope', '$http', '$window', '$log', function($scope, $http, $window, $log) {
	'use strict';
	
	// Logout function with CSRF
	$scope.logout = function($event) {
		$event.stopPropagation();
		$event.preventDefault();

		$http({
			method: 'POST',
			url: serviceBaseUrl+'/logout',
			data: {}
		}).then(function(response) {
			$window.location.href = response.data;
		}, function(response) {
			// Display the form error.
			$log.warn("Logout failed: "+response);
		});
	};
}])
	
/**
 * Controller for registration pages
 */
.controller('register', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;
	// Default page state
	$scope.dataLoaded = false;
	$scope.account = {};
	
	// FIXME: call these direct
	$scope.getValidationState = function(form, field) {
		return BootstrapService.getValidationState(form, field);
	};
	$scope.getGlyphState = function(form, field) {
		return BootstrapService.getGlyphState(form, field);
	};
	
	// TODO: get the service base URL from somewhere
	$scope.save = function(form, data) {
		if (!form.$valid) return; // do this from inside the function as it will cause $submitted to be true

		$http({
			method: 'POST',
			url: serviceBaseUrl+'/user/register',
			data: data
		}).then(function(response) {
			// User registration was successful, send the user to the post login page identified
			$window.location.href = response.data;
		}, function(response) {
			// No chance for a 401/403 here, not authentication/authorisation required.
			if (400 == response.status) {
				$log.log(response.data);
			} else {
				$log.log(response.data);
				// TODO: set the global form error handler
			}
		});
	};

	// This is tricky. The CSS change takes a short time to apply, but the unhide function calculates
	// the height first, this causes a "bounce" on screen. To avoid, we need to defer the unhide of the
	// registration box until the $apply() has completed from the web service response and the effects have
	// taken. This is REALLY hacky, but seems to work. Maybe a better approach is to have two forms...
	// This seems to be a small enough interval for the setTimeout 0 methods to jump infront
	var deferred = $q.defer();
	$http.get(serviceBaseUrl+'/user/register').
		then(function(response) {
			// Only update the account object. Once the $apply() has occured for that, then
			// set the dataLoaded variable to unhide.
			$scope.account = response.data;
			$timeout(function() {
				deferred.resolve();
			}, 50);
		}, function(response) {
			$timeout(function() {
				deferred.resolve();
			}, 50);
		});
	deferred.promise.then(function() {
		$scope.dataLoaded = true;
	});
	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}])

/**
 * Controller for product registration (bind)
 */
.controller('bind', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;
	// Default page state
	$scope.bind = {};
	
	// FIXME: call these direct
	$scope.getValidationState = function(form, field) {
		return BootstrapService.getValidationState(form, field);
	};
	$scope.getGlyphState = function(form, field) {
		return BootstrapService.getGlyphState(form, field);
	};
	
	// TODO: get the service base URL from somewhere
	$scope.save = function(form, data) {
		if (!form.$valid) return; // do this from inside the function as it will cause $submitted to be true

		$http({
			method: 'POST',
			url: serviceBaseUrl+'/user/bind',
			data: data
		}).then(function(response) {
			$scope.bindSuccess = response.data;
		}, function(response) {
			$scope.bindSuccess = false;
		});
	};

	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}])

/**
 * Controller for Contact Us pages
 */
.controller('contactus', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;
	// Default page state
	$scope.contactus = {};
	
	// FIXME: call these direct
	$scope.getValidationState = function(form, field) {
		return BootstrapService.getValidationState(form, field);
	};
	$scope.getGlyphState = function(form, field) {
		return BootstrapService.getGlyphState(form, field);
	};

	// Submit the form
	$scope.save = function(form, data) {
		if (!form.$valid) return; // do this from inside the function as it will cause $submitted to be true

		$http({
			method: 'POST',
			url: serviceBaseUrl+'/contactus',
			data: data
		}).then(function(response) {
			// User registration was successful, go to the thankyou page.
			$scope.formSubmitted = true;
		}, function(response) {
			// Display the form error.
			$log.log(response);
		});
	};
	
	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}])

/**
 * Controller for home page
 */
.controller('mypolicies', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	'use strict';
	
	// AJAX defaults to include credentials
	$http.defaults.withCredentials = true;
	$scope.loggedIn = false;

	// Get the user's logged in/out status from the backend
	var deferred = $q.defer();
	$http.get(serviceBaseUrl+'/policies/mine').
		then(function(response) {
			// Only once we have the user status, display the page
			$scope.loggedIn=true;
			$scope.policies = response.data;
			$timeout(function() {
				deferred.resolve();
			}, 50);
		}, function(response) { // errors 
			$timeout(function() {
				deferred.resolve();
			}, 50);
		});
	deferred.promise.then(function() {
		$scope.dataLoaded = true;
	});

	// Unhide any of the elements that need expensive initialisation
	$scope.pageLoaded=true;
}]);
