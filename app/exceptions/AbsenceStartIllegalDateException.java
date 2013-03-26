package exceptions;

import models.JAbsence;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author f.patin
 */
public class AbsenceStartIllegalDateException extends Exception {
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

    public AbsenceStartIllegalDateException(final DateTime dt) {
        super(String.format("Votre demande commence un week-end ou un jour férié [%s].", dtf.print(dt)));
    }
}
