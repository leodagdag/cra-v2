angular.module('typeOfClaimFilter', [])
	.filter('typeOfClaim', function() {
		'use strict';
		var TYPE_OF_CLAIMS = {
			JOURNEY: "frais kilométriques",
			FUEL: 'Carburant',
			TOLL: 'Péage',
			PARKING: 'Parking',
			PUBLIC_TRANSPORT: 'Métro, bus',
			TRAIN_PLANE: 'Train, avion',
			TAXI: 'Taxi',
			RENT_CAR: 'Location de voiture',
			BREAKFAST: 'Petit déjeuner',
			LUNCH: 'Déjeuner',
			DINER: 'Diner',
			HOSTEL: 'Hotels',
			INVITATION: 'Invitations',
			CONSUMPTION: 'Consommations',
			PHONE: 'Tél, poste',
			MISCELLANEOUS: 'Divers',
			FIXED_FEE: "forfait de séjour",
			ZONE_FEE: "forfait de zone",
			TOTAL: "total"
		};
		return function(typeOfClaim) {
			return TYPE_OF_CLAIMS[typeOfClaim] || '[' + typeOfClaim + ']';
		};
	});

