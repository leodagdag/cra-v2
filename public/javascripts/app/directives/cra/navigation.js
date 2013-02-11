angular.module('craNavigationDirective', []).
	directive('craNavigation', function () {
		return {
			restrict:'EA',
			replace: false,
			templateUrl:'public/views/templates/cra/navigation.html'
		};
	});
