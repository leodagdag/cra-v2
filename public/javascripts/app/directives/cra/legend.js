angular.module('craLegendDirective', []).
	directive('craLegend', function () {
		return {
			restrict:'EA',
			replace: false,
			templateUrl:'public/views/templates/cra/legend.html'
		};
	});
