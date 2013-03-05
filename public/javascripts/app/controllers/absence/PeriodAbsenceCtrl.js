app.controller('PeriodAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function PeriodAbsenceCtrl($scope, $http, $log, $location, $routeParams) {
		$scope.localSave = function() {
			var period = {
				missionId: $scope.missionId,
				startDate: moment($scope.startDate, 'DD/MM/YYYY').valueOf(),
				startMorning: $scope.startMorning,
				startAfternoon: $scope.startAfternoon,
				endDate: moment($scope.endDate, 'DD/MM/YYYY').valueOf(),
				endMorning: $scope.endMorning,
				endAfternoon: $scope.endAfternoon,
				comment: $scope.comment
			};
			$log.debug('period', period);
			$scope.save(period);
		};
	}]);
