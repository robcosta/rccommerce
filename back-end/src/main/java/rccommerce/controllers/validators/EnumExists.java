package rccommerce.controllers.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumExistsValidator.class)  // Validará com o novo EnumExistsValidator
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumExists {
    String message() default "Valor inválido para enum.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    // Adiciona o parâmetro para definir o tipo de enum a ser validado
    Class<? extends Enum<?>> enumClass();
}

