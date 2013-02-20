app.controller('NormalDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function NormalDayCtrl($scope, $http, $log, $location, $routeParams) {
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
		var NormalDay = function(d) {
			return {
				morning: (d.morning) ? d.morning.missionId : null,
				afternoon: (d.afternoon) ? d.afternoon.missionId : null,
				comment: d.comment
			}
		};

		$scope.form = new NormalForm($scope.day);

		$scope.localSave = function() {
			$scope.save($scope.form);
		}
	}]);
