app.controller('BackOfficeUserMissionsCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', '$rootScope',
	function($scope, $http, $log, $location, $routeParams, $rootScope) {
		'use strict';

		$scope.customers = [];
		$scope.missions = [];
		$scope.users = [];
		$scope.form = {};
		$scope.affectedMissions = [];
		$scope.errors = {};

		var loadEmployeess = function() {
			var route = jsRoutes.controllers.JUsers.employees();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(users) {
					$scope.users = _(users).sortBy("username").valueOf();
				});
		};

		var loadCustomers = function() {
			var route = jsRoutes.controllers.JCustomers.withoutGenesis();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(customers) {
					$scope.customers = _(customers).sortBy('name').valueOf();
				});
		};

		$scope.init = function() {
			loadEmployeess();
			loadCustomers();
		};


		$scope.loadAffectedMissions = function() {
			var route = jsRoutes.controllers.JUsers.customerAffectedMissions($scope.form.user.username);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(affectedMissions) {
					$scope.affectedMissions = _(affectedMissions).sortBy('startDate').reverse().valueOf();
				});

		};

		$scope.loadCustomerMissions = function(mission) {
			var route = jsRoutes.controllers.JMissions.customerMissions($scope.form.customer.id);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(customerMissions) {
					$scope.customerMissions = _(customerMissions).sortBy('startDate').reverse().valueOf();
					if(mission) {
						$scope.form.mission = _($scope.customerMissions).find({id: mission.id});
					}
				});

		};

		$scope.editAffectedMission = function(am) {
			var route = jsRoutes.controllers.JMissions.customerByMissionId(am.mission.id);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(customer) {
					$scope.form.customer = _($scope.customers).find({id: customer.id}).valueOf();
					$scope.loadCustomerMissions(am.mission);
					$scope.form.startDate = moment(am.startDate).format("DD/MM/YYYY");
					$scope.form.endDate = am.endDate ? moment(am.endDate).format("DD/MM/YYYY") : null;
					$scope.form.zoneFee = am.allowanceType === "ZONE";
					$scope.form.feeAmount = am.feeAmount;

				});

		};

		$scope.save = function() {
			$scope.errors = {};
			var route = jsRoutes.controllers.JUsers.saveAffectedMission(),
				f = $scope.form,
				data = {
					userId: f.user.id,
					missionId: f.mission.id,
					startDate: moment(f.startDate, 'DD/MM/YYYY').valueOf(),
					endDate: f.endDate ? moment(f.endDate, 'DD/MM/YYYY').valueOf() : null,
					feeZone: f.feeZone,
					feeAmount: f.feeAmount
				};
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false,
				'data': data
			})
				.success(function(result) {
					$rootScope.onSuccess("Mission sauvegardée.");
					$scope.loadAffectedMissions();
				})
				.error(function(errors){
					_(errors).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};

		$scope.resetMission = function() {
			var user = $scope.form.user
			$scope.form = {};
			$scope.form.user = _($scope.users).find({id: user.id}).valueOf();
		}

	}]);