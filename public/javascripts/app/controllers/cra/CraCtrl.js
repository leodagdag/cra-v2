app.controller('CraCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'YearsConst', 'MonthsConst', 'RolesConst', 'profile',
	function CraCtrl($rootScope, $scope, $http, $log, $location, $routeParams, YearsConst, MonthsConst, RolesConst, profile) {

		/* Toolbar */
		var initialUsername = $routeParams.username || (profile.data.role === RolesConst.EMPLOYEE ? profile.data.username : $scope.employee);
		var initialYear = {
			'id': _.find(YearsConst,function(y) {
				return y.label === ($routeParams.year || moment().year()).toString()
			}).id,
			'label': ($routeParams.year || moment().year()).toString()
		};
		var initialMonth = {
			'id': ($routeParams.month || (moment().month() + 1)).toString(),
			'label': _.str.capitalize(moment.months[$routeParams.month || (moment().month())])
		};

		$scope.criterias = {
			'employees': [],
			'years': YearsConst,
			'months': MonthsConst,
			'showEmployees': false,
			'selected': {
				'employee': initialUsername,
				'year': initialYear,
				'month': initialMonth
			}
		};

		$scope.init = function() {
			if(profile.data.role === RolesConst.EMPLOYEE) {
				loadCra(initialUsername, initialYear.label, initialMonth.id);
			}
		};

		$scope.initToolbar = function() {
			if(profile.data.role !== RolesConst.EMPLOYEE) {
				$scope.criterias.showEmployees = true;
				var route = jsRoutes.controllers.JUsers.employees();
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(employees, status, headers, config) {
						$log.log('employees', employees);
						$scope.criterias.employees = employees;
					});
			}
		};


		/* Cra */
		$scope.cra = {};
		$scope.selectedWeeks = [];
		$scope.selectedDays = [];

		$scope.search = function() {
			$log.log("search()", $scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
			loadCra($scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
		};


		$scope.showCheckDay = function(day) {
			return !day.inPastOrFuture;
		};

		$scope.checkDay = function(date) {
			if(!_.contains($scope.selectedDays, date)) {
				$scope.selectedDays.push(date);
			}
		};

		$scope.toggleDay = function(date) {
			if(_.contains($scope.selectedDays, date)) {
				$scope.selectedDays = _.reject($scope.selectedDays, function(d) {
					return date === d;
				});
			} else {
				$scope.selectedDays.push(date);
			}
		};


		$scope.toggleWeek = function(wIndex) {
			var isSelected = _.contains($scope.selectedWeeks, wIndex);
			if(isSelected) {
				$scope.selectedWeeks = _.reject($scope.selectedWeeks, function(w) {
					return wIndex === w;
				});
			} else {
				$scope.selectedWeeks.push(wIndex);
			}
			$scope.selectedDays = _.compact(_.flatten(_.map($scope.selectedWeeks, function(wIdx) {
				return _.map($scope.cra.weeks[wIdx].days, function(d) {
					return (d.inPastOrFuture || d.isDayOff || d.isSaturday || d.isSunday || d.morning || d.afternoon) ? null : d.date;
				});
			})));

		};

		$scope.validate = function() {
			var route = jsRoutes.controllers.Cras.validate($scope.cra.id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$scope.cra.isValidated = true;
				});
		};

		$scope.invalidate = function() {
			var route = jsRoutes.controllers.Cras.invalidate($scope.cra.id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$scope.cra.isValidated = false;
				});
		};

		$scope.deleteDay = function(wIndex, date, dIndex) {
			var route = jsRoutes.controllers.Days.delete($scope.cra.id, date);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(day, status, headers, config) {
					removeDay($scope.cra.weeks[wIndex].days[dIndex])
				})
				.error(function(data, status, headers, config) {
					$log.error(data, status);
				});
		};

		$scope.deleteHalfDay = function(wIndex, date, dIndex, momentOfDay) {
			var day = $scope.cra.weeks[wIndex].days[dIndex];
			if(!day.morning || !day.afternoon) {
				$scope.deleteDay(wIndex, date, dIndex);
			} else {
				var route = jsRoutes.controllers.Days.deleteHalfDay($scope.cra.id, date, momentOfDay);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(halfDay, status, headers, config) {
						var mOfD = momentOfDay.toLowerCase();
						removeHalfDay(day, mOfD);
					})
					.error(function(data, status, headers, config) {
						$log.error(data, status);
					});
			}
		};

		$scope.openDay = function() {
			$log.log('openDay');

			$location.path('/day/' + $scope.criterias.selected.employee + '/' + $scope.cra.id + '/' + _.sortBy($scope.selectedDays).join(','))
		};

		var removeHalfDay = function(day, mOfD) {
			day[mOfD] = null;
		};

		var removeDay = function(day) {
			day.id = null;
			day.comment = null;
			day.morning = null;
			day.afternoon = null;
		};

		var loadCra = function(username, year, month) {
			var route = jsRoutes.controllers.JCras.fetch(username, year, month);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$log.log('cra', cra);
					$scope.cra = cra;
					$scope.selectedWeeks = [];
					$scope.selectedDays = [];
				});
		};

	}])
;


