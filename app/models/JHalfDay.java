package models;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

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

	public JHalfDay() {
	}

	public JHalfDay(final ObjectId missionId) {
		this.missionId = missionId;
	}

	public Set<ObjectId> missionIds() {
		if(isSpecial()) {
			return Sets.newHashSet(Collections2.transform(periods, new Function<JPeriod, ObjectId>() {
				@Nullable
				@Override
				public ObjectId apply(@Nullable final JPeriod p) {
					return p.missionId;
				}
			}));
		} else {
			return Sets.newHashSet(missionId);
		}
	}

}
