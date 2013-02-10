app.controller('ActivePartTimeCtrl', ['$scope', '$http', '$log', '$location',
	function ActivePartTimeCtrl($scope, $http, $log, $location) {
		$scope.fetchActivePartTimes = function() {
			$http({
				method: jsRoutes.controllers.PartTimes.myActivePartTime().method,
				url: jsRoutes.controllers.PartTimes.myActivePartTime().url
			})
				.success(function(activePartTime, status, headers, config) {
					$scope.activePartTime = activePartTime;
				});
		}
	}]);