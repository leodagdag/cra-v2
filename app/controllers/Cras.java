package controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dto.CraDTO;
import models.Cra;
import models.Day;
import models.Mission;
import models.User;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class Cras extends Controller {

	public static Result fetch(final String trigramme, final Integer year, final Integer month) {
		final User user = User.idByTrigramme(trigramme);
		final Cra cra = Cra.find(user.id, year, month);
		final List<Day> days = Day.find(cra.id, year, month);
		final List<ObjectId> missionsIds = Lists.newArrayList();
		for (Day day : days) {
			missionsIds.addAll(day.missionIds());
		}
		final ImmutableMap<ObjectId, Mission> missions = Mission.codeAndMissionType(missionsIds);

		return ok(toJson(CraDTO.of(cra, days, missions)));
	}
}
