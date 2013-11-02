angular.module('httpInterceptorServiceProvider', [])
    .config(['$httpProvider', function ($httpProvider) {
        'use strict';
        $httpProvider.responseInterceptors.push(function ($q, $rootScope, $log) {
            function success(response) {
                return response;
            }

            function error(response) {
                switch (response.status) {
                    case 401:
                        $rootScope.$broadcast('event:auth-loginRequired');
                        break;
                    case 500:
                        $rootScope.$broadcast('event:error', response);
                        break;
                }
                return $q.reject(response);
            }

            return function (promise) {
                return promise.then(success, error);
            };

        });
    }]);


var app = angular.module('app', ['ngResource', 'httpInterceptorServiceProvider',
    'bDatepicker', 'craLegendDirective', 'craToolbarDirective',
    'allowanceTypeFilter', 'capitalizeFilter', 'dayOfMonthFilter', 'dayOfWeekFilter', 'dayOfYearFilter', 'formatYearWeekFilter', 'momentOfDayFilter', 'monthOfYearFilter', 'localTimeFilter', 'kilometerFilter', 'vehicleTypeFilter', 'vehiclePowerFilter', 'vehicleBrandFilter', 'typeOfClaimFilter']);


/* based on https://github.com/bleporini/angular-authent */
app.directive('authenticator', ['$location', '$window',
    function ($location, $window) {
        'use strict';
        return function (scope, elem, attrs) {
            scope.$on('event:auth-loginRequired', function () {
                $window.location.href = "/logout";
            });
        };
    }]);


