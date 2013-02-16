app.controller('CraCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'YearsConst', 'MonthsConst',
	function CraCtrl($rootScope, $scope, $http, $log, $location, $routeParams, YearsConst, MonthsConst) {

		/* Toolbar */
		var initialEmployee = $routeParams.username || ($rootScope.profile.role === $rootScope.Roles.EMPLOYEE ? $rootScope.profile.username : $scope.employee);
		var initialYear = {
			'id': _.find(YearsConst, function(y) {
				return y.label === ($routeParams.year || moment().year()).toString()
			}).id,
			'label': ($routeParams.year || moment().year()).toString()
		};
		var initialMonth = {
			'id': ($routeParams.month || (moment().month() + 1)).toString(),
			'label': _.str.capitalize(moment.months[$routeParams.month || (moment().month())])
		};

		$scope.initToolbar = function() {
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
			$log.log("search()",$scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
			loadCra($scope.criterias.selected.employee, $scope.criterias.selected.year.label, $scope.criterias.selected.month.id);
		};

		$scope.init = function() {
			if($rootScope.profile.role === $rootScope.Roles.EMPLOYEE) {
				loadCra(initialEmployee, initialYear.label, initialMonth.id);
			}

		};

		var loadCra = function(username, year, month) {
			var route = jsRoutes.controllers.JCras.fetch(username, year, month);
			$log.log('route', route)
			$http({
				method: route.method,
				url: route.url
			})
				.success(function(cra, status, headers, config) {
					$log.log('cra', cra);
					$scope.cra = cra;
				});
		};

	}]);


