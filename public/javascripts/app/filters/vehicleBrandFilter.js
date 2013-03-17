angular.module('vehicleBrandFilter', [])
	.filter('vehicleBrand', function() {
		'use strict';
		return function(brand) {
			return _.str.humanize(brand);
		}

	});

