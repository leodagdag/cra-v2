app.controller('ClaimCtrl', ['$scope', '$http', '$log', '$location', 'ClaimTypeConst', 'MonthsConst', 'profile',
	function ClaimCtrl($scope, $http, $log, $location, ClaimTypeConst, MonthsConst, profile) {
		var Claim = function(username, form) {
			return {
				username: username,
				missionId: form.missionId,
				date: moment(form.date, 'DD/MM/YYYY').valueOf(),
				claimType: form.claimType,
				amount: form.amount,
				kilometer: form.kilometer,
				journey: form.journey,
				comment: form.comment
			}
		};

		$scope.profile = profile.data;
		$scope.months = _(MonthsConst).flatten().valueOf();
		$scope.claimsType = ClaimTypeConst;
		$scope.sortBys = [
			{'key': 'date', 'label': 'Date'},
			{'key': 'claimType', 'label': 'Type'}
		];
		$scope.filter = {
			'year': moment().year(),
			'month': $scope.months[moment().month()].id,
			'sortBy': $scope.sortBys[0].key
		};
		$scope.form = {};
		$scope.claims = [];
		$scope.missions = [];

		$scope.loadRefs = function() {
			var route = jsRoutes.controllers.JMissions.claims($scope.profile.username);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(missions, status, headers, config) {
					$scope.missions = _($scope.missions)
						.push(missions)
						.flatten()
						.valueOf();
				})
				.error(function(error, status, headers, config) {
					$log.error('error', error);
				});
		};

		$scope.loadHistory = function() {
			var route = jsRoutes.controllers.JClaims.history($scope.profile.username, $scope.filter.year, $scope.filter.month);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(claims, status, headers, config) {
					$scope.claims = _(claims)
						.sortBy($scope.filter.sortBy)
						.flatten()
						.valueOf();
				})
				.error(function(error, status, headers, config) {
					$log.error('error', error);
				});
		};

		$scope.save = function() {
			var claim = new Claim($scope.profile.username, $scope.form);
			var route = jsRoutes.controllers.JClaims.create();
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false,
				'data': claim
			})
				.success(function(claim, status, headers, config) {
					$rootScope.onSuccess("La note de frais a été créée.");
					$scope.claims = _($scope.claims)
						.push(claim)
						.filter(function(c) {
							return c.month === $scope.filter.month && c.year === $scope.filter.year;
						})
						.sortBy($scope.filter.sortBy)
						.flatten()
						.valueOf()
				})
				.error(function(error, status, headers, config) {
				});
		};

		$scope.delete = function(id) {
			var route = jsRoutes.controllers.JClaims.delete(id);
			$http({
				'method': route.method,
				'url': route.url,
				'cache': false
			})
				.success(function(claims, status, headers, config) {
					$scope.claims = _($scope.claims)
						.reject({'id': id})
						.flatten()
						.sortBy($scope.filter.sortBy)
						.valueOf()
				})
				.error(function(error, status, headers, config) {
					$log.error('error', error);
				});
		};

		$scope.filterChange = function() {
			if($scope.filter.year && $scope.filter.month) {
				$scope.loadHistory();
			}
		};

		$scope.sortByChange = function() {
			$log.debug($scope.claims);
			$scope.claims = _($scope.claims)
				.sortBy($scope.filter.sortBy)
				.valueOf();
			$log.debug($scope.claims);
		}

	}]);
