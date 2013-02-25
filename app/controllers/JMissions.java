package controllers;

import com.google.common.collect.ImmutableList;
import dto.MissionDTO;
import models.JMission;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JMissions  extends Controller {

    public static Result absences() {
        final ImmutableList<JMission> missions = JMission.getAbsencesMissions();
        return ok(toJson(MissionDTO.of(missions)));
    }

}
