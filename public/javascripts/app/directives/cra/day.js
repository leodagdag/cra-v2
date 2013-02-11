angular.module('day', []).
    directive('day', function () {
        return {
	        restrict:'EA',
	        replace: true,
	        //require:'ngModel',
	        templateUrl:'public/views/templates/day.html'
	        //template:"<td>day</td>"

        };
    });
