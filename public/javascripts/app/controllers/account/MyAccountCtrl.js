app.controller('MyAccountCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'profile',
	function MyAccountCtrl($scope, $http, $log, $location, $routeParams, profile) {
		$scope.profile = profile.data;
		$scope.subSections = {
			'general': 'public/html/views/my-account/general.html',
			'vehicle': 'public/html/views/my-account/vehicle.html',
			'affected-missions': 'public/html/views/my-account/affected-missions.html',
			'part-time': 'public/html/views/my-account/part-time.html'
		};

		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.activateSubSection = function(name) {
			$scope.activeSubSection.name = name;
			$scope.activeSubSection.page = $scope.subSections[name];
			$location.path('/my-account/' + name);
		}
	}]);

app.controller('MyAccountGeneralCtrl', ['$scope', '$http', '$log', '$location', 'AccountRes', 'ManagerRes',
	function MyAccountGeneralCtrl($scope, $http, $log, $location, AccountResource, ManagerResource) {
		$scope.managers = ManagerResource.query();
		$scope.account = AccountResource.get({id: $scope.profile.id});

		$scope.save = function() {
			$scope.account.$update();
		}
	}]);

app.controller('MyAccountPasswordCtrl', ['$scope', '$http', '$log', '$location', 'AccountRes',
	function MyAccountPasswordCtrl($scope, $http, $log, $location, AccountResource) {
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
					$log.log('data', data);
					$location.url("/")
				})
				.error(function(errors, status, headers, config) {
					$log.log('errors', errors);
					_(errors).forEach(function(err, key){
						$scope.errors[key] = err.join('<br>');
					})
				})
		}


	}]);

app.controller('MyAccountAffectedMissionsCtrl', ['$scope', '$http', '$log', '$location',
	function MyAccountAffectedMissionsCtrl($scope, $http, $log, $location) {

	}]);
