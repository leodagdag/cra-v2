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

		});
		//$httpProvider.defaults.headers.common['Cache-Control'] = 'no-store';
		//$httpProvider.defaults.headers.common['Pragma'] = 'no-cache';
	}]);


var app = angular.module('app', ['ngResource', 'httpInterceptorServiceProvider',
	'bDatepicker', 'craLegendDirective', 'craToolbarDirective',
	'dayOfMonthFilter', 'dayOfWeekFilter', 'momentOfDayFilter', 'monthOfYearFilter', 'capitalizeFilter', 'localTimeFilter', 'kilometerFilter']);


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
