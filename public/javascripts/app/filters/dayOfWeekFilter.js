angular.module('dayOfWeekFilter', [])
	.filter('dayOfWeek', function() {
		'use strict';
		return function(weekDay) {
			if(weekDay === 7) {
				return moment.weekdays[0];
			}
			return moment.weekdays[weekDay];
		}
	});

