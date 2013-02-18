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

		$scope.initTabs = function() {
			var route = jsRoutes.controllers.JDays.fetch($scope.craId, $scope.date);
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
		};

		$scope.save = function(day) {
			$log.log('$scope.save', day, $scope.dates);
		}
	}]);


app.controller('NormalDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function NormalDayCtrl($scope, $http, $log, $location, $routeParams) {

		if($scope.day.isSpecial) {
			$scope.localDay = {
				morning: {
					missionId: null
				},
				afternoon: {
					missionId: null
				},
				comment: null
			};
		} else {
			$scope.localDay = $scope.day;
		}

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

		$scope.localSave = function() {
			var newDay = {
				morning: ($scope.localDay.morning) ? $scope.localDay.morning.missionId : null,
				afternoon: ($scope.localDay.afternoon) ? $scope.localDay.afternoon.missionId : null,
				comment: $scope.localDay.comment
			};

			$scope.save(newDay);
		}
	}]);

app.controller('SpecialDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function SpecialDayCtrl($scope, $http, $log, $location, $routeParams) {

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


		$scope.form = {
			missionId: null,
			periodType: null,
			startTime: null,
			endTime: null
		};

		$scope.add = function(data){
			$log.log(data);
		}

		$scope.localSave = function() {
			var newDay = {
				morning: ($scope.localDay.morning) ? $scope.localDay.morning.missionId : null,
				afternoon: ($scope.localDay.afternoon) ? $scope.localDay.afternoon.missionId : null,
				comment: $scope.localDay.comment
			};

			$scope.save(newDay);
		}
	}]);
