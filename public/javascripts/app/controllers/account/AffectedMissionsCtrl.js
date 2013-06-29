app.controller('MyAccountAffectedMissionsCtrl', ['$scope', '$http', '$log', '$location',
	function($scope, $http, $log, $location) {
		'use strict';
		$scope.affectedMissions = [];
		$scope.filter = {
			sortBy:"-startDate"
		};
		$scope.sortBys = [
			{key: '+startDate', label: 'Date de début (asc)'},
			{key: '-startDate', label: 'Date de début (desc)'},
			{key: '+endDate', label: 'Date de fin (asc)'},
			{key: '-endDate', label: 'Date de fin (desc)'},
			{key: '+label', label: 'Mission (asc)'},
			{key: '-label', label: 'Mission (desc)'}
		];

		$scope.sortByChange = function() {
			$scope.affectedMissions = sort($scope.affectedMissions);
		};

		var sort = function(list) {
			var field = $scope.filter.sortBy.substr(1),
				direction = $scope.filter.sortBy.substr(0, 1) === '+' ? 'asc' : 'desc',
				result = _(list).sortBy(field);
			if(direction === 'desc') {
				result.reverse();
			}
			return result.valueOf();
		};

		$scope.init = function(){
			var route = jsRoutes.controllers.JUsers.allAffectedMissions($scope.profile.username);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(affectedMissions) {
					$scope.affectedMissions = sort(_(affectedMissions)
						.map(function(affectedMission){
							affectedMission.label = affectedMission.mission.label
							return affectedMission;
						})
						.valueOf());
				})
		}
	}]);