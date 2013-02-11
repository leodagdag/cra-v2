angular.module('week', []).
	directive('week', function () {
		return {
			restrict:'A',
			templateUrl:'public/views/templates/week.html',
			//template:'<tr><td>test</td></tr>',
			replace:true,
			scope:{
				days: '=days'
			}
		};
	});
