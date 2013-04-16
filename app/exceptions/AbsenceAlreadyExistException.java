package exceptions;

import models.JAbsence;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author f.patin
 */
public class AbsenceAlreadyExistException extends Exception {
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");

    public AbsenceAlreadyExistException(final JAbsence absence) {
        super(String.format("Une demande d'absence existe déjà pour les dates suivantes [%s -> %s].", dtf.print(absence.startDate), dtf.print(absence.endDate)));
    }
}
