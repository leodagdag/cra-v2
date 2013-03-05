app.factory('UserRes', ['$resource',
	function($resource) {
		'use strict';
		return $resource(
			'/users/:criteria', {criteria: '@id'}, {
				create: {method: 'POST'},
				update: {method: 'PUT'}
			}
		);
	}]);
