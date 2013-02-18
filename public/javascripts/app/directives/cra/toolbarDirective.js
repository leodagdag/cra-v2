angular.module('craToolbarDirective', [])
	.directive('craToolbar', function() {
		'use strict';
		return {
			restrict: 'EA',
			replace: false,
			templateUrl: 'public/html/templates/cra/toolbar.html'
		};
	});
