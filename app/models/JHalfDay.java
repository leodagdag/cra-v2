package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author f.patin
 */
@Embedded
public class JHalfDay {

	public ObjectId missionId;

	public List<JPeriod> periods = Lists.newArrayList();

	@JsonProperty("isSpecial")
	public Boolean isSpecial() {
		return !Iterables.isEmpty(periods);
	}

	public List<ObjectId> missionIds() {
		if (isSpecial()) {
			return Lists.newArrayList(Collections2.transform(periods, new Function<JPeriod, ObjectId>() {
				@Nullable
				@Override
				public ObjectId apply(@Nullable final JPeriod p) {
					return p.missionId;
				}
			}));
		} else {
			return Lists.newArrayList(missionId);
		}
	}

}
