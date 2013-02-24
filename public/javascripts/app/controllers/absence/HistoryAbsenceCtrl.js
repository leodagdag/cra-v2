app.controller('HistoryAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function HistoryAbsenceCtrl($scope, $http, $log, $location, $routeParams) {
		$scope.init = function() {
			var route = jsRoutes.controllers.JAbsences.history($scope.profile.username);
			$log.debug(route);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(history, status, headers, config) {
					$log.debug('history', history);
					$scope.history = history;
				});

		};
	}]);
