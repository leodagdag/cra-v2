app.controller('PartTimeNewCtrl', ['$rootScope', '$scope', '$http', '$log', '$location',
	function PartTimeNewCtrl($rootScope, $scope, $http, $log, $location) {
		$scope._ = _;
		/* Form */
		var PartTime = function(form) {
			this.userId = $scope.profile.id;
			this.startDate = (form.startDate) ? moment(form.startDate, 'DD/MM/YYYY').valueOf() : null;
			this.endDate = (form.endDate) ? moment(form.endDate, 'DD/MM/YYYY').valueOf() : null;
			this.daysOfWeek = form.daysOfWeek;
			this.frequency = form.frequency;
		};

		var Form = function() {
			this.startDate = null;
			this.endDate = null;
			this.daysOfWeek = [];
			this.frequency = null;
		};

		$scope.errors = {
			global: null,
			startDate: null,
			endDate: null,
			daysOfWeek: null,
			frequency: null
		};

		$scope.dayMoments = [
			{moment: "day", label: "Journée"},
			{moment: "morning", label: "Matin"},
			{moment: "afternoon", label: "Après-midi"}
		];

		$scope.form = new Form();

		$scope.toggleDay = function(dayId, dayMoment) {
			if(dayMoment) {
				var weekDay = {"dayOfWeek": dayId, "momentOfDay": dayMoment.moment};
				$scope.form.daysOfWeek.push(weekDay);
			} else {
				$scope.form.daysOfWeek = _($scope.form.daysOfWeek)
					.reject(function(item) {
						return item.dayOfWeek === this.valueOf();
					}, dayId)
					.valueOf();
			}
		};

		$scope.save = function() {
			var partTime = new PartTime($scope.form);
			var route = jsRoutes.controllers.JPartTimes.addPartTimes();
			$http({
				method: route.method,
				url: route.url,
				data: partTime
			})
				.success(function(data, status, headers, config) {
					$rootScope.onSuccess("Votre temps partiel a été sauvegardé.");
					$scope.loadActive();
				})
				.error(function(errors, status, headers, config) {
					_(errors).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};

		/* Active PartTime */
		$scope.activePartTime = {};

		$scope.loadActive = function() {
			var route = jsRoutes.controllers.JPartTimes.active($scope.profile.id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(activePartTime, status, headers, config) {
					$scope.activePartTime = activePartTime;
				});
		};

		$scope.deactivate = function(id) {
			if(confirm('Êtes-vous sûr de vouloir désactiver votre temps partiel ?')) {
				var route = jsRoutes.controllers.JPartTimes.deactivate();
				$http({
					method: route.method,
					url: route.url,
					data: {'id': id}
				})
					.success(function(result, status, headers, config) {
						$scope.activePartTime = {};
						$rootScope.onSuccess("Le temps partiel a été désactivé.");
					});
			}
		};
	}]);

