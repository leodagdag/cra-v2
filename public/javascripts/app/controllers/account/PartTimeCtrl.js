app.controller('PartTimeNewCtrl', ['$scope', '$http', '$log', '$location',
	function PartTimeNewCtrl($scope, $http, $log, $location) {
		$scope.emptyDayOfWeek = false;
		$scope.dayMoments = [
			{moment: "day", label: "Journée"},
			{moment: "morning", label: "Matin"},
			{moment: "afternoon", label: "Après-midi"}
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
				var route = jsRoutes.controllers.Accounts.addPartTime();
				$http({
					method: route.method,
					url: route.url,
					data: $scope.newPartTime
				})
					.success(function(data, status, headers, config) {
						$log.log(data);
					});

			}
		}
	}]);

app.controller('ActivePartTimeCtrl', ['$scope', '$http', '$log', '$location',
	function ActivePartTimeCtrl($scope, $http, $log, $location) {
		$scope.fetchActivePartTimes = function() {
			$http({
				method: jsRoutes.controllers.PartTimes.myActivePartTime().method,
				url: jsRoutes.controllers.PartTimes.myActivePartTime().url
			})
				.success(function(activePartTime, status, headers, config) {
					$scope.activePartTime = activePartTime;
				});
		}
	}]);
