package exceptions;

import models.JAbsence;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author f.patin
 */
public class AbsenceEndIllegalDateException extends Exception {
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

    public AbsenceEndIllegalDateException(final DateTime dt) {
        super(String.format("Votre demande se termine un week-end ou un jour férié [%s].", dtf.print(dt)));
    }
}
