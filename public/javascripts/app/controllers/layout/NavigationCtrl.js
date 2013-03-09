app.controller('NavigationCtrl', ['$scope', '$http', '$log', '$location',
	function NavigationCtrl($scope, $http, $log, $location) {

		$scope.isNav = function(nav) {
			return _.str.startsWith($location.path(), '/' + nav);
		}
	}]);
