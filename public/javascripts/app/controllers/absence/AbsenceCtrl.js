app.controller('AbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'profile',
	function AbsenceCtrl($scope, $http, $log, $location, $routeParams, profile) {
		$scope.profile = profile.data;

		$scope.subSections = {
			'day': 'public/html/views/absence/day.html',
			'period': 'public/html/views/absence/period.html'
		};
		$scope.activeSubSection = {
			name: $routeParams.subSection || null,
			page: $scope.subSections[$routeParams.subSection] || null
		};
		$scope.activateSubSection = function(name) {
			$scope.activeSubSection.name = name;
			$scope.activeSubSection.page = $scope.subSections[name];
			$location.path('/absence/' + name);
		};

		$scope.init = function() {
			if(!$routeParams.subSection) {
				$location.path('/absence/day');
			}       else {
                var route = jsRoutes.controllers.JMissions.absences();
                $http({
                    method: route.method,
                    url: route.url
                })
                    .success(function(missions, status, headers, config) {
                        $log.debug('missions', missions);
                        $scope.missions = missions;
                    })
                    .error(function(error, status, headers, config){
                        $log.debug('error', error);
                    });
            }
		};
	}]);
