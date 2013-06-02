app.controller('BackOfficeUsersCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
    function ($scope, $http, $log, $location, $routeParams) {
        'use strict';

        $scope.sortBys = [
            {key: '+username', label: 'Username (asc)'},
            {key: '-username', label: 'Username (desc)'},
            {key: '+firstName', label: 'Prénom (asc)'},
            {key: '-firstName', label: 'Prénom (desc)'},
            {key: '+lastName', label: 'Nom (asc)'},
            {key: '-lastName', label: 'Nom (desc)'}
        ];

        $scope.filter = {
            'role': 'all',
            'sortBy': $scope.sortBys[0].key
        };

        $scope.filterChange = function () {
            $scope.loadUsers();
        };

        $scope.sortByChange = function () {
            $scope.users = sort($scope.users);
        };

        var filter = function (users) {

            switch ($scope.filter.role) {
                case 'employee':
                    return _(users)
                        .filter({'isManager': false})
                        .filter({'role': 'employee'})
                        .valueOf();
                    break;
                case 'manager':
                    return _(users)
                        .filter({'isManager': true})
                        .valueOf();
                case 'other' :
                    return _(users)
                        .filter(function (user) {
                            return user.role !== 'employee'
                        })
                        .filter({'isManager': false})
                        .valueOf();
                default:
                    return users;
            }
        };
        var sort = function (list) {
            var field = $scope.filter.sortBy.substr(1),
                direction = $scope.filter.sortBy.substr(0, 1) === '+' ? 'asc' : 'desc',
                result = _(list).sortBy(field);
            if (direction === 'desc') {
                result.reverse();
            }
            return result.valueOf();
        };

        $scope.users = [];


        $scope.loadUsers = function () {
            var route = jsRoutes.controllers.JUsers.all();
            $http({
                'method': route.method,
                'url': route.url,
                'cache': false
            })
                .success(function (users, status, headers, config) {
                    $scope.users = sort(filter(users));
                })
                .error(function (error, status, headers, config) {
                    $log.error('error', error);
                });
        };

        $scope.edit = function (id) {

        };

        $scope.delete = function (id) {

        };
    }]);