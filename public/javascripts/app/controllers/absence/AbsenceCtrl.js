app.controller('AbsenceCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'profile',
	function AbsenceCtrl($rootScope, $scope, $http, $log, $location, $routeParams, profile) {
		$scope.profile = profile.data;

		$scope.subSections = {
			'day': 'public/html/views/absence/day.html',
			'period': 'public/html/views/absence/period.html'
		};
		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.activateSubSection = function(name) {
			$scope.activeSubSection.name = name;
			$scope.activeSubSection.page = $scope.subSections[name];
			$location.path('/absence/' + name);
		};

		var loadHistory = function() {
			var route = jsRoutes.controllers.JAbsences.history($scope.profile.username);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(history, status, headers, config) {
					$log.debug('history', history);
					$scope.history = _(history)
						.sortBy('startDate')
						.valueOf();
				})
				.error(function(error, status, headers, config) {
					$log.debug('error', error);
				});
		};

		$scope.history = [];
		loadHistory();

		$scope.init = function() {
			if(!$routeParams.subSection) {
				$location.path('/absence/day');
			} else {
				loadMissions();

			}
		};

		$scope.save = function(absence) {
			absence.username = $scope.profile.username;
			var route = jsRoutes.controllers.JAbsences.create();
			$http({
				method: route.method,
				url: route.url,
				data: absence
			})
				.success(function(absence, status, headers, config) {
					$rootScope.onSuccess("L'absence a été créée.");
					$scope.history = _($scope.history)
						.push(absence)
						.sortBy('startDate')
						.valueOf();
				})
				.error(function(error, status, headers, config) {
					$log.debug('error', error);
				});
		};

		$scope.delete = function(id) {
			var route = jsRoutes.controllers.JAbsences.delete($scope.profile.id, id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(absence, status, headers, config) {
					$rootScope.onSuccess("L'absence a été supprimée.");
					$scope.history = _($scope.history)
						.filter(function(a) {
							return a.id !== absence.id;
						})
						.sortBy('startDate')
						.valueOf();
				})
		};
		var loadMissions = function() {
			var route = jsRoutes.controllers.JMissions.absences();
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(missions, status, headers, config) {
					$log.debug('missions', missions);
					$scope.missions = missions;
				})
				.error(function(error, status, headers, config) {
					$log.debug('error', error);
				});
		};


	}]);
