package utils.business;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import constants.ClaimType;
import models.JClaim;
import org.apache.commons.lang3.StringUtils;
import utils.time.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static constants.ClaimType.*;

/**
 * @author f.patin
 */
public class JClaimUtils {

	private static Map<Integer, Map<ClaimType, List<JClaim>>> _transform(final Integer year, final Integer month, final ImmutableList<JClaim> claims) {
		final List<Integer> weeks = TimeUtils.getWeeks(year, month);
		final Map<Integer, Map<ClaimType, List<JClaim>>> result = Maps.newTreeMap();
		for(Integer weekNb : weeks) {
			result.put(weekNb, new EnumMap<ClaimType, List<JClaim>>(ClaimType.class));
			for(ClaimType claimType : values()) {
				if(!TOTAL.equals(claimType)) {
					result.get(weekNb).put(claimType, new ArrayList<JClaim>());
				}
			}
		}

		for(JClaim claim : claims) {
			final Integer weekNb = claim.date.getWeekOfWeekyear();
			final ClaimType claimType = valueOf(claim.claimType);
			result.get(weekNb).get(claimType).add(claim);
		}
		return result;
	}

	public static Map<String, Map<ClaimType, String>> synthesis(final Integer year, final Integer month, final ImmutableList<JClaim> claims) {
		final Map<Integer, Map<ClaimType, List<JClaim>>> synthesis  =_transform(year, month, claims);
		final Map<String, Map<ClaimType, String>> result = Maps.newTreeMap();
		for(Integer week : synthesis.keySet()) {
			final String w = StringUtils.leftPad(week.toString(), 2, '0');
			result.put(w, new EnumMap<ClaimType, String>(ClaimType.class));
			for(ClaimType claimType : synthesis.get(week).keySet()) {
				if(!result.get(w).containsKey(claimType)) {
					result.get(w).put(claimType, BigDecimal.ZERO.toPlainString());
				}
				for(JClaim claim : synthesis.get(week).get(claimType)) {
					if(JOURNEY.name().equals(claim.claimType)) {
						result.get(w)
							.put(claimType, new BigDecimal(result.get(w).get(claimType)).add(claim.kilometerAmount).toPlainString());
					} else {
						result.get(w)
							.put(claimType, new BigDecimal(result.get(w).get(claimType)).add(claim.amount).toPlainString());
					}
				}
			}
		}

		// Weeks by total
		for(String w : result.keySet()) {
			if(!result.get(w).containsKey(TOTAL)) {
				result.get(w).put(TOTAL, BigDecimal.ZERO.toPlainString());
			}
			for(ClaimType c : result.get(w).keySet()) {
				if(!TOTAL.equals(c)) {
					final BigDecimal bd = new BigDecimal(result.get(w).get(c));
					final BigDecimal total = new BigDecimal(result.get(w).get(TOTAL));
					result.get(w).put(TOTAL, total.add(bd).toPlainString());
				}
			}
		}

		// Total by claimType
		result.put("TOTAL", new EnumMap<ClaimType, String>(ClaimType.class));
		for(String w : result.keySet()) {
			if(!w.equals("TOTAL")) {
				for(ClaimType c : result.get(w).keySet()) {
					if(!result.get("TOTAL").containsKey(c)) {
						result.get("TOTAL").put(c, BigDecimal.ZERO.toPlainString());
					}
					result.get("TOTAL").put(c, new BigDecimal(result.get(w).get(c)).add(new BigDecimal(result.get("TOTAL").get(c))).toPlainString());
				}
			}
		}

		return result;
	}
}
