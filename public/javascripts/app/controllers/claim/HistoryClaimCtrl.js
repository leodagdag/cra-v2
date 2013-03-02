app.controller('HistoryClaimCtrl', ['$scope', '$http', '$log', '$location', 'MonthsConst',
    function HistoryClaimCtrl($scope, $http, $log, $location, MonthsConst) {
        $scope.months = _(MonthsConst)
            .flatten()
            .valueOf();
        $scope.filter = {
            year: moment().year(),
            month: $scope.months[moment().month()].id,
            orderBy: 'date'
        };
	    $scope.claims = [];

	    $scope.init = function () {
		    var route = jsRoutes.controllers.JClaims.fetch($scope.profile.username, $scope.filter.year, $scope.filter.month);
		    $http({
			    method: route.method,
			    url: route.url
		    })
			    .success(function (claims, status, headers, config) {
				    $log.debug('claims', claims);
				    $scope.claims = _($scope.claims)
					    .push(claims)
					    .flatten()
					    .valueOf();
			    })
			    .error(function (error, status, headers, config) {
				    $log.debug('error', error);
			    });
	    };
    }]);
