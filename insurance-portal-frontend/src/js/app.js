/**
 * PhoneGap application JS
 * This does not include the Cordova JS itself, that is included in the
 * header. This will always include at the footer of the main template
 *
 * https://www.npmjs.com/package/angular-crypto-js
 */
'use strict';
var angular = require('angular'),
    angularI18n = require('angular-i18n/angular-locale_en-gb'),
    angularUiBootstrap = require('angular-ui-bootstrap'),
    angularAnimate = require('angular-animate'),
    angularTouch = require('angular-touch'),
    angularMessages = require('angular-messages'),
    angularSlider = require('angularjs-slider');

window.CryptoJS = {};
window.CryptoJS.MD5 = require('crypto-js/md5');

/**
 * Cordova hooks
 */
window.app = {
	// Application Constructor
	initialize: function() {
		this.bindEvents();
	},

	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
		document.addEventListener('online', function() {
			var $scope = angular.element(document.getElementsByTagName('body')[0]).scope();
			$scope.$apply(function() {
				$scope.wentOnline();
			});
		}, false);
	}
};

/**
 * Localisation - Temp, better to use proper Angular
 * localisation/internationalisation
 */
window.DATE_FORMAT = 'yyyy-MM-dd';

/**
 * Angular modules
 */
var debugEnabled = true;
var appStorageKey = 'app';

/**
 * Angular main
 */
angular.module('phonegap', ['ui.bootstrap', 'ngAnimate', 'ngTouch', 'ngMessages', 'rzModule'])
/**
 * Config for the module
 */
.config(['$compileProvider', '$logProvider', function ($compileProvider, $logProvider) {
	$compileProvider.debugInfoEnabled(debugEnabled);
	$logProvider.debugEnabled(debugEnabled);
}])

/**
 * Filter for counting the number of keys in an object
 */
.filter('keyLength', function() {
	return function(object) {
		if (undefined == object || typeof object !== 'object') return undefined;
		return Object.keys(object).length;
	};
})

/**
 * Create a factory for functions common to multiple controllers
 */
.service('BootstrapService', ['$log', function ($log) {
	/**
	 * Link to bootstrap's validation
	 */
	// FIXME: this doesn't work for radio buttons because the actual field is hidden.
	this.getValidationState = function(form, fieldName) {
		if (!(fieldName in form)) {
			$log.warn('Invalid field name: '+fieldName);
			return '';
		}
		if (form[fieldName].$touched || form.$submitted) {
			return form[fieldName].$valid?'has-success':'has-error';
		}
		return '';
	};
	this.getGlyphState = function(form, fieldName, options) {
		if (!(fieldName in form)) {
			$log.warn('Invalid field name: '+fieldName);
			return '';
		}
		if (form[fieldName].$touched || form.$submitted) {
			return form[fieldName].$valid?'glyphicon-ok':'glyphicon-remove';
		}
		return options.required?'glyphicon-pencil':'';
	};
	this.getAriaState = function(form, fieldName, options) {
		if (!(fieldName in form)) {
			$log.warn('Invalid field name: '+fieldName);
			return '';
		}
		// TODO: if there is a mandatory error, show that first
		// TODO: reference the existing errors?
		if (form[fieldName].$touched || form.$submitted) {
			return form[fieldName].$valid?'has-success-desc':'has-error-desc';
		}
		return options.required?'has-mandatory-desc':'';
	};
}])

/**
 * Create a factory for functions common to multiple controllers
 */
