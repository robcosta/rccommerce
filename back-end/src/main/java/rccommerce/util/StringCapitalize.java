package rccommerce.util;

public class StringCapitalize {

    public static String words(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] words = text.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
}
