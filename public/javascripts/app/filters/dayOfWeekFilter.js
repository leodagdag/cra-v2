angular.module('dayOfWeekFilter', [])
	.filter('dayOfWeek', function() {
		'use strict';
		return function(dayOfWeek) {
			switch(dayOfWeek) {
				case 1:
					return 'lundi';
				case 2:
					return 'mardi';
				case 3:
					return 'mercredi';
				case 4:
					return 'jeudi';
				case 5:
					return 'vendredii';
				case 6:
					return 'samedi';
				case 7:
					return 'dimanche';
				default :
					return "j'sais pas ;)";
			}
		};
	});

