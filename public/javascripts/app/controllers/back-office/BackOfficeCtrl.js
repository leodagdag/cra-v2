app.controller('BackOfficeCtrl', ['$scope', '$http', '$log', '$location','$routeParams',
	function($scope, $http, $log, $location, $routeParams) {
		'use strict';

        $scope.subSections = {
            'users': 'assets/html/views/back-office/users.html',
            'user-missions': 'assets/html/views/back-office/user-missions.html',
            'customers': 'assets/html/views/back-office/customers.html'
        };

        $scope.currentSubSection = {
            name: $routeParams.subSection || null,
            page: $scope.subSections[$routeParams.subSection] || null
        };

        $scope.goToSubSection = function(name) {
            $location.path('/back-office/' + name);
        };
	}]);