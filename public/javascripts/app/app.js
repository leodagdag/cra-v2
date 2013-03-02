'use strict';

angular.module('httpInterceptorServiceProvider', [])
	.config(['$httpProvider', function($httpProvider) {
		$httpProvider.responseInterceptors.push(function($q, $rootScope, $log) {
			function success(response) {
				return response;
			}

			function error(response) {
				switch(response.status) {
					case 401:
						$log.error("401!!!!");
						$rootScope.$broadcast('event:auth-loginRequired');
						break;
					case 500:
						$rootScope.$broadcast('event:alert', response);
						break;
				}
				return $q.reject(response);
			}

			return function(promise) {
				return promise.then(success, error);
			};

		})
	}]);


var app = angular.module('app', ['ngResource', 'httpInterceptorServiceProvider',
		'ui.bootstrap',
		'bDatepicker', 'craLegendDirective', 'craToolbarDirective', 'craCheckDayDirective', 'craCheckWeekDirective',
		'dayOfMonthFilter', 'dayOfWeekFilter', 'momentOfDayFilter', 'monthOfYearFilter', 'capitalizeFilter', 'localTimeFilter'])
	.constant('YearsConst', [
		{'id': '1', 'label': '2012'},
		{'id': '2', 'label': '2013'},
		{'id': '3', 'label': '2014'},
		{'id': '4', 'label': '2015'}
	])
	.constant('MonthsConst', [
		{'id': '1', 'label': _.str.capitalize(moment('1', 'MM').format('MMMM'))},
		{'id': '2', 'label': _.str.capitalize(moment('2', 'MM').format('MMMM'))},
		{'id': '3', 'label': _.str.capitalize(moment('3', 'MM').format('MMMM'))},
		{'id': '4', 'label': _.str.capitalize(moment('4', 'MM').format('MMMM'))},
		{'id': '5', 'label': _.str.capitalize(moment('5', 'MM').format('MMMM'))},
		{'id': '6', 'label': _.str.capitalize(moment('6', 'MM').format('MMMM'))},
		{'id': '7', 'label': _.str.capitalize(moment('7', 'MM').format('MMMM'))},
		{'id': '8', 'label': _.str.capitalize(moment('8', 'MM').format('MMMM'))},
		{'id': '9', 'label': _.str.capitalize(moment('9', 'MM').format('MMMM'))},
		{'id': '10', 'label': _.str.capitalize(moment('10', 'MM').format('MMMM'))},
		{'id': '11', 'label': _.str.capitalize(moment('11', 'MM').format('MMMM'))},
		{'id': '12', 'label': _.str.capitalize(moment('12', 'MM').format('MMMM'))}
	])
	.constant('RolesConst', {
		'EMPLOYEE': 'employee',
		'PRODUCTION': 'production',
		'ADMIN': 'admin'
	})
	.constant('ClaimTypeConst', [
		{code: 'TAXI', label: 'Taxi'},
		{code: 'PARKING', label: 'Parking'},
		{code: 'PEAGE', label: 'Péage'},
		{code: 'LOCATION', label: 'Location de voiture'}
	]);


/* based on https://github.com/bleporini/angular-authent */
app.directive('authenticator', ['$location', '$window',
	function($location, $window) {
		return function(scope, elem, attrs) {
			scope.$on('event:auth-loginRequired', function() {
				$window.location.href = "/logout";
			})
		};
	}]);

app.controller('AlertCtrl', ['$scope', '$rootScope', '$timeout', '$log',
	function($scope, $rootScope, $timeout, $log) {
		$rootScope.alerts = [];

		var close = function(ts) {
			$rootScope.alerts = _($rootScope.alerts)
				.filter(function(alert) {
					return alert.ts != ts
				})
				.valueOf();
		};

		$scope.close = close;

		$scope.$on('event:alert', function(res, err) {
			$log.error('Erreur', err);
			var now = moment().valueOf();
			$rootScope.alerts.push(
				{msg: err.data, ts: now}
			);
			$timeout(function() {
				close(now)
			}, 5000, true);
		});
	}]);
