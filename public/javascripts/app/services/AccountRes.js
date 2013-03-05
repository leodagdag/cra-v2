app.factory('AccountRes', ['$resource',
	function($resource) {
		'use strict';
		return $resource(
			'/account/:id', {}, {
				'update': {'method': 'PUT'}
			}
		);
	}]);
