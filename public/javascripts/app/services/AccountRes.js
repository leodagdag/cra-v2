'use strict';
app.factory('AccountRes', ['$resource', function ($resource) {
	return $resource('/account',{},
		{
			'update': {'method': 'PUT'}
		}
	);
}]);
