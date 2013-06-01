app.controller('CraCtrl', ['$window', '$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'CraYearsConst', 'MonthsConst', 'RolesConst', 'profile',
	function($window, $rootScope, $scope, $http, $log, $location, $routeParams, CraYearsConst, MonthsConst, RolesConst, profile) {
		'use strict';
		$scope.profile = profile.data;
		/* Toolbar */
		var initialUsername = $routeParams.username || ($scope.profile.role === RolesConst.EMPLOYEE ? $scope.profile.username : $scope.employee);
		var initialYear = {
			'code': _.find(CraYearsConst,function(y) {
				return y.label === ($routeParams.year || moment().year()).toString();
			}).code,
			'label': ($routeParams.year || moment().year()).toString()
		};
		var initialMonth = {
			'code': ($routeParams.month || (moment().month() + 1 )),
			'label': _.str.capitalize(moment(($routeParams.month || (moment().month())).toString(), 'MMMM').format('MMMM'))
		};

		$scope.criterias = {
			'employees': [],
			'years': CraYearsConst,
			'months': MonthsConst,
			'showEmployees': false,
			'selected': {
				'employee': initialUsername,
				'year': initialYear,
				'month': initialMonth
			}
		};

		$scope.missionsOfCra = [];
		$scope.init = function() {
			if(profile.data.role === RolesConst.EMPLOYEE) {
				loadCra(initialUsername, initialYear.label, initialMonth.code);
			}
		};

		$scope.claimSynthesis = function() {
			// /claim/synthesis/:year/:month
			$location.path(_.str.sprintf('/cra/claim/synthesis/%s/%s', $scope.cra.year, $scope.cra.month));
		};

		$scope.previous = function() {
			var previous = moment(_.str.sprintf('%s/%s', $scope.cra.month, $scope.cra.year), 'MM/YYYY').subtract(1, 'months');
			// /cra/:username/:year/:month
			$location.path(_.str.sprintf("/cra/%s/%s/%s", $scope.criterias.selected.employee, previous.year(), previous.month() + 1));
		};

		$scope.next = function() {
			var next = moment(_.str.sprintf('%s/%s', $scope.cra.month, $scope.cra.year), 'MM/YYYY').add(1, 'months');
			// /cra/:username/:year/:month
			$location.path(_.str.sprintf("/cra/%s/%s/%s", $scope.criterias.selected.employee, next.year(), next.month() + 1));
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
						$scope.criterias.employees = employees;
					});
			}
		};


		/* Cra */
		$scope.cra = {};

		$scope.search = function() {
			loadCra($scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.code);
		};

		$scope.exportByEmployee = function() {
			$window.open(jsRoutes.controllers.JCras.exportByEmployee($scope.cra.id).url);
		};
		$scope.exportByMission = function(id) {
			$window.open(jsRoutes.controllers.JCras.exportByMission($scope.cra.id, id).url);
		};

		$scope.send = function() {
			var route = jsRoutes.controllers.JCras.send($scope.cra.id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(sentDate, status, headers, config) {
					$scope.cra.sentDate = moment(_.parseInt(sentDate)).valueOf();
				});
		};

		$scope.sent = function() {
			$window.open(jsRoutes.controllers.JCras.sent($scope.cra.id).url);
		};

		$scope.getClass = function(halfday) {
			if(!halfday) {
				return "";
			} else if(halfday.missionId) {
				return halfday.missionType;
			} else if(halfday.isSpecial) {
				return 'special';
			}
			return "";
		};

		$scope.isDayDeletable = function(day) {
			return day && !(day.inPastOrFuture) &&
				$scope.isHalfDayDeletable(day.morning) &&
				$scope.isHalfDayDeletable(day.afternoon);
		};

		$scope.isHalfDayDeletable = function(halfday, inPastOrFuture) {
			return !inPastOrFuture && halfday && halfday.missionType !== 'holiday';
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

		$scope.setComment = function() {
			var title = _.str.sprintf("Veuillez sasisir un commentaire pour le CRA de %s", moment().month($scope.cra.month - 1).year($scope.cra.year).format("MMMM YYYY"));
			var comment = $window.prompt(title, ($scope.cra.comment) ? $scope.cra.comment : "");
			if(comment !== null) {
				var route = jsRoutes.controllers.JCras.setComment($scope.cra.id);
				$http({
					method: route.method,
					url: route.url,
					data: {comment: comment}
				})
					.success(function(comment, status, headers, config) {
						$scope.cra.comment = comment;
					});
			}
		};


		$scope.openDay = function(dIndex) {
			var selectedDate = moment($scope.selectedDays[dIndex].date);
			if(selectedDate.month() + 1 === $scope.cra.month && selectedDate.year() === $scope.cra.year) {
				$scope.selectedDays[dIndex].checked = true;
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
				$location.path(_.str.sprintf("/day/%s/%s/%s/%s/%s", $scope.criterias.selected.employee, ($scope.cra.id) ? $scope.cra.id : "", $scope.cra.year, $scope.cra.month, days));
			}
		};

		$scope.removeDay = function(wIndex, date, dIndex) {
			if(window.confirm("Êtes-vous sûr de vouloir supprimer cette journée ?")) {
				var route = jsRoutes.controllers.JDays.remove($scope.cra.id, date);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(day, status, headers, config) {
						removeDay($scope.cra.weeks[wIndex].days[dIndex]);
					})
					.error(function(error, status, headers, config) {
						$rootScope.onError(error);
					});
			}
		};

		var removeHalfDay = function(day, mOfD) {
			day[mOfD] = null;
		};

		$scope.removeHalfDay = function(wIndex, date, dIndex, momentOfDay) {
			var day = $scope.cra.weeks[wIndex].days[dIndex];
			if(!day.morning || !day.afternoon) {
				$scope.removeDay(wIndex, date, dIndex);
			} else {
				if(window.confirm("Êtes-vous sûr de vouloir supprimer cette demi-journée ?")) {
					var route = jsRoutes.controllers.JDays.removeHalfDay($scope.cra.id, date, momentOfDay);
					$http({
						method: route.method,
						url: route.url
					})
						.success(function(halfDay, status, headers, config) {
							var mOfD = momentOfDay.toLowerCase();
							removeHalfDay(day, mOfD);
						})
						.error(function(error, status, headers, config) {
							$rootScope.onError(error);
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

		var extractMissionsOfCra = function(cra) {
			return  _(cra.weeks)
				.map(function(week, index) {
					return _(week.days)
						.map(function(day, index) {
							if(day.year === cra.year && day.month === cra.month) {
								return extractMissionsOfDay(day);
							} else {
								return [];
							}
						})
						.valueOf();
				})
				.flatten()
				.unique('id')
				.valueOf();
		};

		var extractMissionsOfDay = function(day) {
			return _(extractMissionsOfHalfDay(day.morning)).union(extractMissionsOfHalfDay(day.afternoon))
				.valueOf();
		};

		var extractMissionsOfHalfDay = function(halfDay) {
			var result = [];
			if(halfDay) {
				if(halfDay.isSpecial) {
					result = _(halfDay.periods)
						.map(function(period, index) {
							return {
								'id': period.missionId,
								'code': period.code,
								'label': period.label,
								'missionType': period.missionType
							};
						})
						.valueOf();
				} else {
					result = [
						{
							'id': halfDay.missionId,
							'code': halfDay.code,
							'label': halfDay.label,
							'missionType': halfDay.missionType
						}
					];
				}
			}

			return _(result)
				.filter(function(mission) {
					return mission.missionType === 'customer';
				})
				.valueOf();
		};

		var loadCra = function(username, year, month) {
			var route = jsRoutes.controllers.JCras.fetch(username, year, month);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$scope.cra = cra;
					$scope.selectedMonth = {name: month, checked: false};
					$scope.missionsOfCra = extractMissionsOfCra(cra);
					$scope.$watch('selectedMonth.checked', function() {
						_.forEach($scope.selectedWeeks, function(week, wIdx) {
							week.checked = $scope.selectedMonth.checked;
							$scope.toggleWeek(wIdx);
						});
					});
					$scope.selectedWeeks = _.map($scope.cra.weeks, function(week, i) {
						return {number: week.number, checked: false};
					});
					$scope.selectedDays = _($scope.cra.weeks)
						.map(function(week) {
							return _.map(week.days, function(day, dIdx) {
								return {index: dIdx, weekNumber: week.number, date: day.date, checked: false};
							});
						})
						.flatten()
						.valueOf();
				});
		};

		$scope.toggleWeek = function(wIdx) {
			var week = $scope.selectedWeeks[wIdx];
			_.forEach($scope.selectedDays, function(day) {
				var theDay = $scope.cra.weeks[wIdx].days[day.index];
				if(day.weekNumber === week.number && !theDay.inPastOrFuture && !theDay.isDayOff && !theDay.isSaturday && !theDay.isSunday) {
					if(theDay.morning && theDay.morning.missionType === 'holiday' && !theDay.afternoon) {
						day.checked = week.checked;
					} else if(theDay.afternoon && theDay.afternoon.missionType === 'holiday' && !theDay.morning) {
						day.checked = week.checked;
					} else if(!theDay.morning && !theDay.afternoon) {
						day.checked = week.checked;
					}
				}
			});
		};
	}])
;


