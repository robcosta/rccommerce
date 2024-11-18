package rccommerce.services.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import rccommerce.services.exceptions.InvalidPasswordExecption;

public class ValidPassword {

    public static String isValidPassword(String password) {
        StringBuilder msg = new StringBuilder("Senha inválida:");

        // Verifica o tamanho mínimo
        if (password == null || password.length() < 6) {
            msg.append(" pelo menos 6 dígitos.");
            throw new InvalidPasswordExecption(msg.toString());
        }

        // Verifica os critérios usando expressões regulares
        if (!password.matches(".*[A-Z].*")) {
            msg.append(" pelo menos uma letra maiúscula.");
            throw new InvalidPasswordExecption(msg.toString());
        }

        if (!password.matches(".*[a-z].*")) {
            msg.append(" pelo menos uma letra minúscula.");
            throw new InvalidPasswordExecption(msg.toString());
        }

        if (!password.matches(".*[0-9].*")) {
            msg.append(" pelo menos um dígito.");
            throw new InvalidPasswordExecption(msg.toString());
        }

        if (!password.matches(".*[!@#$%^&*()\\+=-].*")) {
            msg.append(" pelo menos um caractere especial.");
            throw new InvalidPasswordExecption(msg.toString());
        }

        // Retorna a senha encriptada se válida
        return new BCryptPasswordEncoder().encode(password);
    }
}
