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

		$scope.ctx = {};
		$scope.ctx.username = $routeParams.username;
		$scope.init = function() {
			$log.log($routeParams);
			$scope.ctx.craId = $routeParams.id;
			$scope.ctx.dates = _.map($routeParams.days.split(','), function(i) {
				return Number(i);
			});
			$scope.ctx.date = $scope.ctx.dates[0];
			var days = _.map($scope.ctx.dates, function(date) {
				return moment(date).date();
			});
			$scope.title = _.str.toSentence(days, ', ', ' et ') + ' ' + _.str.capitalize(moment($scope.ctx.date).format('MMMM YYYY'));

			var route = jsRoutes.controllers.JDays.fetch($scope.ctx.craId, $scope.ctx.date);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(day, status, headers, config) {
					$scope.ctx.day = day;
					$scope.activateSubSection((day.isSpecial) ? 'special' : 'normal');
					route = jsRoutes.controllers.JUsers.affectedMissions($scope.ctx.username, _.head($scope.ctx.dates), $scope.ctx.dates[$scope.ctx.dates.length - 1]);
					$http({
						method: route.method,
						url: route.url
					})
						.success(function(affectedMissions, status, headers, config) {
							$log.log('affectedMissions', affectedMissions);
							$scope.ctx.affectedMissions = affectedMissions;
						})
						.error(function(data, status, headers, config) {
							$log.error(data, status);
						});
				})
				.error(function(data, status, headers, config) {
					$log.error(data, status);
				});
			$log.log('ctx', $scope.ctx);
		};
	}]);


app.controller('NormalDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function NormalDayCtrl($scope, $http, $log, $location, $routeParams) {

	}]);

app.controller('SpecialDayCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function SpecialDayCtrl($scope, $http, $log, $location, $routeParams) {

	}]);
