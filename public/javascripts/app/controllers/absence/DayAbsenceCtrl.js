app.controller('DayAbsenceCtrl', ['$scope', '$http', '$log', '$location', '$routeParams',
    function DayAbsenceCtrl($scope, $http, $log, $location, $routeParams) {

        $scope.localSave = function(){
            var day = {
                missionId: $scope.missionId,
                startDate: moment($scope.date, 'DD/MM/YYYY').valueOf(),
                startMorning: $scope.morning,
                startAfternoon: $scope.afternoon,
                endDate: moment($scope.date, 'DD/MM/YYYY').valueOf(),
                endMorning: $scope.morning,
                endAfternoon: $scope.afternoon,
                comment: $scope.comment
            };
            $log.debug('day', day);
            $scope.save(day);
        };
    }]);
