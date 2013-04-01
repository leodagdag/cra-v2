angular.module('absencePeriodFilter', [])
	.filter('absencePeriod', function() {
		'use strict';
		return function(absence) {
			var pattern = "D MMM YYYY";
			var out = _([]);
			if(moment(absence.startDate).hour(0).minutes(0).second(0).millisecond(0)
				.isSame(moment(absence.endDate).hour(0).minutes(0).second(0).millisecond(0))) {
				// Same Day
				out
					.push("le")
					.push(moment(absence.startDate).format(pattern));
				if(!moment(absence.startDate).isSame(moment(absence.endDate), 'hour')) {
					out.push(moment(absence.startDate).hour() === 0 ? "matin" : "après-midi");
				}
			} else {
				var start = moment(absence.startDate);
				var end = moment(absence.endDate);
				out.push("du")
					.push(start.format(pattern))
					.push(start.hour() === 0 ? "" : "après-midi")
					.push("au")
					.push(end.format(pattern))
					.push(end.hour() === 12 ? "matin" : "")

			}

			return out.join(" ");
		}
	});

