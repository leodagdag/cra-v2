app.controller('ClaimSynthesisCtrl', ['$scope', '$rootScope', '$http', '$log', '$location', '$routeParams', 'MonthsConst', 'profile',
	function ClaimSynthesisCtrl($scope, $rootScope, $http, $log, $location, $routeParams, MonthsConst, profile) {
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
					$log.debug('synthesis', synthesis);
					$scope.header = _(synthesis)
						.keys()
						.sortBy()
						.valueOf();
					$scope.body = [];

					_.forEach($scope.header, function(week) {
						_.forEach(_(synthesis[week]).keys().valueOf(), function(claim) {
							if(!$scope.body[claim]) {
								$scope.body[claim] = [];
							}
							if(!$scope.body[claim][week]) {
								$scope.body[claim][week] = [];
							}
							$scope.body[claim][week].push(synthesis[week][claim]);
						});
					});

					$log.debug('$scope.body', $scope.body);

				})
				.error(function(error, status, headers, config) {
					$log.error(error);
				});
		}
	}]);
