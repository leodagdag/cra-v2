angular.module('craToolbarDirective', []).
	directive('craToolbar', function () {
		return {
			restrict:'EA',
			replace: false,
			templateUrl:'public/views/templates/cra/toolbar.html'
		};
	});
