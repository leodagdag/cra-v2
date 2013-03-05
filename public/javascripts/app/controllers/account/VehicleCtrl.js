app.controller('VehicleCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'VehicleBrandConst', 'VehicleCarPowerConst', 'VehicleMotorcyclePowerConst',
	function VehicleCtrl($scope, $http, $log, $location, $routeParams, VehicleBrandConst, VehicleCarPowerConst, VehicleMotorcyclePowerConst) {
		var Vehicle = function(form) {
			this.username = $scope.profile.username;
			this.vehicleType = form.vehicleType;
			this.power = form.power;
			this.brand = form.brand;
			this.matriculation = form.matriculation;
			this.startDate = moment(form.startDate, 'DD/MM/YYYY').valueOf;
		};
		$scope.vehicleType = 'car';
		$scope.brands = VehicleBrandConst;
		$scope.powers = VehicleCarPowerConst;
		$scope.form = {
			vehicleType: $scope.vehicleType,
			power: null,
			brand: null,
			matriculation: null,
			startDate: null
		};

		var resetForm = function(vehicleType) {
			$scope.form = {
				vehicleType: vehicleType,
				power: null,
				brand: null,
				matriculation: null
			};
			$scope.powers = (vehicleType === 'car') ? VehicleCarPowerConst : VehicleMotorcyclePowerConst;
		};

		$scope.toggleVehicleType = function(vehicleType) {
			$scope.vehicleType = vehicleType;
			resetForm(vehicleType);
		};

		$scope.save = function() {
			var vehicle = new Vehicle($scope.form);
			var route = jsRoutes.controllers.JVehicles.saveVehicle();
			$http({
				'method': route.method,
				'url': route.url,
				'data': vehicle
			})
				.success(function(vehicle, status, headers, config) {
					$log.debug('vehicle', vehicle);
				})
				.error(function(error, status, headers, config) {
					$log.error('error', error);
				});
		};
	}]);

