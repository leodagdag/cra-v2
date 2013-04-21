angular.module('kilometerFilter', [])
	.filter('kilometer', function() {
		'use strict';
		return function(km) {
			return (km) ? _.str.numberFormat(km, 2, ',') + ' km' : '';
		};
	});

