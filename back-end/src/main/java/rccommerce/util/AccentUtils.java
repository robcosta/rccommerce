package rccommerce.util;

import java.text.Normalizer;

public class AccentUtils {
    // Remove acentos de uma string
    public static String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", ""); // Remove os diacríticos
    }
}

