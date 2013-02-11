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
public class Day {

    @Id
    public ObjectId id;

    @Transient
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    public DateTime date;
    private Date _date;

    @Embedded
    public HalfDay morning;

    @Embedded
    public HalfDay afternoon;

    @Transient
    public Boolean isSaturday;

    @Transient
    public Boolean isSunday;

    @Transient
    public Boolean isDayOff;

    @SuppressWarnings({"unused"})
    @PrePersist
    private void prePersist() {
        if (date != null) {
            _date = date.toDate();
        }
    }

    @SuppressWarnings({"unused"})
    @PostLoad
    private void postLoad() {
        if (_date != null) {
            date = new DateTime(_date.getTime());
        }
    }

    @SuppressWarnings({"unused"})
    public Day() {
    }


}
