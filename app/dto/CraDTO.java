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
	public Boolean isValidated;
	public List<WeekDTO> weeks = Lists.newArrayList();

	public CraDTO() {
	}

	public CraDTO(final JCra jCra, final List<JDay> jDays, final ImmutableMap<ObjectId, JMission> missions) {
		this.id = jCra.id;
		this.year = jCra.year;
		this.month = jCra.month;
		this.comment = jCra.comment;
		this.isValidated = jCra.isValidated;
		this.weeks.addAll(WeekDTO.of(jDays,missions, jCra.year, jCra.month));
	}

	public static CraDTO of(final JCra jCra, final List<JDay> jDays, final ImmutableMap<ObjectId, JMission> jMissions) {
		return new CraDTO(jCra, jDays, jMissions);
	}
}
