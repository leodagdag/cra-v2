angular.module('weekDayFilter', []).filter('weekDay', function(){
	'use strict';
	return function(weekDay){
		if(weekDay === 7){
			return moment.weekdays[0];
		}
		return moment.weekdays[weekDay];
	}
});

