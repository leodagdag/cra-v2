angular.module('localTimeFilter', [])
	.filter('localTime', function() {
		'use strict';
		return function(lt) {
			return (lt) ? moment(lt).format('HH:mm') : "";
		}
	});

