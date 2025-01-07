package rccommerce.services.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rccommerce.services.exceptions.InvalidArgumentExecption;

public class DateUtils {

    public static Instant convertToStartOfDay(String dateString, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentExecption("Data inválida. Formato esperado: " + pattern);
        }
    }
}
