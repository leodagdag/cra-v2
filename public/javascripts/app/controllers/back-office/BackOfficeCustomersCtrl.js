app.controller('BackOfficeCustomersCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', '$rootScope',
	function($scope, $http, $log, $location, $routeParams, $rootScope) {
		'use strict';

		$scope.customers = [];
		$scope.customer = {};
		$scope.missions = [];
		$scope.mission = {};

		$scope.sortBys = [
			{key: '+code', label: 'Code (asc)'},
			{key: '-code', label: 'Code (desc)'},
			{key: '+name', label: 'Nom (asc)'},
			{key: '-name', label: 'Nom (desc)'}
		];

		$scope.filter = {
			'sortBy': $scope.sortBys[0].key
		};

		$scope.sortByChange = function() {
			$scope.customers = sort($scope.customers);
		};

		var sort = function(list) {
			var field = $scope.filter.sortBy.substr(1),
				direction = $scope.filter.sortBy.substr(0, 1) === '+' ? 'asc' : 'desc',
				result = _(list).sortBy(field);
			if(direction === 'desc') {
				result.reverse();
			}
			return result.valueOf();
		};


		var loadMissions = function(customer) {
			var route = jsRoutes.controllers.JMissions.customerMissions(customer.id);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(missions, status, headers, config) {
					$scope.missions = missions;
				});
		};

		$scope.loadCustomers = function() {
			var route = jsRoutes.controllers.JCustomers.withoutGenesis();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(customers, status, headers, config) {
					$scope.customers = sort(customers);
				});
		};

		$scope.editCustomer = function(customer) {
			var route = jsRoutes.controllers.JCustomers.fetch(customer.code);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(customer, status, headers, config) {
					$scope.customer = customer;
					loadMissions($scope.customer);
				});
			loadMissions(customer);
		};

		$scope.editMission = function(mission) {
			var route = jsRoutes.controllers.JMissions.fetch(mission.id);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(mission, status, headers, config) {
					$scope.mission = mission;
				});
		};

		$scope.saveCustomer = function() {
			var route = jsRoutes.controllers.JCustomers.save();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false,
				'data': $scope.customer
			})
				.success(function(customer, status, headers, config) {
					$scope.customer = customer;
					$scope.loadCustomers();
					$rootScope.onSuccess("Client sauvegardé.");
				});
		};

		$scope.saveMission = function() {
			var data = $scope.mission;
			data.customerId = $scope.customer.id;
			var route = jsRoutes.controllers.JMissions.save();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false,
				'data': data
			})
				.success(function(mission, status, headers, config) {
					$scope.mission = mission;
					loadMissions($scope.customer);
					$rootScope.onSuccess("Mission sauvegardée.");
				});
		};

		$scope.newCustomer = function() {
			$scope.customer = {};
			$scope.missions = [];
			$scope.mission = {};
		};

		$scope.newMission = function() {
			$scope.mission = {};
		};

	}]);