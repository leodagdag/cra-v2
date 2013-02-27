app.controller('ClaimCtrl', ['$scope', '$http', '$log', '$location', 'ClaimTypeConst', 'profile',
    function ClaimCtrl($scope, $http, $log, $location, ClaimTypeConst, profile) {
        var Claim = function(username, form){
            return {
                username: username,
                missionId: form.missionId,
                date: moment(form.date, 'DD/MM/YYYY').valueOf(),
                type: form.type,
                amount: form.amount,
                km: form.km,
                journey: form.journey,
                description: form.description
            }
        };
        $scope.profile = profile.data;
        $scope.claimsType = ClaimTypeConst;
        $scope.form = {};

        $scope.missions = [];
        $scope.loadRefs = function () {
            var route = jsRoutes.controllers.JMissions.claims($scope.profile.username);
            $http({
                method: route.method,
                url: route.url
            })
                .success(function (missions, status, headers, config) {
                    $log.debug('missions', missions);
                    $scope.missions = _($scope.missions)
                        .push(missions)
                        .flatten()
                        .valueOf();
                })
                .error(function (error, status, headers, config) {
                    $log.debug('error', error);
                });
        };

        $scope.save = function () {
            $log.debug('$scope.form',$scope.form);
            var claim = new Claim($scope.profile.username, $scope.form);
            $log.debug('claim',claim);
        };
    }]);