angular.module('vehicleTypeFilter', [])
	.filter('vehicleType', function() {
		'use strict';
		return function(vehicleType) {
			return vehicleType === 'car' ? 'voiture' : 'moto';
		}
	});

