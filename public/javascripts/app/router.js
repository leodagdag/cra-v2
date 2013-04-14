app.factory('Profile', ['$http', '$log',
	function($http, $log) {
		var route = jsRoutes.controllers.Authentication.profile();
		return $http({
			method: route.method,
			url: route.url
		});
	}]);

app.config(['$routeProvider',
	function($routeProvider) {

		$routeProvider
			.when('/cra/:username/:year/:month', {
				templateUrl: 'assets/html/views/cra/cra.html',
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/cra/:username', {
				templateUrl: 'assets/html/views/cra/cra.html',
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/cra', {
				templateUrl: 'assets/html/views/cra/cra.html',
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/cra/claim/synthesis/:year/:month', {
				templateUrl: 'assets/html/views/cra/claimSynthesis.html',
				controller: 'ClaimSynthesisCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/day/:username/:craId/:year/:month/:days', {
				templateUrl: 'assets/html/views/cra/day.html',
				controller: 'DayCtrl',
				resolve: {'profile': 'Profile'}
			})
			.when('/absence', {
				templateUrl: 'assets/html/views/absence/absence.html',
				controller: 'AbsenceCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/absence/:subSection', {
				templateUrl: 'assets/html/views/absence/absence.html',
				controller: 'AbsenceCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/claim', {
				templateUrl: 'assets/html/views/claim/claim.html',
				controller: 'ClaimCtrl',
				resolve: {'profile': 'Profile' }
			})

			.when('/remuneration', {
				templateUrl: 'assets/html/views/remuneration/remuneration.html',
				controller: 'RemunerationCtrl'
			})
			.when('/parameter', {
				templateUrl: 'assets/html/views/parameter/parameter.html',
				controller: 'ParameterCtrl'
			})
			.when('/backoffice', {
				templateUrl: 'assets/html/views/back-office/back-office.html',
				controller: 'BackOfficeCtrl'
			})
			.when('/my-account', {
				templateUrl: 'assets/html/views/my-account/my-account.html',
				controller: 'MyAccountCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/my-account/:subSection', {
				templateUrl: 'assets/html/views/my-account/my-account.html',
				controller: 'MyAccountCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when('/', {
				templateUrl: 'assets/html/views/changeLog.html',
				controller: 'MainCtrl'
			});


	}
]);
