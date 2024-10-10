package rccommerce.controllers.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumExistsValidator implements ConstraintValidator<EnumExists, Object> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumExists constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;  // Considera valores nulos como válidos. Caso contrário, pode-se retornar false aqui.
        }

        // Verifica se o valor é uma lista ou coleção
        if (value instanceof Iterable<?>) {
            for (Object element : (Iterable<?>) value) {
                if (!isEnumValueValid(element)) {
                    return false;  // Um ou mais elementos da lista não são válidos
                }
            }
            return true;
        }

        // Trata o caso de um único valor enum
        return isEnumValueValid(value);
    }

    // Verifica se o valor passado existe no enum
    private boolean isEnumValueValid(Object value) {
        if (value instanceof String) {
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(value.toString()));
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumConstant -> enumConstant.equals(value));
    }
}

