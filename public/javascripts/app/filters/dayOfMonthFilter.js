angular.module('dayOfMonthFilter', []).filter('dayOfMonth', function() {
	'use strict';
	return function(date) {
		return moment(date, "DD/MM/YYYY").format("DD/MM");
	}
});

