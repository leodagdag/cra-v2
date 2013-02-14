package dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JCra;
import models.JDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import java.util.List;

/**
 * @author f.patin
 */
public class CraDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public Integer year;
	public Integer month;
	public String comment;
	public List<WeekDTO> weeks = Lists.newArrayList();

	public CraDTO() {
	}

	public CraDTO(final JCra JCra, final List<JDay> JDays, final ImmutableMap<ObjectId, JMission> missions) {
		this.id = JCra.id;
		this.year = JCra.year;
		this.month = JCra.month;
		this.comment = JCra.comment;
		this.weeks.addAll(WeekDTO.of(JDays,missions));
	}

	public static CraDTO of(final JCra JCra, final List<JDay> JDays, final ImmutableMap<ObjectId, JMission> missions) {
		return new CraDTO(JCra, JDays, missions);
	}
}
