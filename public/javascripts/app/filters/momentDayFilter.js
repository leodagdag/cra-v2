angular.module('momentDayFilter', []).filter('momentDay', function(){
	'use strict';
	return function(momentDay){
		switch (momentDay){
			case 'DAY':
				return 'journée';
			case 'MORNING':
				return 'matin';
			case 'AFTERNOON':
				return 'après-midi';
			default:
				return "j'sais pas";
		}
	}
});

