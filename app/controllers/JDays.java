package controllers;

import caches.ResponseCache;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import constants.MomentOfDay;
import dto.DayDTO;
import exceptions.IllegalDayOperation;
import models.JClaim;
import models.JCra;
import models.JDay;
import models.JHalfDay;
import models.JMission;
import models.JPeriod;
import models.JVehicle;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JDays extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result fetch(final String craId, final Long date) {
		final DateTime dt = new DateTime(date);
		final ObjectId idCra = ObjectId.massageToObjectId(craId);

		JDay day = JDay.find(idCra, dt);
		final List<ObjectId> missionsIds = Lists.newArrayList();
		if(day == null) {
			day = new JDay(dt);
		} else {
			missionsIds.addAll(day.missionIds());
		}
		final Map<ObjectId, JMission> jMissions = JMission.codeAndMissionType(missionsIds);
		return ok(toJson(DayDTO.of(day, jMissions, dt.getYear(), dt.getMonthOfYear())));
	}

	@BodyParser.Of(BodyParser.Json.class)
	@ResponseCache.NoCacheResponse
	public static Result create() {
		final Form<CreateForm> form = Form.form(CreateForm.class).bind(request().body().asJson());
		if(form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final CreateForm createForm = form.get();
		final ObjectId craId = createForm.craId;
		final ObjectId userId = createForm.userId;
		final Integer year = createForm.year;
		final Integer month = createForm.month;
		final JCra cra = JCra.getOrCreate(craId, userId, year, month);

		try {
			final List<JDay> days = JDay.createDays(cra.id, createForm.days());
			JClaim.computeMissionAllowance(userId, days);
			return created("Journée(s) sauvegardée(s)");
		} catch(IllegalDayOperation e) {
			return badRequest(toJson(e));
		}
	}

	public static Result removeHalfDay(final String craId, final Long date, final String mod) {
		final MomentOfDay momentOfDay = MomentOfDay.valueOf(mod);
		final DateTime dt = new DateTime(date);
		final ObjectId idCra = ObjectId.massageToObjectId(craId);

		final JHalfDay deleteHalfDay = JDay.findHalfDay(idCra, dt, momentOfDay);
		if(JMission.isAbsenceMission(deleteHalfDay.missionId)) {
			return badRequest(toJson("Vous ne pouvez pas supprimer une absence."));
		}
		final JDay day = JDay.deleteHalfDay(idCra, dt, momentOfDay);

		// Day empty
		if(day.morning == null && day.afternoon == null) {
			return remove(craId, date);
		}
		JClaim.computeMissionAllowance(day.userId, day);
		return ok();
	}

	public static Result remove(final String craId, final Long date) {
		final DateTime dt = new DateTime(date);
		final ObjectId idCra = ObjectId.massageToObjectId(craId);

		final JDay day = JDay.find(idCra, dt);
		if((day.morning != null && JMission.isAbsenceMission(day.morning.missionId))
			   || (day.afternoon != null && JMission.isAbsenceMission(day.afternoon.missionId))) {
			return badRequest(toJson("Vous ne pouvez pas supprimer une absence."));
		}
		JDay.delete(craId, date);
		JClaim.deleteMissionAllowance(day.userId, day.date);
		return ok();
	}

	public static class CreateForm {

		public ObjectId userId;
		public ObjectId craId;
		public Integer year;
		public Integer month;
		public List<Long> dates;
		public CreateDayForm day;

		public List<ValidationError> validate() {
			final List<ValidationError> errors = Lists.newArrayList();
			if(day != null) {

				final Set<ObjectId> missionIds = day.missionIds();
				if(missionIds.isEmpty()) {
					errors.add(new ValidationError("global", "Vous devez saisir au moins une mission."));
				} else {

					final Map<ObjectId, JMission> missions = Maps.newHashMap();
					final boolean activeVehicleExists = JVehicle.active(userId) != null;
					for(ObjectId missionId : missionIds) {
						if(!missions.containsKey(missionId)) {
							missions.put(missionId, JMission.fetch(missionId));
						}
						final JMission mission = missions.get(missionId);
						/* #104
						if(MissionAllowanceType.REAL.name().equals(mission.allowanceType) & !activeVehicleExists) {
							errors.add(new ValidationError("global", String.format("Vous ne pouvez pas choisir cette mission [%s] (véhicule requis).", mission.label)));
						}
						*/
					}
				}
			} else {
				errors.add(new ValidationError("global", "Vous devez saisir au moins une mission."));
			}

			return errors.isEmpty() ? null : errors;
		}

		public List<JDay> days() {
			return Lists.newArrayList(Collections2.transform(dates, new Function<Long, JDay>() {
				@Nullable
				@Override
				public JDay apply(@Nullable final Long date) {
					JDay d = new JDay(date);
					d.craId = craId;
					d.userId = userId;
					if(day != null) {
						d.morning = day.morning();
						d.afternoon = day.afternoon();
					}
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

		public Set<ObjectId> missionIds() {
			final Set<ObjectId> result = Sets.newHashSet();
			if(morning != null) {
				result.addAll(morning.missionIds());
			}
			if(afternoon != null) {
				result.addAll(afternoon.missionIds());
			}
			return result;
		}

		public JHalfDay morning() {
			if(this.morning == null) {
				return null;
			}
			final JHalfDay hd = new JHalfDay();
			if(this.morning.missionId == null) {
				hd.periods.addAll(this.morning.periods());
			} else {
				hd.missionId = ObjectId.massageToObjectId(this.morning.missionId);
			}
			return hd;
		}

		public JHalfDay afternoon() {
			if(this.afternoon == null) {
				return null;
			}
			final JHalfDay hd = new JHalfDay();
			if(this.afternoon.missionId == null) {
				hd.periods.addAll(this.afternoon.periods());
			} else {
				hd.missionId = ObjectId.massageToObjectId(this.afternoon.missionId);
			}
			return hd;
		}

	}

	public static class CreateHalfDayForm {

		public ObjectId missionId;
		public List<CreatePeriodForm> periods;

		public Set<ObjectId> missionIds() {
			if(CollectionUtils.isNotEmpty(periods)) {
				return Sets.newHashSet(Collections2.transform(periods, new Function<CreatePeriodForm, ObjectId>() {
					@Nullable
					@Override
					public ObjectId apply(@Nullable final CreatePeriodForm p) {
						return p.missionId;
					}
				}));
			} else {
				return Sets.newHashSet(missionId);
			}
		}

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

		public ObjectId missionId;
		public Long startTime;
		public Long endTime;

		public JPeriod to() {
			return new JPeriod(ObjectId.massageToObjectId(missionId), new DateTime(startTime).toLocalTime(), new DateTime(endTime).toLocalTime());
		}
	}

}
