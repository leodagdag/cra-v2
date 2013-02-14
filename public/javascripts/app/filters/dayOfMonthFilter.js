angular.module('dayOfMonthFilter', []).filter('dayOfMonth', function() {
	'use strict';
	return function(date) {
		return (date) ? moment(date).format("DD/MM") : "";
	}
});

