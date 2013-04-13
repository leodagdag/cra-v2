package utils.business;

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
import java.util.TreeMap;

import static constants.ClaimType.*;

/**
 * @author f.patin
 */
public class JClaimUtils {

	private static final String totalKey = "Total";

	public static Map<Integer, Map<ClaimType, List<JClaim>>> transform(final Integer year, final Integer month, final List<JClaim> claims) {
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

	public static Map<String, Map<ClaimType, String>> synthesis(final Integer year, final Integer month, final List<JClaim> claims) {
		final Map<Integer, Map<ClaimType, List<JClaim>>> synthesis = transform(year, month, claims);
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

		result.put(totalKey, new EnumMap<ClaimType, String>(ClaimType.class));
		for(String w : result.keySet()) {
			if(!totalKey.equals(w)) {
				for(ClaimType c : result.get(w).keySet()) {
					if(!result.get(totalKey).containsKey(c)) {
						result.get(totalKey).put(c, BigDecimal.ZERO.toPlainString());
					}
					result.get(totalKey).put(c, new BigDecimal(result.get(w).get(c)).add(new BigDecimal(result.get(totalKey).get(c))).toPlainString());
				}
			}
		}

		return result;
	}
}
