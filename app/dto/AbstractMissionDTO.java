package dto;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import models.JMission;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import utils.serializer.ObjectIdSerializer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author f.patin
 */
public abstract class AbstractMissionDTO {

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId id;
	public String code;
	public String label;

	@SuppressWarnings({"unused"})
	public AbstractMissionDTO() {
	}

	public AbstractMissionDTO(final JMission mission) {
		this.id = mission.id;
		this.code = mission.code;
		this.label = mission.label;
	}

}
