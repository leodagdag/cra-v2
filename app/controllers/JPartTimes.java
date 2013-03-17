package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dto.PartTimeDTO;
import models.JPartTime;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.serializer.ObjectIdSerializer;
import utils.time.TimeUtils;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JPartTimes extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	public static Result addPartTimes() {
		final Form<CreateForm> form = Form.form(CreateForm.class).bind(request().body().asJson());
		if (form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final CreateForm createForm = form.get();
		final ImmutableList<JPartTime> pts = createForm.to();
		return created(toJson(PartTimeDTO.of(JPartTime.addPartTimes(pts))));
	}

	public static Result active(final String userId) {
		return ok(toJson(PartTimeDTO.of(JPartTime.activeByUser(userId))));
	}

	public static Result history(final String userId) {
		return ok(toJson(PartTimeDTO.of(JPartTime.byUser(userId))));
	}

	public static class PartTimeWeekDay {

		public Integer dayOfWeek;
		public String momentOfDay;
	}

	public static class CreateForm {

		public ObjectId userId;
		public Long startDate;
		public Long endDate;
		public List<PartTimeWeekDay> daysOfWeek = Lists.newArrayList();
		public Integer frequency;

		public ImmutableList<JPartTime> to() {
			List<JPartTime> pts = Lists.newArrayListWithCapacity(daysOfWeek.size());
			for (PartTimeWeekDay wd : daysOfWeek) {
				JPartTime pt = new JPartTime(userId, TimeUtils.toNextDayOfWeek(new DateTime(startDate), wd.dayOfWeek), frequency);
				pt.dayOfWeek = wd.dayOfWeek;
				pt.momentOfDay = wd.momentOfDay;
				pts.add(pt);
			}
			return ImmutableList.copyOf(pts);
		}
	}
}
