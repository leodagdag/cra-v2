app.controller('ClaimSynthesisCtrl', ['$scope', '$rootScope', '$http', '$log', '$location', '$routeParams', 'ClaimTypeConst', 'MonthsConst', 'profile',
	function ClaimSynthesisCtrl($scope, $rootScope, $http, $log, $location, $routeParams, ClaimTypeConst, MonthsConst, profile) {
		$scope.profile = profile.data;
		var route = jsRoutes.controllers.JClaims.synthesis($scope.profile.id, $routeParams.year, $routeParams.month);
		$http({
			'method': route.method,
			'url': route.url
		})
			.success(function(synthesis, status, headers, config) {
				$log.debug(synthesis);
			})
			.error(function(error, status, headers, config) {

			});
	}]);
