package controllers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dto.DayDTO;
import leodagdag.play2morphia.MorphiaPlugin;
import models.JCra;
import models.JDay;
import models.JHalfDay;
import models.JMission;
import models.JPeriod;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.Date;
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

	public static Result create() {
		final Form<CreateForm> form = Form.form(CreateForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest("");
		}
		final CreateForm createForm = form.get();
		final String userId = createForm.userId;
		final Integer year = createForm.year;
		final Integer month = createForm.month;
		final ObjectId craId = createForm.craId == null ? JCra.create(userId, year, month).id : ObjectId.massageToObjectId(createForm.craId);

		JDay.update(createForm.days());

		return ok("");
	}



	public static class CreateForm {
		public String userId;
		public String craId;
		public Integer year;
		public Integer month;
		public List<Long> dates;
		public CreateDayForm day;

		public List<JDay> days() {
			return Lists.newArrayList(Collections2.transform(dates, new Function<Long, JDay>() {
				@Nullable
				@Override
				public JDay apply(@Nullable final Long date) {
					JDay d = new JDay(date);
					d.morning = day.morning();
					d.afternoon = day.afternoon();
					d.comment = day.comment;
					return d;
				}
			}));
		}
	}

	public static class CreateDayForm {
		public CreateHalfDayForm morning;
		public CreateHalfDayForm afternoon;
		public String comment;

		public JHalfDay morning() {
			JHalfDay hd = new JHalfDay();
			if (this.morning.missionId == null) {
				hd.JPeriods.addAll(this.morning.periods());
			} else {
				hd.missionId = ObjectId.massageToObjectId(this.morning.missionId);
			}
			return hd;
		}

		public JHalfDay afternoon() {
			JHalfDay hd = new JHalfDay();
			if (this.afternoon.missionId == null) {
				hd.JPeriods.addAll(this.afternoon.periods());
			} else {
				hd.missionId = ObjectId.massageToObjectId(this.afternoon.missionId);
			}
			return hd;
		}

	}

	public static class CreateHalfDayForm {
		public String missionId;
		public List<CreatePeriodForm> periods;

		public List<JPeriod> periods() {
			return Lists.newArrayList(Collections2.transform(periods, new Function<CreatePeriodForm, JPeriod>() {
				@Nullable
				@Override
				public JPeriod apply(@Nullable final CreatePeriodForm p) {
					return p.to();
				}
			}));
		}
	}

	public static class CreatePeriodForm {
		public String missionId;
		public Long startTime;
		public Long endTime;

		public JPeriod to() {
			return new JPeriod(ObjectId.massageToObjectId(missionId), new DateTime(startTime).toLocalTime(), new DateTime(startTime).toLocalTime());
		}
	}


}
