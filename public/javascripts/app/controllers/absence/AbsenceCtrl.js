app.controller('AbsenceCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', '$window', 'profile', 'AbsenceTypeConst', 'MonthsConst',
	function AbsenceCtrl($rootScope, $scope, $http, $log, $location, $routeParams, $window, profile, AbsenceTypeConst, MonthsConst) {
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

		/* Form */
		$scope.init = function() {
			if(!$routeParams.subSection) {
				$location.path('/absence/day');
			} else {
				loadMissions();
			}
		};
		$scope.form = {};

		$scope.errors = {
			global: null,
			missionId: null,
			date: null,
			startDate: null,
			endDate: null,
			limits: null
		};
		$scope.save = function(absence) {
			absence.username = $scope.profile.username;
			$scope.errors = {};
			var route = jsRoutes.controllers.JAbsences.create();
			$http({
				method: route.method,
				url: route.url,
				data: absence
			})
				.success(function(abs, status, headers, config) {
					$rootScope.onSuccess("L'absence a été créée.");
					$scope.form = {
						startMorning: true,
						endAfternoon: true
					};
					$scope.loadHistory();
				})
				.error(function(errors, status, headers, config) {
					$log.error(errors);
					_(errors).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};

		$scope.remove = function(id) {
			if(confirm("Êtes vous sur de vouloir supprimer cette absence ?")) {
				var route = jsRoutes.controllers.JAbsences.remove($scope.profile.id, id);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(absence, status, headers, config) {
						$rootScope.onSuccess("L'absence a été supprimée.");
						$scope.loadHistory();
					})
					.error(function(error, status, headers, config) {
					});
			}
		};

		$scope.send = function(id) {
			var route = jsRoutes.controllers.JAbsences.send(id);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(absence, status, headers, config) {
					$rootScope.onSuccess("Votre demande a été envoyée.");
					$scope.loadHistory();
				})
				.error(function(error, status, headers, config) {
				});
		};

		$scope.exportFile = function(id) {
			$window.open(jsRoutes.controllers.JAbsences.exportFile(id).url);
		};

		var loadMissions = function() {
			var route = jsRoutes.controllers.JMissions.absences();
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(missions, status, headers, config) {
					$scope.missions = missions;
				})
				.error(function(error, status, headers, config) {
				});
		};

		/* History */
		$scope.months = _(MonthsConst).flatten().valueOf();
		$scope.absenceTypes = AbsenceTypeConst;
		$scope.sortBys = [
			{'key': '+startDate', 'label': 'Date (asc)'},
			{'key': '-startDate', 'label': 'Date (desc)'},
			{'key': '+code', 'label': 'Code (asc)'},
			{'key': '-code', 'label': 'Code (desc)'}
		];
		$scope.filter = {
			'year': moment().year(),
			'month': $scope.months[moment().month()].code,
			'absenceType': $scope.absenceTypes[0].code,
			'sortBy': $scope.sortBys[0].key
		};

		$scope.history = [];
		$scope.filterChange = function() {
			if(($scope.filter.year === "0")) {
				$scope.filter.month = 0;
			}
			$scope.loadHistory();
		};

		$scope.sortByChange = function() {
			$scope.history = sort($scope.history, $scope.filter);
		};

		var sort = function(list) {
			var field = $scope.filter.sortBy.substr(1),
				direction = $scope.filter.sortBy.substr(0, 1) === '+' ? 'asc' : 'desc',
				result = _(list)
					.sortBy(field);
			if(direction === 'desc') {
				result.reverse();
			}
			return result.valueOf();

		};
		$scope.loadHistory = function() {
			var route = jsRoutes.controllers.JAbsences.history($scope.profile.id, $scope.filter.absenceType, $scope.filter.year, $scope.filter.month || 0);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(history, status, headers, config) {
					$scope.history = sort(history);
				})
				.error(function(error, status, headers, config) {
				});
		};


	}]);
