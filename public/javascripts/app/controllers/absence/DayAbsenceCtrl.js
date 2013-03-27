app.controller('DayAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function DayAbsenceCtrl($scope, $http, $log, $location, $routeParams) {
		$scope.localSave = function() {
			var day = {
				day: true,
				missionId: $scope.form.missionId,
				startDate: $scope.form.date ? moment($scope.form.date, 'DD/MM/YYYY').valueOf() : null,
				startMorning: $scope.form.morning,
				endDate: moment($scope.form.date, 'DD/MM/YYYY').valueOf(),
				endAfternoon: $scope.form.afternoon,
				comment: $scope.form.comment
			};
			$scope.save(day);
		};
	}]);
