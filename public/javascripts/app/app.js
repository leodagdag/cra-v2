'use strict';

var app = angular.module("app", ['ngResource', 'authServiceProvider',
	'bDatepicker', 'craLegendDirective', 'craToolbarDirective',
	'dayOfMonthFilter', 'weekDayFilter', 'momentDayFilter', 'monthOfYearFilter', 'capitalizeFilter']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider
		.when("/cra", {
			templateUrl: "public/views/cra.html",
			controller: 'CraCtrl'
		})
		.when("/absence", {
			templateUrl: "public/views/absence.html",
			controller: 'AbsenceCtrl'
		})
		.when("/claim", {
			templateUrl: "public/views/claim.html",
			controller: 'ClaimCtrl'
		})
		.when("/remuneration", {
			templateUrl: "public/views/remuneration.html",
			controller: 'RemunerationCtrl'
		})
		.when("/parameter", {
			templateUrl: "public/views/parameter.html",
			controller: 'ParameterCtrl'
		})
		.when("/backoffice", {
			templateUrl: "public/views/back-office.html",
			controller: 'BackOfficeCtrl'
		})
		.when("/my-account", {
			templateUrl: "public/views/my-account/my-account.html",
			controller: 'MyAccountCtrl'
		})
		.when("/my-account/:subSection", {
			templateUrl: "public/views/my-account/my-account.html",
			controller: 'MyAccountCtrl'
		});

}]);

/* based on https://github.com/bleporini/angular-authent */
app.directive('authenticator', ['$location', '$window', function($location, $window) {
	return function(scope, elem, attrs) {
		scope.$on('event:auth-loginRequired', function() {
			$window.location.href = "/logout";
		})
	};
}]);

/* http://stackoverflow.com/questions/12863663/angularjs-complex-nesting-of-partials-and-templates */
/*
app.directive('subNav', function(){
	return {
		restrict: 'E',
		scope: {
			current: '=current'
		},
		templateUrl: 'mySubNav.html',
		controller: function($scope) {
		}
	};
});
*/
angular.module('authServiceProvider', []).
	config(['$httpProvider', function($httpProvider) {

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
