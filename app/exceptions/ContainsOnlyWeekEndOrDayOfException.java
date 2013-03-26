package exceptions;

import models.JAbsence;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author f.patin
 */
public class ContainsOnlyWeekEndOrDayOfException extends Throwable {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

    public ContainsOnlyWeekEndOrDayOfException(final JAbsence absence) {
        super(String.format("Votre demande d'absence ne contient que des jours fériés ou des week-end [%s -> %s]", dtf.print(absence.startDate), dtf.print(absence.endDate.minusDays(1))));
    }
}
