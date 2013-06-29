angular.module('allowanceTypeFilter', [])
	.filter('allowanceType', function() {
		'use strict';
		return function(allowanceType) {
			switch(allowanceType) {
				case "FIXED":
					return "Séjour"
				case "ZONE":
					return "Zone"
				default :
					return "";
			}
		};
	});

