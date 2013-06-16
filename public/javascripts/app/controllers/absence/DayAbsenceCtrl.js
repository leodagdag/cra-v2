app.controller('DayAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';
		$scope.localSave = function() {
			var day = {
				day: true,
				missionId: $scope.form.missionId,
				startDate: $scope.form.date ? moment($scope.form.date, 'DD/MM/YYYY').valueOf() : null,
				startMorning: $scope.form.startMorning,
				endDate: $scope.form.date ? moment($scope.form.date, 'DD/MM/YYYY').valueOf(): null,
				endAfternoon: $scope.form.endAfternoon,
				comment: $scope.form.comment
			};
			$scope.save(day);
		};
	}]);
