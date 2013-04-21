app.controller('MainCtrl', ['$scope', '$rootScope', '$http', '$log', '$location',
	function MainCtrl($scope, $rootScope, $http, $log, $location) {
		"use strict";
		$rootScope.onSuccess = function (msg) {
			$rootScope.$broadcast('event:success', msg);
		};
		$rootScope.onError = function (msg) {
			$rootScope.$broadcast('event:error', msg);
		};
	}]);

app.controller('AlertCtrl', ['$scope', '$rootScope', '$timeout', '$log',
	function AlertCtrl($scope, $rootScope, $timeout, $log) {
		"use strict";
		$rootScope.errors = [];
		$rootScope.successes = [];

		var closeError = function (ts) {
			close('errors', ts);
		};
		var closeSuccess = function (ts) {
			close('successes', ts);
		};
		var close = function (list, ts) {
			$rootScope[list] = _($rootScope[list])
				.filter(function (alert) {
					return alert.ts !== ts;
				})
				.valueOf();
		};

		$scope.show = function () {
			return $rootScope.errors.length || $rootScope.successes.length;
		};
		$scope.closeError = closeError;
		$scope.closeSuccess = closeSuccess;

		$scope.$on('event:error', function (evt, err) {
			$log.error('Erreur', err);
			var now = moment().valueOf();
			$rootScope.errors.push({
				msg: _(err.data).filter(function (c) {
					return c !== '"';
				}).valueOf().join(''),
				ts: now
			});
			$timeout(function () {
				closeError(now);
			}, 5000, true);
		});

		$scope.$on('event:success', function (evt, msg) {
			var now = moment().valueOf();
			$rootScope.successes.push({
				msg: msg,
				ts: now
			});
			$timeout(function () {
				closeSuccess(now);
			}, 5000, true);
		});
	}]);
