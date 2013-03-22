angular.module('typeOfClaimFilter', [])
	.filter('typeOfClaim', function() {
		'use strict';
		var TYPE_OF_CLAIMS = {
			JOURNEY: "déplacement",
			TAXI: "taxi",
			TOLL: "péage",
			PARKING: "parking",
			RENT_CAR: "location de voiture",
			TOTAL: "total"
		}
		return function(typeOfClaim) {
			return TYPE_OF_CLAIMS[typeOfClaim];
		}
	});

