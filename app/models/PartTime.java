package models;

import com.github.jmkgreen.morphia.annotations.*;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import utils.deserializer.DateTimeDeserializer;
import utils.serializer.DateTimeSerializer;

import java.util.Date;

/**
 * @author f.patin
 */
@Entity
public class PartTime {

    @Id
    public ObjectId id;

    public ObjectId userId;

    @Transient
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    public DateTime startDate;
    private Date _startDate;

    @Transient
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    public DateTime endDate;
    private Date _endDate;

    public Integer dayOfWeek;
    public String moment;
    public Integer frequency;


    @SuppressWarnings({"unused"})
    @PrePersist
    private void prePersist() {
        if (startDate != null) {
            _startDate = startDate.toDate();
        }
        if (endDate != null) {
            _endDate = endDate.toDate();
        }
    }

    @SuppressWarnings({"unused"})
    @PostLoad
    private void postLoad() {
        if (_startDate != null) {
            startDate = new DateTime(_startDate.getTime());
        }
        if (_endDate != null) {
            endDate = new DateTime(_endDate.getTime());
        }
    }

    @SuppressWarnings({"unused"})
    public PartTime() {
    }
}
