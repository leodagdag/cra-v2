package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import caches.ResponseCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import constants.ClaimType;
import dto.CraDTO;
import export.PDF;
import models.*;
import org.bson.types.ObjectId;
import play.mvc.Controller;
import play.mvc.Result;
import security.JDeadboltHandler;
import security.JSecurityRoles;
import utils.business.JClaimUtils;

import java.util.List;
import java.util.Map;

import static play.libs.Json.toJson;

/**
 * @author f.patin
 */
public class JCras extends Controller {

    @Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
    @ResponseCache.NoCacheResponse
    public static Result fetch(final String username, final Integer year, final Integer month) {
        final ObjectId userId = JUser.id(username);
        final JCra cra = JCra.getOrCreate(userId, year, month);
        final List<JDay> jDays = Lists.newArrayList();
        final List<ObjectId> missionsIds = Lists.newArrayList();

        jDays.addAll(JDay.find(cra.id, year, month, true));
        for (JDay jDay : jDays) {
            missionsIds.addAll(jDay.missionIds());
        }
        final ImmutableMap<ObjectId, JMission> jMissions = JMission.codeAndMissionType(ImmutableList.copyOf(missionsIds));
        return ok(toJson(CraDTO.of(cra, jDays, jMissions)));
    }

    @Restrict(value = {@Group(JSecurityRoles.role_employee), @Group(JSecurityRoles.role_production), @Group(JSecurityRoles.role_admin)}, handler = JDeadboltHandler.class)
    @ResponseCache.NoCacheResponse
    public static Result claimSynthesis(final String userId, final Integer year, final Integer month) {
        final ImmutableList<JClaim> claims = JClaim.synthesis(userId, year, month);
        final Map<String, Map<ClaimType, String>> synthesis = JClaimUtils.synthesis(year, month, claims);
        return ok(toJson(synthesis));
    }

    @ResponseCache.NoCacheResponse
    public static Result export(final String id) {
        return ok(PDF.getCraData(JCra.fetch(id))).as("application/pdf");
    }
}
