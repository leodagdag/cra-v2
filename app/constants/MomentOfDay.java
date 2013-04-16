package constants;

import play.libs.F;

/**
 * @author f.patin
 */
public enum MomentOfDay {
	morning(0),
	afternoon(1),
	day(2);

	public final Integer index;

	private MomentOfDay(final Integer index) {
		this.index = index;
	}

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
