'use strict';

app.factory('UserRes', ['$resource',
	function($resource) {
		return $resource(
			'/users/:criteria', {criteria: '@id'},
			{
				create: {method: 'POST'},
				update: {method: 'PUT'}
			}
		);
	}]);
