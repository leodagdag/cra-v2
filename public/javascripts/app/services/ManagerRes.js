app.factory('ManagerRes', ['$resource',
	function($resource) {
		'use strict';
		return $resource(
			'/users/managers/:id', {id: '@id'}
		);
	}]);
