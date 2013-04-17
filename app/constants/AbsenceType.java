package constants;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * @author f.patin
 */
public enum AbsenceType {
	ALL,
	CP,
	RTT,
	OTHER;

	public static List<String> asString(final List<AbsenceType> absenceType) {
		List<AbsenceType> criterias = Lists.newArrayList();
		if (absenceType.isEmpty()) {
			criterias.addAll(Arrays.asList(AbsenceType.values()));
		} else {
			criterias.addAll(absenceType);
		}

		return Lists.newArrayList(Collections2.transform(criterias, new Function<AbsenceType, String>() {
			@Nullable
			@Override
			public String apply(@Nullable final AbsenceType absenceType) {
				return absenceType.name();
			}
		}));
	}

	public static String to(final AbsenceType absenceType) {
		return absenceType.name().toLowerCase();
	}

	public static AbsenceType of(final String s){
		for(AbsenceType absenceType: AbsenceType.values()){
			if(absenceType.name().toLowerCase().equals(s)){
				return absenceType;
			}
		}
		return  null;
	}
}
