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
				case 'special':
					return "";
				default:
					return "j'sais pas ;)";
			}
		};
	});

