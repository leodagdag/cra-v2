app.controller('HistoryClaimCtrl', ['$scope', '$http', '$log', '$location',
	function HistoryClaimCtrl($scope, $http, $log, $location) {
		$scope.filter = {
			year: moment().year(),
			month: moment().month(),
			orderBy: 'date'
		};
	}]);
