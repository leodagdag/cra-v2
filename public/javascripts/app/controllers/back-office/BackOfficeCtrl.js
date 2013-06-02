app.controller('BackOfficeCtrl', ['$scope', '$http', '$log', '$location','$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';

        $scope.subSections = {
            'users': 'assets/html/views/back-office/users.html',
            'customers': 'assets/html/views/back-office/customers.html',
            'missions': 'assets/html/views/back-office/missions.html'
        };

        $scope.activeSubSection = {
            name: $routeParams.subSection || null,
            page: $scope.subSections[$routeParams.subSection] || null
        };
        $scope.activateSubSection = function(name) {
            $location.path('/back-office/' + name);
        };
	}]);