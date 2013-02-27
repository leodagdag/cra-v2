app.controller('HistoryClaimCtrl', ['$scope', '$http', '$log', '$location', 'MonthsConst',
    function HistoryClaimCtrl($scope, $http, $log, $location, MonthsConst) {
        $scope.months = _(MonthsConst)
            .flatten()
            .valueOf();
        $scope.filter = {
            year: moment().year(),
            month: $scope.months[moment().month()].id,
            orderBy: 'date'
        };
    }]);
