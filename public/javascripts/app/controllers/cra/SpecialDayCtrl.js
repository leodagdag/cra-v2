app.controller('SpecialDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';
		var noon = moment().hours(12).minutes(0).seconds(0).milliseconds(0);
		var PeriodForm = function() {
			return {
				missionId: null,
				periodType: null,
				startTime: null,
				endTime: null
			};
		};

		var Period = function(p) {
			var startTime, endTime;
			if(p.startTime && p.endTime) {
				var start = _.isNumber(p.startTime) ? moment(p.startTime) : moment(p.startTime, 'HH:mm'),
					end = _.isNumber(p.endTime) ? moment(p.endTime) : moment(p.endTime, 'HH:mm');
				startTime = moment(noon).hours(start.hours()).minutes(start.minutes()).valueOf();
				endTime = moment(noon).hours(end.hours()).minutes(end.minutes()).valueOf();
			}
			return {
				missionId: p.missionId,
				periodType: p.periodType,
				startTime: startTime,
				endTime: endTime
			};
		};

		var SpecialDay = function() {
			return {
				morning: null,
				afternoon: null
			};
		};

		var extractPeriods = function(halfday) {
			if(halfday && halfday.periods) {
				return _(halfday.periods)
					.map(function(p) {
						return new Period(p);
					}).valueOf();
			}
			return [];
		};

		$scope.form = new PeriodForm();
		$scope.comment = $scope.day.comment;
		$scope.periods = _.union(
				extractPeriods($scope.day.morning),
				extractPeriods($scope.day.afternoon)
			)
			.valueOf();

		$log.log($scope.periods);

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


		$scope.isSpecial = function(p) {
			return p.periodType === 'special';
		};

		$scope.localSave = function() {
			var day = {
				comment: $scope.comment
			};
			_.each($scope.periods, function(p) {
				if(p.periodType === 'special') {
					var halfDay = (moment(p.endTime).toDate() < noon.toDate()) ? 'morning' : 'afternoon';
					if(!this[halfDay]) {
						this[halfDay] = {
							periods: []
						};
					} else if(!this[halfDay].periods) {
						this[halfDay].periods = [];
					}
					this[halfDay].periods.push(_.pick(p, 'missionId', 'startTime', 'endTime'));
				}
				else {
					this[p.periodType] = {
						missionId: p.missionId
					};
				}
			}, day);
			$scope.save(day);
		};


	}
])
;
