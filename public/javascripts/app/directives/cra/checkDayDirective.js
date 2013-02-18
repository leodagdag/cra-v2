angular.module('craCheckDayDirective', [])
	.directive('craCheckDay', function() {
		'use strict';
		return {
			restrict: 'EA',
			replace: true,
			template: '<i class="icon-check-empty pull-left" data-ng-show="showCheckDay(day)" data-ng-click="toggleDay(day.date)"></i>',
			link: function(scope, element, attrs) {

				element.bind('click', toggle);

				element.bind('$destroy', function() {
					element.unbind('click');
				});

				function toggle() {
					var isChecked = element.hasClass('icon-check');
					element.removeClass(isChecked ? 'icon-check' : 'icon-check-empty');
					element.addClass(isChecked ? 'icon-check-empty' : 'icon-check');
				}
			}
		};
	});
