package rccommerce.controllers.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ValidIdValidator implements ConstraintValidator<ValidId, String> {

    private Map<Long, Supplier<String>> messageMap;
    private boolean checkSpecialValues;  // Alterado para refletir o novo nome do parâmetro

    @Override
    public void initialize(ValidId constraintAnnotation) {
        // Inicializa o valor do novo parâmetro
        this.checkSpecialValues = constraintAnnotation.checkSpecialValues();

        // Mapa com mensagens personalizadas
        messageMap = Map.of(
            1L, () -> "Não é permitido operar no Operador: 'Administrador'", 
            4L, () -> "Não é permitido operar no cliente: 'Venda ao Consumidor'"
        );
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
    	if(value.isEmpty()) {
    		return true;
    	}
        Long id = null;

        // 1. Valida se o valor pode ser convertido para Long
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException e) {
            // Se a conversão falhar, adiciona a mensagem de erro
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("O valor do parâmetro 'id: "+ value +"' não é um número válido.")
                   .addConstraintViolation();
            return false;
        }

        // 2. Se `checkSpecialValues` for `true`, faz a validação adicional
        if (checkSpecialValues) {
            return Optional.ofNullable(messageMap.get(id))
                .map(messageSupplier -> {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(messageSupplier.get())
                           .addConstraintViolation();
                    return false;
                }).orElse(true);
        }

        // Se `checkSpecialValues` for `false`, apenas valida a conversão para Long
        return true;
    }
}
