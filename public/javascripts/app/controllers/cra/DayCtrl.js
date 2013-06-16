app.controller('DayCtrl', ['$scope', '$rootScope', '$http', '$log', '$location', '$routeParams', 'profile',
	function($scope, $rootScope, $http, $log, $location, $routeParams, profile) {
		'use strict';
		$scope.profile = profile.data;

		$scope.subSections = {
			'normal': 'assets/html/views/cra/normalDay.html',
			'special': 'assets/html/views/cra/specialDay.html'
		};
		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.activateSubSection = function(name) {
			$scope.activeSubSection.name = name;
			$scope.activeSubSection.page = $scope.subSections[name];
			resetError();
		};

		$scope.userId = $scope.profile.id;
		$scope.username = $scope.profile.username;
		$scope.craId = $routeParams.craId;
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
		$scope.day = {};
		$scope.affectedMissions = [];
		$scope.title = _.str.toSentence($scope.days, ', ', ' et ') + ' ' + _.str.capitalize(moment($scope.date).format('MMMM YYYY'));

		$scope.errors = {
			global: []
		};

		$scope.init = function() {
			var route = jsRoutes.controllers.JMissions.craMissions($scope.username, _.head($scope.dates), $scope.dates[$scope.dates.length - 1]);
			$http({
				method: route.method,
				url: route.url
			}).success(function(affectedMissions, status, headers, config) {
					$scope.affectedMissions = affectedMissions;
				})
				.error(function(error, status, headers, config) {
					$rootScope.onError(error);
				});


			if($scope.days.length === 1 && $scope.craId) {
				route = jsRoutes.controllers.JDays.fetch($scope.craId, $scope.date);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(day, status, headers, config) {
						$scope.day = day;
						$scope.activateSubSection((day.isSpecial) ? 'special' : 'normal');

					})
					.error(function(error, status, headers, config) {
						$rootScope.onError(error);
					});
			} else {
				$scope.activateSubSection('normal');
			}
		};

		$scope.getMissionLabel = function(id) {
			return _.find($scope.affectedMissions,function(am) {
				return am.id === id;
			}).code;
		};

		$scope.save = function(d) {
			resetError();
			var data = {
				userId: $scope.userId,
				craId: $scope.craId,
				year: $scope.year,
				month: $scope.month,
				dates: $scope.dates,
				day: d
			};

			var route = jsRoutes.controllers.JDays.create();
			$http({
				method: route.method,
				url: route.url,
				data: data
			})
				.success(function(data, status, headers, config) {
					$scope.back();
				})
				.error(function(errors, status, headers, config) {
					_(errors).forEach(function(err, key) {
						if(_.isArray($scope.errors[key])) {
							$scope.errors[key] = _($scope.errors[key]).push(err).flatten().valueOf();
						} else {
							$scope.errors[key] = err;
						}
					});
				});
		};

		$scope.back = function() {
			///cra/:username/:year/:month
			$location.path(_.str.sprintf('/cra/%s/%s/%s', $scope.username, $scope.year, $scope.month));
		};

		var resetError = function() {
			$scope.errors = {
				global: []
			};
		};
	}]);


