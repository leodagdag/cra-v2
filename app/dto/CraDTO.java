package dto;

import com.google.common.collect.Lists;
import models.JCra;
import models.JDay;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author f.patin
 */
public class CraDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId userId;
	public Integer year;
	public Integer month;
	public String comment;
	public Boolean isValidated;
	public Long sentDate;
	public List<WeekDTO> weeks = Lists.newArrayList();

	@SuppressWarnings({"unused"})
	public CraDTO() {
	}

	public CraDTO(final JCra cra, final List<JDay> days, final Map<ObjectId, JMission> missions) {
		this.id = cra.id;
		this.userId = cra.userId;
		this.year = cra.year;
		this.month = cra.month;
		this.comment = cra.comment;
		this.isValidated = cra.isValidated;
		if(cra.sentDate != null){
			this.sentDate = cra.sentDate.getMillis();
		}
		this.weeks.addAll(WeekDTO.of(days, missions, cra.year, cra.month));
	}


	public static CraDTO of(final JCra cra, final List<JDay> days, final Map<ObjectId, JMission> missions) {
		return new CraDTO(cra, days, missions);
	}

}
