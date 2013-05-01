package controllers;

import com.google.common.collect.Lists;
import constants.AbsenceType;
import dto.AbsenceDTO;
import exceptions.AbsenceAlreadyExistException;
import export.PDF;
import http.ResponseCache;
import mail.MailerAbsence;
import models.DbFile;
import models.JAbsence;
import models.JDay;
import models.JUser;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import utils.time.TimeUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JAbsences extends Controller {

	@BodyParser.Of(BodyParser.Json.class)
	@ResponseCache.NoCacheResponse
	public static Result create() {
		final Form<CreateAbsenceForm> form = Form.form(CreateAbsenceForm.class).bind(request().body().asJson());
		if(form.hasErrors()) {
			return badRequest(form.errorsAsJson());
		}
		final CreateAbsenceForm createAbsenceForm = form.get();
		try {
			final List<JAbsence> absences = createAbsenceForm.toAbsences();
			for(JAbsence abs : absences) {
				final JAbsence absence = JAbsence.create(abs);
				JDay.addAbsenceDays(absence);
			}
			return created(toJson(AbsenceDTO.of(absences)));
		} catch(AbsenceAlreadyExistException e) {
			return internalServerError(toJson(e.getMessage()));
		}
	}

	@ResponseCache.NoCacheResponse
	public static Result remove(final String userId, final String id) {
		// Suppression de l'absence concernée
		final JAbsence absence = JAbsence.delete(id);
		if(absence.fileId != null) {
			// supprimer le fichier
			DbFile.remove(absence.fileId);
			// récupérer toutes les absences de ce fichier SAUF celle concerné
			final List<JAbsence> remainAbsences = JAbsence.byFileId(absence.fileId);
			if(CollectionUtils.isNotEmpty(remainAbsences)) {
				// Créer nouveau fichier et l'affecter à celle restante
				PDF.createAbsenceData(remainAbsences);
			}
			// Envoi de l'annulation sans stockage du fichier
			final JUser user = JUser.account(userId);
			final File file = PDF.createCancelAbsenceFile(absence, user);
			MailerAbsence.sendCancelAbsence(user, file);
		}
		JDay.deleteAbsenceDays(absence);
		return ok(toJson(AbsenceDTO.of(absence)));
	}

	@ResponseCache.NoCacheResponse
	public static Result history(final String userId, final String absenceType, final Integer year, final Integer month) {
		final List<JAbsence> absences = Lists.newArrayList();
		final AbsenceType absType = AbsenceType.of(absenceType);

		switch(absType) {
			case CP:
				absences.addAll(JAbsence.fetchCP(userId, year, month));
			case RTT:
				absences.addAll(JAbsence.fetchRTT(userId, year, month));
			case OTHER:
				absences.addAll(JAbsence.fetchOTHER(userId, year, month));
			default:
				absences.addAll(JAbsence.fetchALL(userId, year, month));
		}

		return ok(toJson(AbsenceDTO.of(absences)));
	}

	public static Result send(final String id) {
		final JAbsence absence = JAbsence.fetch(id);
		final File file = PDF.createAbsenceFile(absence, JUser.account(absence.userId));
		final DateTime date = MailerAbsence.send(absence, file);
		JAbsence.updateSentDate(absence.id, date);
		return ok();
	}

	public static Result exportFile(final String id) {
		final String title = DbFile.fileName(JAbsence.fetch(id).fileId);
		return redirect(routes.JExports.exportAbsence(id, title));
	}

	public static class CreateAbsenceForm {

		public Boolean day = Boolean.TRUE;
		public String username;
		public ObjectId missionId;
		public Long startDate;
		public Boolean startMorning;
		public Long endDate;
		public Boolean endAfternoon;
		public String comment;

		public List<ValidationError> validate() {
			final List<ValidationError> errors = Lists.newArrayList();
			if(missionId == null) {
				errors.add(new ValidationError("missionId", "Le motif est requis."));
			}
			if(Boolean.TRUE.equals(day)) {
				if(startDate == null) {
					errors.add(new ValidationError("date", "La date est requise."));
				} else if(new DateTime(startDate).isBefore(TimeUtils.firstDateOfMonth(DateTime.now()))) {
					errors.add(new ValidationError("date", "Vous ne pouvez pas saisir une absence à une date précédant le mois en cours."));
				}
				if(!Boolean.TRUE.equals(startMorning) && !Boolean.TRUE.equals(endAfternoon)) {
					errors.add(new ValidationError("limits", "Vous devez sélectionner au moins une demi-journée."));
				}
				if(TimeUtils.isDayOffOrWeekEnd(new DateTime(startDate))) {
					errors.add(new ValidationError("date", "Vous ne pouvez pas sélectionner un jour férié ou un week-end."));
				}
			} else {
				if(startDate == null) {
					errors.add(new ValidationError("startDate", "La date de début est requise."));
				} else if(TimeUtils.isDayOffOrWeekEnd(new DateTime(startDate))) {
					errors.add(new ValidationError("startDate", "Vous ne pouvez pas sélectionner un jour férié ou un week-end."));
				}
				if(endDate == null) {
					errors.add(new ValidationError("endDate", "La date de fin est requise."));
				} else if(TimeUtils.isDayOffOrWeekEnd(new DateTime(endDate))) {
					errors.add(new ValidationError("endDate", "Vous ne pouvez pas sélectionner un jour férié ou un week-end."));
				}
				if(startDate != null && endDate != null && endDate < startDate) {
					errors.add(new ValidationError("dates", "La date de début doit être antérieur la date de fin."));
				} else if(new DateTime(startDate).isBefore(DateTime.now().withDayOfMonth(1))) {
					errors.add(new ValidationError("dates", "Vous ne pouvez pas saisir une absence précédant le mois en cours."));
				}

			}
			return errors.isEmpty() ? null : errors;
		}

		public List<JAbsence> toAbsences() {

			final DateTime startDate = new DateTime(this.startDate).withTimeAtStartOfDay();
			final DateTime endDate = new DateTime(this.endDate).withTimeAtStartOfDay();
			final int startYear = startDate.getYear();
			final int startMonth = startDate.getMonthOfYear();
			final int endYear = endDate.getYear();
			final int endMonth = endDate.getMonthOfYear();


			final ObjectId userId = JUser.id(this.username);
			if(startYear == endYear && startMonth == endMonth) { // Only in one month
				final JAbsence absence = new JAbsence();
				absence.userId = userId;
				absence.missionId = this.missionId;
				absence.comment = this.comment;
				absence.startDate = startDate;
				absence.startMorning = this.startMorning;
				absence.endDate = endDate;
				absence.endAfternoon = this.endAfternoon;
				return Lists.newArrayList(absence);
			} else {
				final Collection<F.Tuple<Integer, Integer>> yearMonths = TimeUtils.getYearMonth(startDate, endDate);
				final List<JAbsence> absences = Lists.newArrayListWithCapacity(yearMonths.size());
				for(F.Tuple<Integer, Integer> yearMonth : yearMonths) {
					final Integer year = yearMonth._1;
					final Integer month = yearMonth._2;

					final JAbsence absence = new JAbsence();
					absence.userId = userId;
					absence.missionId = this.missionId;
					absence.comment = this.comment;

					if(startYear == year && startMonth == month) { // first absence...
						absence.startDate = startDate;
						absence.startMorning = this.startMorning;
						absence.endDate = TimeUtils.lastDateOfMonth(TimeUtils.lastDateOfMonth(startDate));
						absence.endAfternoon = true;
					} else if(endYear == year && endMonth == month) { // ...last absence...
						absence.startDate = TimeUtils.nextWorkingDay(TimeUtils.firstDateOfMonth(endDate));
						absence.startMorning = true;
						absence.endDate = endDate;
						absence.endAfternoon = this.endAfternoon;
					} else { // ...and other
						absence.startDate = TimeUtils.nextWorkingDay(TimeUtils.firstDateOfMonth(year, month));
						absence.startMorning = true;
						absence.endDate = TimeUtils.previousWorkingDay(TimeUtils.lastDateOfMonth(year, month));
						absence.endAfternoon = true;
					}
					absences.add(absence);
				}
				return absences;
			}


		}
	}
}
