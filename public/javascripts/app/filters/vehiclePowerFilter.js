angular.module('vehiclePowerFilter', [])
	.filter('vehiclePower', function() {
		'use strict';
		return function(power, vehicleType) {
			switch(vehicleType) {
				case 'car':
					return power + ' cv';
				case 'motorcycle':
					switch(power) {
						case 0:
							return 'De 0 à 500 cm²';
						case 501:
							return 'Plus de 500 cm²';
					}
			}
			return null;
		};
	});

