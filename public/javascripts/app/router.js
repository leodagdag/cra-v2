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
			.when("/cra/:username/:year/:month", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when("/cra/:username", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when("/cra", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when("/day/:username/:craId/:year/:month/:days", {
				templateUrl: "public/html/views/cra/day.html",
				controller: 'DayCtrl',
				resolve: {'profile': 'Profile'}
			})
			.when("/absence", {
				templateUrl: "public/html/views/absence/absence.html",
				controller: 'AbsenceCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when("/absence/:subSection", {
				templateUrl: "public/html/views/absence/absence.html",
				controller: 'AbsenceCtrl',
				resolve: {'profile': 'Profile' }
			})
			.when("/claim", {
				templateUrl: "public/html/views/claim/claim.html",
				controller: 'ClaimCtrl',
                resolve: {'profile': 'Profile' }
			})
			.when("/remuneration", {
				templateUrl: "public/html/views/remuneration/remuneration.html",
				controller: 'RemunerationCtrl'
			})
			.when("/parameter", {
				templateUrl: "public/html/views/parameter/parameter.html",
				controller: 'ParameterCtrl'
			})
			.when("/backoffice", {
				templateUrl: "public/html/views/back-office/back-office.html",
				controller: 'BackOfficeCtrl'
			})
			.when("/my-account", {
				templateUrl: "public/html/views/my-account/my-account.html",
				controller: 'MyAccountCtrl'
			})
			.when("/my-account/:subSection", {
				templateUrl: "public/html/views/my-account/my-account.html",
				controller: 'MyAccountCtrl'
			});
	}]);
