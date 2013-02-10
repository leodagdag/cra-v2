app.controller('PartTimeNewCtrl', ['$scope', '$http', '$log', '$location',
	function PartTimeNewCtrl($scope, $http, $log, $location) {
		$scope.emptyDayOfWeek = false;
		$scope.dayMoments = [
			{moment: "DAY", label: "Journée"},
			{moment: "MORNING", label: "Matin"},
			{moment: "AFTERNOON", label: "Après-midi"}
		];

		$scope.newPartTime = {
			startDate: null,
			endDate: null,
			weekDays: [],
			frequency: null
		};

		$scope.toggleDay = function(dayId, dayMoment) {
			if(dayMoment) {
				var weekDay = {"day": dayId, "moment": dayMoment.moment};
				$scope.newPartTime.weekDays.push(weekDay);
				$scope.emptyDayOfWeek = false;
			} else {
				$scope.newPartTime.weekDays = _.reject($scope.newPartTime.weekDays, function(item) {
					return item.day === this.valueOf();
				}, dayId);
			}
		};

		$scope.save = function() {
			if(!_.size($scope.newPartTime.weekDays)) {
				$scope.emptyDayOfWeek = true;
			} else {
				$http({
					method: jsRoutes.controllers.Accounts.addPartTime().method,
					url: jsRoutes.controllers.Accounts.addPartTime().url,
					data: $scope.newPartTime
				})
					.success(function(data, status, headers, config) {
						$log.log(data);
					});

			}
		}
	}]);