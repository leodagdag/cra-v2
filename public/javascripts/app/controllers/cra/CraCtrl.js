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
			'id': ($routeParams.month || (moment().month() + 1 )).toString(),
			'label': _.str.capitalize(moment(($routeParams.month || (moment().month())).toString(), 'MMMM').format('MMMM'))
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

		$scope.search = function() {
			$log.log("search()", $scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
			loadCra($scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
		};


		$scope.class = function(halfday) {
			if(!halfday) {
				return "";
			} else if(halfday.missionId) {
				return halfday.missionType;
			} else if(halfday.isSpecial) {
				return 'special';
			}
			return "";
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

		$scope.openDay = function() {
			var days = _($scope.selectedDays)
				.filter('checked')
				.map(function(day) {
					return moment(day.date).date();
				})
				.sortBy()
				.compact()
				.join(',')
				.valueOf();
			// "/day/:username/:craId/:year/:month/:days"
			$location.path(_.str.sprintf("/day/%s/%s/%s/%s/%s", $scope.criterias.selected.employee, ($scope.cra.id) ? $scope.cra.id : "", $scope.cra.year, $scope.cra.month, days))
		};

		$scope.deleteDay = function(wIndex, date, dIndex) {
			if(confirm("Êtes vous sur de vouloir supprimer cette journée ?")) {
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
			}
		};

		var removeHalfDay = function(day, mOfD) {
			day[mOfD] = null;
		};

		$scope.deleteHalfDay = function(wIndex, date, dIndex, momentOfDay) {

			var day = $scope.cra.weeks[wIndex].days[dIndex];
			if(!day.morning || !day.afternoon) {
				$scope.deleteDay(wIndex, date, dIndex);
			} else {
				if(confirm("Êtes vous sur de vouloir supprimer cette demi-journée ?")) {
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
			}

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
					$scope.selectedMonth = {name: month, checked: false};
					$scope.$watch('selectedMonth.checked', function() {
						_.forEach($scope.selectedWeeks, function(week, wIdx) {
							week.checked = $scope.selectedMonth.checked;
							$scope.toggleWeek(wIdx);
						});
					});
					$scope.selectedWeeks = _.map($scope.cra.weeks, function(week, i) {
						return {number: week.number, checked: false}
					});

					$scope.selectedDays = _($scope.cra.weeks)
						.map(function(week) {
							return _.map(week.days, function(day, dIdx) {
								return {index: dIdx, week_number: week.number, date: day.date, checked: false};
							});
						})
						.flatten()
						.valueOf();
				});
		};

		$scope.toggleWeek = function(wIdx) {
			var week = $scope.selectedWeeks[wIdx];
			_.forEach($scope.selectedDays, function(day) {
				if(day.week_number === week.number
					&& !$scope.cra.weeks[wIdx].days[day.index].inPastOrFuture
					&& !$scope.cra.weeks[wIdx].days[day.index].isDayOff
					&& !$scope.cra.weeks[wIdx].days[day.index].isSaturday
					&& !$scope.cra.weeks[wIdx].days[day.index].isSunday
					&& !($scope.cra.weeks[wIdx].days[day.index].morning || $scope.cra.weeks[wIdx].days[day.index].afternoon)) {
					day.checked = week.checked;
				}
			});
		}
	}])
;


