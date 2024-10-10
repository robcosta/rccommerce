package rccommerce.controllers.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {

    String message() default "ID inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Altere o nome do parâmetro para evitar o erro
    boolean checkSpecialValues() default false;
}



