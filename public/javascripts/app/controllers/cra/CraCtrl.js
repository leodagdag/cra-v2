app.controller('CraCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'YearsConst', 'MonthsConst',
	function CraCtrl($rootScope, $scope, $http, $log, $location, $routeParams, YearsConst, MonthsConst) {

		/* Toolbar */
		var initialEmployee = $routeParams.username || ($rootScope.profile.role === $rootScope.Roles.EMPLOYEE ? $rootScope.profile.username : $scope.employee);
		var initialYear = {
			'id': _.find(YearsConst,function(y) {
				return y.label === ($routeParams.year || moment().year()).toString()
			}).id,
			'label': ($routeParams.year || moment().year()).toString()
		};
		var initialMonth = {
			'id': ($routeParams.month || (moment().month() + 1)).toString(),
			'label': _.str.capitalize(moment.months[$routeParams.month || (moment().month())])
		};

		$scope.criterias = {
			'employees': [],
			'years': YearsConst,
			'months': MonthsConst,
			'showEmployees': false,
			'selected': {
				'employee': initialEmployee,
				'year': initialYear,
				'month': initialMonth
			}
		};

		$scope.initToolbar = function() {
			if($rootScope.profile.role !== $rootScope.Roles.EMPLOYEE) {
				$scope.criterias.showEmployees = true;
				var route = jsRoutes.controllers.JUsers.employees();
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(employees, status, headers, config) {
						$log.log('employees', employees);
						$scope.criterias.employees = employees;
					});
			}
		};


		/* Cra */
		$scope.cra = {};

		$scope.search = function() {
			$log.log("search()", $scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
			loadCra($scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
		};

		$scope.init = function() {
			if($rootScope.profile.role === $rootScope.Roles.EMPLOYEE) {
				loadCra(initialEmployee, initialYear.label, initialMonth.id);
			}
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

		$scope.deleteDay = function(wIndex, date, dIndex) {
			var route = jsRoutes.controllers.Days.delete($scope.cra.id, date);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(day, status, headers, config) {
					removeDay($scope.cra.weeks[wIndex].days[dIndex])
				})
				.error(function(data, status, headers, config) {
					$log.error(data, status);
				});
		};

		$scope.deleteHalfDay = function(wIndex, date, dIndex, momentOfDay) {
			var day = $scope.cra.weeks[wIndex].days[dIndex];
			if(!day.morning || !day.afternoon) {
				$scope.deleteDay(wIndex, date, dIndex);
			} else {
				var route = jsRoutes.controllers.Days.deleteHalfDay($scope.cra.id, date, momentOfDay);
				$http({
					method: route.method,
					url: route.url
				})
					.success(function(halfDay, status, headers, config) {
						var mOfD = momentOfDay.toLowerCase();
						removeHalfDay(day, mOfD);
					})
					.error(function(data, status, headers, config) {
						$log.error(data, status);
					});
			}
		};

		var removeHalfDay = function(day, mOfD) {
			day[mOfD] = null;
		};

		var removeDay = function(day) {
			day.id = null;
			day.comment = null;
			day.morning = null;
			day.afternoon = null;
		};

		var loadCra = function(username, year, month) {
			var route = jsRoutes.controllers.JCras.fetch(username, year, month);
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$log.log('cra', cra);
					$scope.cra = cra;
				});
		};

	}])
;


