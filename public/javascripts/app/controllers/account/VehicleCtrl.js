app.controller('VehicleCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'VehicleBrandConst', 'VehicleCarPowerConst', 'VehicleMotorcyclePowerConst',
	function VehicleCtrl($rootScope, $scope, $http, $log, $location, $routeParams, VehicleBrandConst, VehicleCarPowerConst, VehicleMotorcyclePowerConst) {
		$scope._ = _;
		var Vehicle = function(form) {
			this.userId = $scope.profile.id;
			this.vehicleType = form.vehicleType;
			this.power = form.power;
			this.brand = form.brand;
			this.matriculation = form.matriculation;
			this.startDate = moment(form.startDate, 'DD/MM/YYYY').valueOf();
		};
		var Form = function(vehicleType) {
			this.vehicleType = vehicleType || 'car';
			this.power = null;
			this.brand = null;
			this.matriculation = null;
			this.startDate = null;
		};

		$scope.vehicleType = 'car';
		$scope.brands = VehicleBrandConst;
		$scope.powers = VehicleCarPowerConst;
		$scope.form = new Form();

		$scope.toggleVehicleType = function(vehicleType) {
			$scope.vehicleType = vehicleType;
			$scope.form = new Form($scope.vehicleType);
			$scope.powers = (vehicleType === 'car') ? VehicleCarPowerConst : VehicleMotorcyclePowerConst;
		};

		$scope.save = function() {
			var vehicle = new Vehicle($scope.form);
			var route = jsRoutes.controllers.JVehicles.save();
			$http({
				'method': route.method,
				'url': route.url,
				'data': vehicle
			})
				.success(function(vehicle, status, headers, config) {
					$rootScope.onSuccess("Votre véhicule a été sauvegardé.");
					$scope.loadActive();
				})
				.error(function(error, status, headers, config) {
				});
		};

		/* Active Vehicle */
		$scope.activeVehicle = {};

		$scope.loadActive = function(){
			var route = jsRoutes.controllers.JVehicles.active($scope.profile.id);
			$http({
				'method': route.method,
				'url': route.url
			})
				.success(function(vehicle, status, headers, config) {
					$log.debug(vehicle);
					$scope.activeVehicle = vehicle;
				})
				.error(function(error, status, headers, config) {
				});
		}
	}]);

