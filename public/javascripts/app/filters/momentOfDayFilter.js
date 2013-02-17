angular.module('momentOfDayFilter', [])
	.filter('momentOfDay', function() {
		'use strict';
		return function(momentDay) {
			switch(momentDay) {
				case 'day':
					return 'journée';
				case 'morning':
					return 'matin';
				case 'afternoon':
					return 'après-midi';
				default:
					return "j'sais pas ;)";
			}
		}
	});

