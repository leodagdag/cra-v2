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

		$scope.init = function() {
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


			if($scope.days.length === 1 && $scope.craId) {
				route = jsRoutes.controllers.JDays.fetch($scope.craId, $scope.date);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(day, status, headers, config) {
						$log.log('day', day);
						$scope.day = day;
						$scope.activateSubSection((day.isSpecial) ? 'special' : 'normal');

					})
					.error(function(error, status, headers, config) {

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
			var data = {
				username: $scope.username,
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
					$log.log(data);
					// /cra/:username/:year/:month
					$location.path(_.str.sprintf("/cra/%s/%s/%s", $scope.username, $scope.year, $scope.month));
				})
				.error(function(data, status, headers, config) {
					$log.error(data);
				});
		}

		$scope.back = function() {
			///cra/:username/:year/:month
			$location.path(_.str.sprintf('/cra/%s/%s/%s', $scope.username, $scope.year, $scope.month));
		}
	}]);


