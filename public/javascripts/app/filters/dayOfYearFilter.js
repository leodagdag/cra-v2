angular.module('dayOfYearFilter', [])
	.filter('dayOfYear', function() {
		'use strict';
		return function(date) {
			return (date) ? moment(date).format("DD/MM/YY") : "";
		};
	});

