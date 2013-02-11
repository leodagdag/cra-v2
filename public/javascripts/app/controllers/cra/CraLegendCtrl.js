app.controller('CraLegendCtrl', ['$scope', '$http', '$log', '$location',
	function CraLegendCtrl($scope, $http, $log, $location) {
		$scope.isOpen = false;
		$scope.toggle = function() {
			$scope.isOpen = !$scope.isOpen;
		}
	}]);
