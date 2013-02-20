app.controller('DayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'profile',
	function DayCtrl($scope, $http, $log, $location, $routeParams, profile) {
		$scope.subSections = {
			'normal': 'public/html/views/cra/normalDay.html',
			'special': 'public/html/views/cra/specialDay.html'
		};
		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.activateSubSection = function(name) {
			$scope.activeSubSection.name = name;
			$scope.activeSubSection.page = $scope.subSections[name];
		};

		$scope.username = $routeParams.username;
		$scope.craId = $routeParams.id;
		$scope.year = $routeParams.year;
		$scope.month = $routeParams.month;
		$scope.dates = _($routeParams.days.split(','))
			.map(function(i) {
				return moment(Number(i) + '/' + ($routeParams.month) + '/' + $routeParams.year, 'DD/MM/YYYY').valueOf();
			})
			.valueOf();
		$scope.date = $scope.dates[0];
		$scope.days = _($scope.dates)
			.map(function(date) {
				return moment(date).date();
			})
			.valueOf();
		$scope.title = _.str.toSentence($scope.days, ', ', ' et ') + ' ' + _.str.capitalize(moment($scope.date).format('MMMM YYYY'));

		var route = jsRoutes.controllers.JUsers.affectedMissions($scope.username, _.head($scope.dates), $scope.dates[$scope.dates.length - 1]);
		$http({
			method: route.method,
			url: route.url
		}).success(function(affectedMissions, status, headers, config) {
				$scope.affectedMissions = affectedMissions;
			})
			.error(function(data, status, headers, config) {
				$log.error(data, status);
			});

		route = jsRoutes.controllers.JDays.fetch($scope.craId, $scope.date);
		$http({
			method: route.method,
			url: route.url
		})
			.success(function(day, status, headers, config) {
				$scope.day = day;
				$scope.activateSubSection((day.isSpecial) ? 'special' : 'normal');

			})
			.error(function(data, status, headers, config) {
				$log.error(data, status);
			});

		$scope.save = function(day) {
			$log.log('$scope.save', day, $scope.dates);
		}
	}]);


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
			$scope.save(new NormalDay($scope.form));
		}
	}]);

app.controller('SpecialDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function SpecialDayCtrl($scope, $http, $log, $location, $routeParams) {
		var noon = moment('12', 'HH');
		var PeriodForm = function() {
			return {
				missionId: null,
				periodType: null,
				startTime: null,
				endTime: null
			};
		};

		var Period = function(p) {
			if(p.startTime && p.endTime) {
				var start = moment(p.startTime, 'HH:mm');
				var st = moment().hours(start.hours()).minutes(start.minutes()).valueOf();
				var end = moment(p.endTime, 'HH:mm');
				var et = moment().hours(end.hours()).minutes(end.minutes()).valueOf();
			}
			return {
				missionId: p.missionId,
				periodType: p.periodType,
				startTime: st || null,
				endTime: et || null
			}
		};

		var SpecialDay = function() {
			return {
				morning: null,
				afternoon: null
			}
		};

		$scope.form = new PeriodForm();
		$scope.comment = $scope.day.comment;
		$scope.periods = [];

		$scope.addPeriod = function(period) {
			$scope.periods = _($scope.periods)
				.push(new Period(period))
				.sortBy('startTime')
				.valueOf();
			$scope.form = new PeriodForm();
		};

		$scope.removePeriod = function(period) {
			$scope.periods = _($scope.periods)
				.reject(period)
				.sortBy('startTime')
				.valueOf();
		};

		$scope.getMissionLabel = function(id) {
			return _.find($scope.affectedMissions,function(am) {
				return am.id === id;
			}).code;
		};

		$scope.isSpecial = function(p) {
			return p.periodType === 'special';
		};

		$scope.localSave = function() {
			var day = {};
			_.each($scope.periods, function(p) {
				if(p.periodType === 'special') {
					var halfDay = (moment(p.endTime).toDate() < noon.toDate()) ? 'morning' : 'afternoon';
					if(!this[halfDay]) {
						this[halfDay] = {
							periods: []
						}
					}
					this[halfDay].periods.push(_.pick(p, 'missionId', 'startTime', 'endTime'))
				}
				else {
					this[p.periodType] = {
						missionId: p.missionId
					}
				}
			}, day);
			$log.log(day);
			//$scope.save(newDay);
		};


	}])
;