.service('SupportService', ['$log', '$window', function ($log, $window) {
	/**
	 * Generate a unique form id. This requires unique formname/input name combination
	 */
	this.generateId = function(form, name) {
		var id = form.$name+'.'+name;
		$log.debug('Generating id for '+id);
		return $window.CryptoJS.MD5(id).toString();
	};

	/**
	 * parse an options object for formatters/parsers
	 */
	this.linkModel = function(scope, ngModelCtrl, debug) {
		// Binding functions
		ngModelCtrl.$formatters.push(function(modelValue) {
			$log.debug('Default formatter called');
			return { val: modelValue };
		});
		ngModelCtrl.$parsers.push(function(viewValue) {
			$log.debug('Default parser called');
			return viewValue.val;
		});
		scope.$watch('val', function() {
			$log.debug('Watch called');
			ngModelCtrl.$setViewValue({val: scope.val});
		});
		ngModelCtrl.$render = function() {
			$log.debug('Render called');
			scope.val = (ngModelCtrl.$viewValue || {}).val;
		};
		// TODO: link the validators from here
		/*
		if (undefined != scope.options) {
			if (undefined != scope.options.formatter) {
				ngModel.$formatters.push(scope.options.formatter);
			}
			if (undefined != scope.options.parser) {
				ngModel.$parsers.push(scope.options.parser);
			}
		}*/

	};

}])

/**
 * Forms manager
 */
.directive('formInput', ['$log', 'SupportService', 'BootstrapService', function($log, SupportService, BootstrapService) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options'
		},
		templateUrl: 'formBasicInput',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);
		}
	};
}])

.directive('formDate', ['$log', '$window', 'SupportService', 'BootstrapService', function($log, $window, SupportService, BootstrapService) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options'
		},
		templateUrl: 'formDateInput',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);

			scope.opened = false;
			scope.togglePopup = function() {
				scope.opened = true;
			};
			scope.dateFormat = $window.DATE_FORMAT;
		}
	};
}])

// FIXME: refer to the docs about not refreshing on first load
.directive('formRange', ['$log', '$window', 'SupportService', 'BootstrapService', function($log, $window, SupportService, BootstrapService) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options'
		},
		templateUrl: 'formRangeInput',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);
		}
	};
}])

.directive('formRadio', ['$log', 'SupportService', 'BootstrapService', function($log, SupportService, BootstrapService) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options'
		},
		templateUrl: 'formRadioButton',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);
		}
	};
}])


.directive('formButton', ['$window', function($window) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options',
			action: '&action',
			// FIXME: disabled should be event/model driven
			disabled: '<stateDisabled'
		},
		templateUrl: 'formBasicButton',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);
		}
	};
}])

/**
 * A label. This uses filters to display the model value in the correct format
 * TODO: Allow multiple dynamic filters to format for a currency, and also do rounding at the quotation stage.
 */
.directive('formLabel', ['$log', 'SupportService', 'BootstrapService', function($log, SupportService, BootstrapService) {
	return {
		replace: true,
		require: ['ngModel'],
		restrict: 'E',
		scope: {
			name: '@name',
			form: '<form',
			options: '<options'
		},
		templateUrl: 'formLabel',
		transclude: true,
		link: function(scope, element, attrs, controllers) {
			var ngModelCtrl = controllers[0];

			scope.id = SupportService.generateId(scope.form, scope.name);
			scope.bs = BootstrapService;
			SupportService.linkModel(scope, ngModelCtrl);
		}
	};
}])

.controller('page', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	var storage = window.localStorage;
	$scope.isNavCollapsed = true;
	$scope.alerts = [];

	$scope.addAlert = function(message, type) {
		$scope.alerts.unshift({when: Date.now(), type: type, msg: message});
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};

	/**
	 * This is the important function that decides what to do when the app
	 * comes online. This may depend on what page it is etc.
	 */
	$scope.wentOnline = function() {
		var apps = parseInt(storage.getItem(appStorageKey));
		if (apps > 0) {
			$scope.addAlert('Online - synced '+apps+' applications', 'success');
			storage.setItem(appStorageKey, 0);
		}
	};

	$scope.$on('alert', function(event, args) {
		$scope.addAlert(args.message, args.type||undefined);
	});

}])

