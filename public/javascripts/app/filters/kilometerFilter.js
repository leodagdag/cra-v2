angular.module('kilometerFilter', [])
	.filter('kilometer', function() {
		'use strict';
		return function(km) {
			if(km) {
				return _.str.numberFormat(km, 2, ',') + ' km';
			}
			return "";
		}
	});

