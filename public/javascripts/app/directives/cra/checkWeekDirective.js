angular.module('craCheckWeekDirective', [])
	.directive('craCheckWeek', function() {
		'use strict';
		return {
			restrict: 'EA',
			replace: true,
			template: '<i class="icon-check-empty pull-left" data-ng-click="toggleWeek(weekIndex)"></i>',
			link: function(scope, element, attrs) {
				element.bind('click', toggle);

				element.bind('$destroy', function() {
					element.unbind('click');
				});

				function toggle() {
					var isChecked = element.hasClass('icon-check');
					element
						.removeClass(isChecked ? 'icon-check' : 'icon-check-empty')
						.addClass(isChecked ? 'icon-check-empty' : 'icon-check');
					element.parents('tr')
						.find('table.day').find('td.head.empty').not('.dayOff, .saturday, .sunday, .pastOrFuture').find('i[data-cra-check-day]')
						.addClass(isChecked ? 'icon-check-empty' : 'icon-check')
						.removeClass(isChecked ? 'icon-check' : 'icon-check-empty');

				}
			}
		};
	});
