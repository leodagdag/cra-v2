package controllers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dto.DayDTO;
import models.JDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JDays extends Controller {

	public static Result fetch(String idCra, Long date) {
		final DateTime dt = new DateTime(date);
		JDay day = JDay.find(new ObjectId(idCra), dt);
		final List<ObjectId> missionsIds = Lists.newArrayList();
		if (day == null) {
			day = new JDay(dt);
		} else {
			missionsIds.addAll(day.missionIds());
		}
		final ImmutableMap<ObjectId, JMission> jMissions = JMission.codeAndMissionType(missionsIds, false);
		return ok(toJson(DayDTO.of(day, jMissions, dt.getYear(), dt.getMonthOfYear())));
	}
}
