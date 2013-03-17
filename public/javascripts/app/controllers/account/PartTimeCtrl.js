app.controller('PartTimeNewCtrl', ['$rootScope', '$scope', '$http', '$log', '$location',
    function PartTimeNewCtrl($rootScope, $scope, $http, $log, $location) {
        var Form = function () {
            this.startDate = null;
            this.endDate = null;
            this.daysOfWeek = [];
            this.frequency = null;

            this.to = function () {
                return {
                    userId: $scope.profile.id,
                    startDate: moment(this.startDate, 'DD/MM/YYYY').valueOf(),
                    endDate: (this.endDate) ? moment(this.endDate, 'DD/MM/YYYY').valueOf() : null,
	                daysOfWeek: this.daysOfWeek,
                    frequency: this.frequency
                }
            }
        };

        $scope.errors = {
            global: null,
            startDate: null,
            endDate: null,
	        daysOfWeek: null,
            frequency: null
        };

        $scope.dayMoments = [
            {moment: "day", label: "Journée"},
            {moment: "morning", label: "Matin"},
            {moment: "afternoon", label: "Après-midi"}
        ];

        $scope.form = new Form();

        $scope.toggleDay = function (dayId, dayMoment) {
            if (dayMoment) {
                var weekDay = {"dayOfWeek": dayId, "momentOfDay": dayMoment.moment};
                $scope.form.daysOfWeek.push(weekDay);
            } else {
                $scope.form.daysOfWeek = _($scope.form.daysOfWeek)
                    .reject(function (item) {
                        return item.dayOfWeek === this.valueOf();
                    }, dayId)
                    .valueOf();
            }
        };

        $scope.save = function () {
            $log.debug('$scope.form', $scope.form.to());
            var route = jsRoutes.controllers.JPartTimes.addPartTimes();
            $http({
                method: route.method,
                url: route.url,
                data: $scope.form.to()
            })
                .success(function (data, status, headers, config) {
                    $rootScope.onSuccess("Votre temps partiel a été sauvegardé.");
                });
        }
    }]);

app.controller('ActivePartTimeCtrl', ['$scope', '$http', '$log', '$location',
    function ActivePartTimeCtrl($scope, $http, $log, $location) {
        $scope.init = function () {
            /*$http({
             method: jsRoutes.controllers.PartTimes.myActivePartTime().method,
             url: jsRoutes.controllers.PartTimes.myActivePartTime().url
             })
             .success(function (activePartTime, status, headers, config) {
             $scope.activePartTime = activePartTime;
             });*/
        }
    }]);
