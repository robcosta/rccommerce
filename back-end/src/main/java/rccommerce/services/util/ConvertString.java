package rccommerce.services.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rccommerce.services.exceptions.InvalidArgumentExecption;

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
     * inválida. Não aceita datas inválidas como 31/02/2021 ou 31/04/2023.
     */
    public static Instant parseDateOrNull(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        String msg = "Data: " + date + " inválida. Formato aceitável 'dd/mm/aaaa'";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            // Divide a data manualmente em dia, mês e ano
            String[] parts = date.split("/");
            if (parts.length != 3) {
                throw new InvalidArgumentExecption(msg);
            }

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Valida o intervalo do mês e ano
            if (month < 1 || month > 12 || year < 1) {
                throw new InvalidArgumentExecption(msg);
            }

            // Obtem o número máximo de dias no mês e ano fornecidos
            int maxDays = getDaysInMonth(month, year);
            if (day < 1 || day > maxDays) {
                throw new InvalidArgumentExecption(msg);
            }

            // Se tudo estiver correto, converte para Instant
            LocalDate parsedDate = LocalDate.parse(date, formatter);
            return parsedDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);

        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidArgumentExecption(msg);
        }
    }

    /**
     * Retorna o número de dias em um mês específico, considerando anos
     * bissextos.
     */
    private static int getDaysInMonth(int month, int year) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 ->
                31;
            case 4, 6, 9, 11 ->
                30;
            case 2 ->
                isLeapYear(year) ? 29 : 28;
            default ->
                0;
        }; // Fevereiro: verifica se o ano é bissexto
        // Isso nunca deve ocorrer devido à validação prévia
    }

    /**
     * Verifica se um ano é bissexto.
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
