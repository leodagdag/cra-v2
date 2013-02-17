'use strict';

angular.module('authServiceProvider', [])
	.config(['$httpProvider', function($httpProvider) {
		$httpProvider.responseInterceptors.push(function($q, $rootScope, $log) {
			function success(response) {
				return response;
			}

			function error(response) {
				if(response.status === 401) {
					$log.error("401!!!!");
					$rootScope.$broadcast('event:auth-loginRequired');
				}
				return $q.reject(response);
			}

			return function(promise) {
				return promise.then(success, error);
			};

		})
	}]);


var app = angular.module('app', ['ngResource', 'authServiceProvider',
		'bDatepicker', 'craLegendDirective', 'craToolbarDirective',
		'dayOfMonthFilter', 'dayOfWeekFilter', 'momentOfDayFilter', 'monthOfYearFilter', 'capitalizeFilter'])
	.constant('YearsConst', [
		{'id': '1', 'label': '2012'},
		{'id': '2', 'label': '2013'},
		{'id': '3', 'label': '2014'},
		{'id': '4', 'label': '2015'}
	])
	.constant('MonthsConst', [
		{'id': '1', 'label': _.str.capitalize(moment.months[0])},
		{'id': '2', 'label': _.str.capitalize(moment.months[1])},
		{'id': '3', 'label': _.str.capitalize(moment.months[2])},
		{'id': '4', 'label': _.str.capitalize(moment.months[3])},
		{'id': '5', 'label': _.str.capitalize(moment.months[4])},
		{'id': '6', 'label': _.str.capitalize(moment.months[5])},
		{'id': '7', 'label': _.str.capitalize(moment.months[6])},
		{'id': '8', 'label': _.str.capitalize(moment.months[7])},
		{'id': '9', 'label': _.str.capitalize(moment.months[8])},
		{'id': '10', 'label': _.str.capitalize(moment.months[9])},
		{'id': '11', 'label': _.str.capitalize(moment.months[10])},
		{'id': '12', 'label': _.str.capitalize(moment.months[11])}
	]);
app.config(['$routeProvider',
	function($routeProvider) {
		$routeProvider
			.when("/cra/:username/:year/:month", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra/:username", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra/:year/:month", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra/:username/:year/:month/:days", {
				templateUrl: "public/html/views/cra/day.html",
				controller: 'DayCtrl'
			})
			.when("/absence", {
				templateUrl: "public/html/views/absence.html",
				controller: 'AbsenceCtrl'
			})
			.when("/claim", {
				templateUrl: "public/html/views/claim.html",
				controller: 'ClaimCtrl'
			})
			.when("/remuneration", {
				templateUrl: "public/html/views/remuneration.html",
				controller: 'RemunerationCtrl'
			})
			.when("/parameter", {
				templateUrl: "public/html/views/parameter.html",
				controller: 'ParameterCtrl'
			})
			.when("/backoffice", {
				templateUrl: "public/html/views/back-office.html",
				controller: 'BackOfficeCtrl'
			})
			.when("/my-account", {
				templateUrl: "public/html/views/my-account/my-account.html",
				controller: 'MyAccountCtrl'
			})
			.when("/my-account/:subSection", {
				templateUrl: "public/html/views/my-account/my-account.html",
				controller: 'MyAccountCtrl'
			});
	}]);


app.run(['$rootScope', '$http', '$log',
	function($rootScope, $http, $log) {
		$rootScope.Roles = {
			'EMPLOYEE': 'employee',
			'PRODUCTION': 'production',
			'ADMIN': 'admin'
		};

		var route = jsRoutes.controllers.Authentication.profile();
		$http({
			method: route.method,
			url: route.url
		})
			.success(function(profile, status, headers, config) {
				$log.log('profile', profile);
				$rootScope.profile = profile;
			});
	}]);

/* based on https://github.com/bleporini/angular-authent */
app.directive('authenticator', ['$location', '$window',
	function($location, $window) {
		return function(scope, elem, attrs) {
			scope.$on('event:auth-loginRequired', function() {
				$window.location.href = "/logout";
			})
		};
	}]);


