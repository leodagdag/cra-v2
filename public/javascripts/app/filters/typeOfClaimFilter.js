angular.module('typeOfClaimFilter', [])
	.filter('typeOfClaim', function() {
		'use strict';
		var TYPE_OF_CLAIMS = {
			JOURNEY: "frais kilométrique",
			TAXI: "taxi",
			TOLL: "péage",
			PARKING: "parking",
			RENT_CAR: "location de voiture",
			FIXED_FEE: "forfait fixe",
			ZONE_FEE: "forfait de zone",
			TOTAL: "total"
		};
		return function(typeOfClaim) {
			return TYPE_OF_CLAIMS[typeOfClaim] || '[' + typeOfClaim + ']';
		};
	});

