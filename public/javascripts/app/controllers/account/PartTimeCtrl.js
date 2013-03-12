app.controller('PartTimeNewCtrl', ['$rootScope', '$scope', '$http', '$log', '$location',
	function PartTimeNewCtrl($rootScope, $scope, $http, $log, $location) {
		var Form = function () {
			this.startDate = null;
			this.endDate = null;
			this.weekDays = [];
			this.frequency = null;

			this.to = function () {
				return {
					userId: $scope.profile.id,
					startDate: moment(this.startDate, 'DD/MM/YYYY').valueOf(),
					endDate: (this.endDate) ? moment(this.endDate, 'DD/MM/YYYY').valueOf() : null,
					weekDays: this.weekDays,
					frequency: this.frequency
				}
			}
		};

		$scope.errors = {
			global: null,
			startDate: null,
			endDate: null,
			weekDays: null,
			frequency: null
		};

		$scope.dayMoments = [
			{moment: "day", label: "Journée"},
			{moment: "morning", label: "Matin"},
			{moment: "afternoon", label: "Après-midi"}
		];

		$scope.form = new Form();

		$scope.toggleDay = function (dayId, dayMoment) {
			if (dayMoment) {
				var weekDay = {"day": dayId, "moment": dayMoment.moment};
				$scope.form.weekDays.push(weekDay);
			} else {
				$scope.form.weekDays = _($scope.partTime.weekDays)
					.reject(function (item) {
						return item.day === this.valueOf();
					}, dayId)
					.valueOf();
			}
		};

		$scope.save = function () {
			$log.debug('$scope.form', $scope.form.to());
			if (!_.size($scope.form.weekDays)) {
			} else {
				var route = jsRoutes.controllers.JPartTimes.setPartTime();
				$http({
					method: route.method,
					url: route.url,
					data: $scope.form
				})
					.success(function (data, status, headers, config) {
						$rootScope.onSuccess("Votre temps partiel a été sauvegardé.");
					});

			}
		}
	}]);

app.controller('ActivePartTimeCtrl', ['$scope', '$http', '$log', '$location',
	function ActivePartTimeCtrl($scope, $http, $log, $location) {
		$scope.init = function () {
			/*$http({
			 method: jsRoutes.controllers.PartTimes.myActivePartTime().method,
			 url: jsRoutes.controllers.PartTimes.myActivePartTime().url
			 })
			 .success(function (activePartTime, status, headers, config) {
			 $scope.activePartTime = activePartTime;
			 });*/
		}
	}]);
