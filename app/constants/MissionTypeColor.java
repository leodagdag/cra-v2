package constants;

import com.itextpdf.text.BaseColor;

/**
 * @author f.patin
 */
public enum MissionTypeColor {
	customer(BaseColor.BLUE, BaseColor.WHITE),
	pre_sale(new BaseColor(0,128,0), BaseColor.WHITE),
	holiday(BaseColor.RED, BaseColor.WHITE),
	not_paid(new BaseColor(180,18,238), BaseColor.WHITE),
	internal_work(new BaseColor(165,34,0), BaseColor.WHITE);
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
