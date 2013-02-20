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
				var start = moment(p.startTime, 'HH:mm').year(0);
				var st = moment(noon).hours(start.hours()).minutes(start.minutes()).valueOf();
				var end = moment(p.endTime, 'HH:mm');
				var et = moment(noon).hours(end.hours()).minutes(end.minutes()).valueOf();
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
					} else if(!this[halfDay].periods) {
						this[halfDay].periods = [];
					}
					this[halfDay].periods.push(_.pick(p, 'missionId', 'startTime', 'endTime'))
				}
				else {
					this[p.periodType] = {
						missionId: p.missionId
					}
				}
			}, day);
			$scope.save(day);
		};


	}])
;
