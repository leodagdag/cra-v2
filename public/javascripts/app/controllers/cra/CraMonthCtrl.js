app.controller('CraMonthCtrl', ['$scope', '$http', '$log', '$location',
	function CraMonthCtrl($scope, $http, $log, $location) {

		$scope.weeks = [
			{
				number: 5,
				days: [
					{
						date: '28/01/2013',
						inPastOrFuture: true

					},
					{
						date: '29/01/2013',
						inPastOrFuture: true
					},
					{
						date: '30/01/2013',
						inPastOrFuture: true
					},
					{
						date: '31/01/2013',
						inPastOrFuture: true
					},
					{
						date: '01/02/2013'
					},
					{
						date: '02/02/2013',
						isSaturday: true
					},
					{
						date: '03/02/2013',
						isSunday: true
					}
				]
			},
			{
				number: 6,
				days: [
					{
						date: '04/02/2013',
						morning: {
							label: 'M1',
							type: 'customer',
							special: false
						},
						comment: 'commentaire'
					},
					{
						date: '05/02/2013',
						afternoon: {
							label: 'SPECIAL',
							type: 'special',
							special: false
						}

					},
					{
						date: '06/02/2013'
					},
					{
						date: '07/02/2013'
					},
					{
						date: '08/02/2013'
					},
					{
						date: '09/02/2013',
						isSaturday: true
					},
					{
						date: '10/02/2013',
						isSunday: true
					}
				]
			},
			{
				number: 7,
				days: [
					{
						date: '11/02/2013',
						morning: {
							label: 'M1',
							type: 'customer',
							special: false
						},
						comment: 'commentaire'
					},
					{
						date: '12/02/2013',
						afternoon: {
							label: 'SPECIAL',
							type: 'special',
							special: false
						}

					},
					{
						date: '13/02/2013'
					},
					{
						date: '14/02/2013'
					},
					{
						date: '15/02/2013'
					},
					{
						date: '16/02/2013',
						isSaturday: true
					},
					{
						date: '17/02/2013',
						isSunday: true
					}
				]
			},
			{
				number: 8,
				days: [
					{
						date: '18/02/2013',
						morning: {
							label: 'M1',
							type: 'customer',
							special: false
						},
						comment: 'commentaire'
					},
					{
						date: '19/02/2013',
						afternoon: {
							label: 'SPECIAL',
							type: 'special',
							special: false
						}

					},
					{
						date: '20/02/2013'
					},
					{
						date: '21/02/2013'
					},
					{
						date: '22/02/2013'
					},
					{
						date: '23/02/2013',
						isSaturday: true
					},
					{
						date: '24/02/2013',
						isSunday: true
					}
				]
			},
			{
				number: 9,
				days: [
					{
						date: '25/02/2013',
						morning: {
							label: 'M1',
							type: 'customer',
							special: false
						},
						comment: 'commentaire'
					},
					{
						date: '26/02/2013',
						afternoon: {
							label: 'SPECIAL',
							type: 'special',
							special: false
						}

					},
					{
						date: '27/02/2013'
					},
					{
						date: '28/02/2013'
					},
					{
						date: '01/03/2013',
						inPastOrFuture: true
					},
					{
						date: '02/03/2013',
						isSaturday: true,
						inPastOrFuture: true
					},
					{
						date: '03/03/2013',
						isSunday: true,
						inPastOrFuture: true
					}
				]
			}
		];


	}]);
