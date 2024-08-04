package rccommerce.controllers.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckEmail implements ConstraintValidator<EmailCustom, String> {

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$";
		return email.matches(regex);
	}
}
