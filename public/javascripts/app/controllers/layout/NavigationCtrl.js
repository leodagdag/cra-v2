app.controller('NavigationCtrl', ['$scope', '$http', '$log', '$location',
	function($scope, $http, $log, $location) {
		'use strict';
		$scope.isNav = function(nav) {
			return (nav === 'cra' && _.str.startsWith($location.path(), '/day')) ||
				_.str.startsWith($location.path(), '/' + nav);
		};
	}]);
