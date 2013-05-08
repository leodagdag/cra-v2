app.controller('NormalDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';
		var NormalForm = function(day) {
			return {
				morning: {
					missionId: (day && !day.isSpecial && day.morning) ? day.morning.missionId : null
				},
				afternoon: {
					missionId: (day && !day.isSpecial && day.afternoon) ? day.afternoon.missionId : null
				},
				comment: (day && !day.isSpecial) ? day.comment : null
			};
		};

		$scope.form = new NormalForm($scope.day);

		$scope.localSave = function() {
			$scope.save($scope.form);
		};
	}]);
