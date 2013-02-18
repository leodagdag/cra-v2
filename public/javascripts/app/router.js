
app.config(['$routeProvider',
	function($routeProvider) {
		$routeProvider
			.when("/cra/:username/:year/:month", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra/:username", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl'
			})
			.when("/cra", {
				templateUrl: "public/html/views/cra/cra.html",
				controller: 'CraCtrl',
				resolve:{'profile' : 'Profile' }
			})
			.when("/day/:username/:id/:days", {
				templateUrl: "public/html/views/cra/day.html",
				controller: 'DayCtrl',
				resolve:{'profile' : 'Profile'}
			})
			.when("/absence", {
				templateUrl: "public/html/views/absence.html",
				controller: 'AbsenceCtrl'
			})
			.when("/claim", {
				templateUrl: "public/html/views/claim.html",
				controller: 'ClaimCtrl'
			})
			.when("/remuneration", {
				templateUrl: "public/html/views/remuneration.html",
				controller: 'RemunerationCtrl'
			})
			.when("/parameter", {
				templateUrl: "public/html/views/parameter.html",
				controller: 'ParameterCtrl'
			})
			.when("/backoffice", {
				templateUrl: "public/html/views/back-office.html",
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
