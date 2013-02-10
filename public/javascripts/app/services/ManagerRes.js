'use strict';
app.factory('ManagerRes', ['$resource', function($resource) {
	return $resource('/managers/:id', {id: '@id'});
}]);
