package rccommerce.controllers.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CheckPassword implements ConstraintValidator<Password, String> {

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		
		if (!password.matches("^\\d+$")) {
			return false;
		}
		return true;
	}
		

}
