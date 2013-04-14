angular.module('craToolbarDirective', [])
	.directive('craToolbar', function() {
		'use strict';
		return {
			restrict: 'EA',
			replace: false,
			templateUrl: 'assets/html/templates/cra/toolbar.html'
		};
	});
