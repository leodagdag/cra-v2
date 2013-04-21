app.controller('ClaimSynthesisCtrl', ['$scope', '$rootScope', '$http', '$log', '$location', '$routeParams', 'MonthsConst', 'profile',
	function ClaimSynthesisCtrl($scope, $rootScope, $http, $log, $location, $routeParams, MonthsConst, profile) {
		'use strict';
		$scope.profile = profile.data;
		$scope._ = _;
		$scope.year = $routeParams.year;
		$scope.month = $routeParams.month;
		$scope.init = function() {
			var route = jsRoutes.controllers.JCras.claimSynthesis($scope.profile.id, $scope.year, $scope.month);
			$http({
				'method': route.method,
				'url': route.url
			})
				.success(function(synthesis, status, headers, config) {
					$scope.header = _(synthesis)
						.keys()
						.sortBy()
						.valueOf();
					$scope.body = {};

					_.forEach($scope.header, function(week) {
						_.forEach(_(synthesis[week]).keys().valueOf(), function(claim) {
							if(!$scope.body[claim]) {
								$scope.body[claim] = {};
							}
							if(!$scope.body[claim][week]) {
								$scope.body[claim][week] = {};
							}
							$scope.body[claim][week] = synthesis[week][claim];
						});
					});

					$scope.claimTypes = _($scope.body)
						.keys()
						.valueOf();
				})
				.error(function(error, status, headers, config) {
					$log.error(error);
				});
		};

		$scope.goCra = function() {
			///cra/:username/:year/:month
			$location.path(_.str.sprintf('/cra/%s/%s/%s', $scope.profile.username, $scope.year, $scope.month));
		};
	}]);
