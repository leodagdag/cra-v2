package dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.Cra;
import models.Day;
import models.Mission;
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

	public CraDTO(final Cra cra, final List<Day> days, final ImmutableMap<ObjectId, Mission> missions) {
		this.id = cra.id;
		this.year = cra.year;
		this.month = cra.month;
		this.comment = cra.comment;
		this.weeks.addAll(WeekDTO.of(days,missions));
	}

	public static CraDTO of(final Cra cra, final List<Day> days, final ImmutableMap<ObjectId, Mission> missions) {
		return new CraDTO(cra, days, missions);
	}
}
