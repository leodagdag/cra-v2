angular.module('formatYearWeekFilter', [])
    .filter('formatYearWeek', function () {
        'use strict';
        return function (yearWeek) {
            if (isNaN(yearWeek)) {
                return yearWeek
            } else {
                return yearWeek.substring(0, 4) + " - " + yearWeek.substring(4);
            }
        };
    });

