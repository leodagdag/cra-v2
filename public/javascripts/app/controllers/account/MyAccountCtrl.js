app.controller('MyAccountCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
	function MyAccountCtrl($scope, $http, $log, $location, $routeParams) {
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
		$scope.account = AccountResource.get(function(account) {
			$log.log('account', account);
			$log.log('$scope.account', $scope.account);
		});

	}]);


app.controller('MyAccountVehicleCtrl', ['$scope', '$http', '$log', '$location',
	function MyAccountVehicleCtrl($scope, $http, $log, $location) {

	}]);

app.controller('MyAccountAffectedMissionsCtrl', ['$scope', '$http', '$log', '$location',
	function MyAccountAffectedMissionsCtrl($scope, $http, $log, $location) {

	}]);
