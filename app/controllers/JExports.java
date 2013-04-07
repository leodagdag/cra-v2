package controllers;

import caches.ResponseCache;
import export.PDF;
import models.JCra;
import models.JMission;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author f.patin
 */
public class JExports  extends Controller {

	@ResponseCache.NoCacheResponse
	public static Result exportByEmployee(final String id, final String title) {
		return ok(PDF.getEmployeeCraData(JCra.fetch(id))).as("application/pdf");
	}

	@ResponseCache.NoCacheResponse
	public static Result exportByMission(final String id, final String idMission, final String title) {
		return ok(PDF.getMissionCraData(JCra.fetch(id), JMission.fetch(ObjectId.massageToObjectId(idMission)))).as("application/pdf");
	}

}
