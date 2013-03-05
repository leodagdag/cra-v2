angular.module('capitalizeFilter', [])
	.filter('capitalize', function() {
		'use strict';
		return function(input) {
			return (input) ? _.str.capitalize(input.toLowerCase()) : "";
		}
	});

