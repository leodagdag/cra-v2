app.controller('PeriodAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';
		$scope.localSave = function() {
			var period = {
				day: false,
				missionId: $scope.form.missionId,
				startDate: $scope.form.startDate ? moment($scope.form.startDate, 'DD/MM/YYYY').valueOf() : null,
				startMorning: $scope.form.startMorning,
				endDate: $scope.form.endDate ? moment($scope.form.endDate, 'DD/MM/YYYY').valueOf() : null,
				endAfternoon: $scope.form.endAfternoon,
				comment: $scope.form.comment
			};
			$scope.save(period);
		};
	}]);
