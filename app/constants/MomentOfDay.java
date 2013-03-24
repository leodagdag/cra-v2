package constants;

import play.libs.F;

/**
 * @author f.patin
 */
public enum MomentOfDay {
	morning,
	afternoon,
	day;

	public static F.Tuple<Boolean, Boolean> to(final String momentOfDay) {
		final MomentOfDay mod = MomentOfDay.valueOf(momentOfDay);
		switch(mod) {
			case morning:
				return F.Tuple(Boolean.TRUE, Boolean.FALSE);
			case afternoon:
				return F.Tuple(Boolean.FALSE, Boolean.TRUE);
			case day:
				return F.Tuple(Boolean.TRUE, Boolean.TRUE);
			default:
				return F.Tuple(Boolean.FALSE, Boolean.FALSE);
		}
	}
}
