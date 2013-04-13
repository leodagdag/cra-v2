package constants;

import com.itextpdf.text.BaseColor;

/**
 * @author f.patin
 */
public enum MissionTypeColor {
	customer(BaseColor.BLUE, BaseColor.WHITE),
	pre_sale(BaseColor.GREEN, BaseColor.WHITE),
	holiday(BaseColor.RED, BaseColor.WHITE),
	not_paid(BaseColor.PINK, BaseColor.WHITE),
	internal_work(BaseColor.YELLOW, BaseColor.WHITE);
	public final BaseColor frontColor;
	public final BaseColor backgroundColor;


	MissionTypeColor(final BaseColor frontColor, final BaseColor backgroundColor) {
		this.frontColor = frontColor;
		this.backgroundColor = backgroundColor;
	}

	public static MissionTypeColor by(final MissionType missionType) {
		return MissionTypeColor.valueOf(missionType.name());
	}
}
