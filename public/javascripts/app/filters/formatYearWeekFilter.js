angular.module('extractWeekFilter', [])
    .filter('extractWeek', function() {
        'use strict';
        return function(yearWeek) {
            return yearWeek.substring(4);
        };
    });

