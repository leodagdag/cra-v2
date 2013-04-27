package constants;

import com.itextpdf.text.BaseColor;
import play.libs.F;

/**
 * @author f.patin
 */
public enum MissionTypeColor {
	customer(F.Tuple(BaseColor.WHITE, BaseColor.BLUE)),
	other_customer(F.Tuple(BaseColor.WHITE, new BaseColor(20,112,139))),
	pre_sale(F.Tuple(BaseColor.WHITE, new BaseColor(0,128,0))),
	holiday(F.Tuple(BaseColor.WHITE, BaseColor.RED)),
	not_paid(F.Tuple(BaseColor.WHITE, new BaseColor(180,18,238))),
	internal_work(F.Tuple(BaseColor.WHITE, new BaseColor(165,34,0)));

	public final F.Tuple<BaseColor,BaseColor> colors;

	MissionTypeColor(final F.Tuple<BaseColor,BaseColor> colors) {
		this.colors = colors;
	}

	public static MissionTypeColor by(final MissionType missionType) {
		return MissionTypeColor.valueOf(missionType.name());
	}

}
