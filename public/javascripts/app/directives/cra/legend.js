angular.module('craLegendDirective', []).
	directive('craLegend', function() {
		'use strict';
		return {
			restrict: 'EA',
			replace: false,
			templateUrl: 'public/html/templates/cra/legend.html',
			link: function(scope, element, attrs) {
				var isOpen = false,
					btn = angular.element(element.find('button')),
					text= angular.element(element.find('#text'));

				btn.bind('click', toggle);

				element.bind('$destroy', function() {
					btn.unbind('click');
				});

				function toggle() {
					isOpen = !isOpen;
					text.removeClass(isOpen ?  'hide' : 'show');
					text.addClass(isOpen ?  'show' : 'hide')
				}
			}
		};
	});
