package rccommerce.services.util;

import java.time.Instant;

public class ConvertString {

    /**
     * Converte uma string para Long ou retorna null se a string for inválida.
     */
    public static Long parseLongOrNull(String value) {
        return (value != null && !value.isEmpty()) ? Long.valueOf(value) : null;
    }

    /**
     * Converte uma string para Integer ou retorna null se a string for
     * inválida.
     */
    public static Integer parseIntegerOrNull(String value) {
        return (value != null && !value.isEmpty()) ? Integer.valueOf(value) : null;
    }

    /**
     * Converte uma string de data para Instant ou retorna null se a string for
     * inválida.
     */
    public static Instant parseDateOrNull(String date) {
        return (date != null && !date.isEmpty()) ? DateUtils.convertDay(date, "dd/MM/yyyy") : null;
    }
}
