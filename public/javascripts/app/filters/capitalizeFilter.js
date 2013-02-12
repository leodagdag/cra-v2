angular.module('capitalizeFilter', [])
	.filter('capitalize', function(){
	'use strict';
	return function(input){
		return _.str.capitalize(input);
	}
});