.controller('eg', ['$scope', '$element', '$http', '$q', '$timeout', '$window', '$log', 'BootstrapService', function($scope, $element, $http, $q, $timeout, $window, $log, BootstrapService) {
	var storage = window.localStorage;
	$scope.eg = {
		product: 'Product1',
		paymentMode: 'Annual'
	};

	// Temporarily do this to clear at startup
	storage.setItem(appStorageKey, 0);

	var baseUrl = 'http://192.168.1.10:8080/socialtest';
	$scope.updateInProgress = false;
	$scope.updatePremium = function() {
		if ($scope.updateInProgress) return;
		$scope.updateInProgress = true;

		$log.info('Updating premium');
		$http({
			method: 'POST',
			url: baseUrl+'/services/product/calculator/sa',
			data: $scope.eg,
			cache: false
		}).then(function successCallback(response) {
			$log.info('success');
			$log.info(response);
			$scope.updateInProgress = false;
			// FIXME: cannot copy all because we end up with a race overwriting null data
			$scope.eg.firstPremium = response.data.firstPremium;
		}, function errorCallback(response) {
			$log.info('failure');
			$log.info(response);
			$scope.updateInProgress = false;
		});
	};

	// Get an XSRF token - FIXME - get a cookie, but not sending the header
	$http({
		method: 'GET',
		url: baseUrl+'/login',
		cache: false
	}).then(function successCallback(response) {
		$log.info('success');
		$log.info(response);
		$scope.updateInProgress = false;
	}, function errorCallback(response) {
		$log.info('failure');
		$log.info(response);
		$scope.updateInProgress = false;
	});

	// Watch specific items, applying the first premium has a problem otherwise
	$scope.$watchGroup(['eg.gender', 'eg.dateOfBirth', 'eg.sumAssured'], $scope.updatePremium);


	$scope.purchase = function(form, field, $event) {
		if (!form.$valid) {
			$event.stopPropagation();
			$event.preventDefault();
		}
		console.log('Submit');
		console.log(form);
		console.log(eg);

		if (Connection.NONE === navigator.connection.type) {
			var apps = parseInt(storage.getItem(appStorageKey))+1;
			storage.setItem(appStorageKey, apps);
			$scope.$emit('alert', {message:'Offline - saved', type:'danger'});
		} else {
			$scope.$emit('alert', {message:'Submitted', type:'success'});
		}
	};

	$scope.givenNameOptions = {
		label: 'What\'s your given name',
		placeholder: 'Given name',
		type: 'text',
		autocomplete: 'given-name',
		required: true
	};
	$scope.familyNameOptions = {
		label: 'What\'s your family name',
		placeholder: 'Family name',
		type: 'text',
		autocomplete: 'family-name',
		required: true
	};
	$scope.genderOptions = {
		label: 'Are you',
		placeholder: 'Female or Male',
		autocomplete: 'sex',
		required: true,
		members: [
			{
				name: 'Female',
				value: 'female'
				// icon: 'todo',
			},
			{
				name: 'Male',
				value: 'male'
				// icon: 'todo',
			}
		]
	};
	$scope.mobilePhoneOptions = {
		label: 'What\'s your mobile phone number',
		placeholder: '+852 1234 5678',
		type: 'tel',
		autocomplete: 'mobile tel',
		required: true,
		pattern: '(?:\\+\\d{2,3}\s*)?\\d{4}\\s*\\d{4}'
	};
	$scope.dateOfBirthOptions = {
		label: 'What\'s your date of birth',
		placeholder: 'YYYY-MM-DD',
		autocomplete: 'bday',
		required: true
	};

	$scope.sumAssuredOptions = {
		label: 'How much would you like to be covered for',
		placeholder: 'Sum Assured',
		floor: 100000,
		ceil: 1000000,
		step: 1000
		/*
		showTicksValues: false,
		stepsArray: [
			{value: 100000, legend: 'Minimum Cover'},
			{value: 200000},
			{value: 300000},
			{value: 400000},
			{value: 500000, legend: 'National Average'},
			{value: 600000},
			{value: 700000},
			{value: 800000},
			{value: 900000},
			{value: 1000000, legend: 'Excellent Care'}
		]*/
	};
	$scope.eg.sumAssured = 500000;
	
	$scope.firstPremiumOptions = {
		label: 'Your first premium is',
		placeholder: 'No value'
	};

	$scope.pageLoaded=true;
}]);

