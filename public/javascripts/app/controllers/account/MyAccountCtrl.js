app.controller('MyAccountCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', '$routeParams', 'profile',
	function($rootScope, $scope, $http, $log, $location, $routeParams, profile) {
		'use strict';
		$scope.profile = profile.data;
		$scope.subSections = {
			'general': 'assets/html/views/my-account/general.html',
			'vehicle': 'assets/html/views/my-account/vehicle.html',
			'affected-missions': 'assets/html/views/my-account/affected-missions.html',
			'part-time': 'assets/html/views/my-account/part-time.html'
		};

		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.goToSubSection = function(name) {
			$location.path('/my-account/' + name);
		};
	}]);

app.controller('MyAccountGeneralCtrl', ['$rootScope', '$scope', '$http', '$log', '$location', 'AccountRes', 'ManagerRes',
	function($rootScope, $scope, $http, $log, $location, AccountResource, ManagerResource) {
		'use strict';
		$scope.managers = ManagerResource.query();
		$scope.account = AccountResource.get({id: $scope.profile.id});
		$scope.errors = {
			global: null,
			firstName: null,
			lastName: null,
			trigramme: null,
			email: null
		};
		$scope.save = function() {
			$scope.errors = {};
			$scope.account.$update(
				function(result) {
					$rootScope.onSuccess("Votre profil a été sauvegardé.");
				},
				function(errors) {
					$log.log('errors', errors);
					_(errors.data).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};
	}]);

app.controller('MyAccountPasswordCtrl', ['$scope', '$http', '$log', '$location',
	function($scope, $http, $log, $location) {
		'use strict';
		$scope.form = {
			oldPassword: null,
			newPassword: null,
			confirmPassword: null
		};
		$scope.errors = {
			global: null,
			oldPassword: null,
			newPassword: null,
			confirmPassword: null
		};

		$scope.save = function() {
			$scope.errors = {};
			var route = jsRoutes.controllers.JAccounts.password();
			$http({
				method: route.method,
				url: route.url,
				data: $scope.form
			})
				.success(function(data, status, headers, config) {
					$location.url("/");
				})
				.error(function(errors, status, headers, config) {
					$log.log('errors', errors);
					_(errors).forEach(function(err, key) {
						$scope.errors[key] = err.join('<br>');
					});
				});
		};


	}]);
