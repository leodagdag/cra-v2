angular.module('monthOfYearFilter', [])
	.filter('monthOfYear', function() {
		'use strict';
		return function(month) {
			return moment(month.toString(), "M").format("MMMM");
		}
	});

