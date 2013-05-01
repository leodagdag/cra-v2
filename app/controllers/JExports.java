package controllers;

import http.ResponseCache;
import export.PDF;
import models.DbFile;
import models.JAbsence;
import models.JCra;
import models.JMission;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author f.patin
 */
public class JExports extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result exportByEmployee(final String id, final String title) {
		return ok(PDF.getEmployeeCraData(JCra.fetch(id))).as("application/pdf");
	}

	@ResponseCache.NoCacheResponse
	public static Result exportByMission(final String id, final String idMission, final String title) {
		return ok(PDF.getMissionCraData(JCra.fetch(id), JMission.fetch(ObjectId.massageToObjectId(idMission)))).as("application/pdf");
	}

	@ResponseCache.NoCacheResponse
	public static Result exportForProduction(final String fileId,final String title) {
		return ok(DbFile.fetch(ObjectId.massageToObjectId(fileId))._2).as("application/pdf");
	}

	@ResponseCache.NoCacheResponse
	public static Result exportAbsence(final String absenceId,final String title) {
		return ok(DbFile.fetch(JAbsence.fetch(absenceId).fileId)._2).as("application/pdf");
	}
}
