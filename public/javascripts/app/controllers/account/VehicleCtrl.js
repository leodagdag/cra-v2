app.controller('VehicleCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'CarBrandConst', 'MotorcycleBrandConst', 'VehicleCarPowerConst', 'VehicleMotorcyclePowerConst', 'YearsConst', 'MonthsConst',
	function VehicleCtrl($rootScope, $scope, $http, $log, $location, $routeParams, CarBrandConst, MotorcycleBrandConst, VehicleCarPowerConst, VehicleMotorcyclePowerConst, YearsConst, MonthsConst) {
		'use strict';
		$scope._ = _;
		var Vehicle = function(form) {
			this.userId = $scope.profile.id;
			this.vehicleType = form.vehicleType;
			this.power = form.power;
			this.brand = form.brand;
			this.matriculation = form.matriculation;
			this.year = form.year;
			this.month = form.month;
		};

		var Form = function(vehicleType) {
			this.vehicleType = vehicleType || $scope.vehicleType;
			this.power = null;
			this.brand = null;
			this.matriculation = null;
			this.year = null;
			this.month = null;
		};
		$scope.years = YearsConst;
		$scope.months = MonthsConst;
		$scope.vehicleType = 'car';
		$scope.brands = CarBrandConst;
		$scope.powers = VehicleCarPowerConst;
		$scope.form = new Form();

		$scope.errors = {
			global: null,
			power: null,
			brand: null,
			matriculation: null,
			month: null,
			year: null,
			validityDate: null
		};

		$scope.toggleVehicleType = function(vehicleType) {
			$scope.vehicleType = vehicleType;
			$scope.form = new Form($scope.vehicleType);
			$scope.powers = (vehicleType === 'car') ? VehicleCarPowerConst : VehicleMotorcyclePowerConst;
			$scope.brands = (vehicleType === 'car') ? CarBrandConst : MotorcycleBrandConst;
		};

		$scope.save = function() {
			$scope.errors = {};
			var vehicle = new Vehicle($scope.form);
			var route = jsRoutes.controllers.JVehicles.save();
			$http({
				'method': route.method,
				'url': route.url,
				'data': vehicle
			})
				.success(function(vehicle, status, headers, config) {
					$rootScope.onSuccess("Votre véhicule a été sauvegardé.");
					$scope.form = new Form();
					$scope.loadActive();
				})
				.error(function(errors, status, headers, config) {
					_(errors).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};

		/* Active Vehicle */
		$scope.activeVehicle = {};

		$scope.loadActive = function() {
			var route = jsRoutes.controllers.JVehicles.active($scope.profile.id);
			$http({
				'method': route.method,
				'url': route.url
			})
				.success(function(vehicle, status, headers, config) {
					$scope.activeVehicle = vehicle;
				})
				.error(function(error, status, headers, config) {
				});
		};

		$scope.deactivate = function(id) {
			if(window.confirm('Êtes-vous sûr de vouloir désactiver votre véhicule ?')) {
				var route = jsRoutes.controllers.JVehicles.deactivate();
				$http({
					'method': route.method,
					'url': route.url,
					'data': {'id': id}
				})
					.success(function(result, status, headers, config) {
						$scope.activeVehicle = {};
						$rootScope.onSuccess("Le véhicle a été désactivé.");
					});
			}
		};
	}]);

