app.controller('CraCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function CraCtrl($scope, $http, $log, $location, $routeParams) {
		$log.log($routeParams);
		$scope.cra = {};
		$scope.trigramme = $routeParams.trigramme;
		$scope.year = $routeParams.year || moment().year();
		$scope.month = $routeParams.month || (moment().month() + 1);
		$log.log($scope.trigramme, $scope.year, $scope.month);

		$scope.fetch = function() {
			var route = jsRoutes.controllers.JCras.fetch('BSN', 2013, 2);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$log.log(cra);
					$scope.cra = cra;
				});
		}

	}]);
