/*******************************************************************************
 * Copyright 2016 Tim Hurman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package uk.org.kano.insuranceportal.model.internal.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.org.kano.insuranceportal.model.internal.PasswordManager;
import uk.org.kano.insuranceportal.utility.ValidationUtilities;

/**
 * Password validation, borrows the regular expression from
 * https://www.mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/#
 * 
 * Modified to allow any punctuation, min 8 chars. This is coupled with an on-screen password strength
 * meter. The reason for this, is the on-screen feedback is a "nudge"
 * (https://en.wikipedia.org/wiki/Nudge_theory) to prompt the user to choose a good password. The theory
 * is that users want a "good" password. This is the enforcement to catch the users who don't.
 * 
 * @author timh
 *
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, PasswordManager> {
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{Punct}).{8,20})");
	private String fieldName;
	
	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		fieldName = constraintAnnotation.fieldName();
	}

	@Override
	public boolean isValid(PasswordManager value, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		String password1=value.getPassword1(),
				password2=value.getPassword2();
		
		// Check if not Empty, we'll return a different error message
		if (null == password1) {
			ValidationUtilities.addValidationError(fieldName+"1", context, "{validation.password.empty}");
		}
		if (null == password2) {
			ValidationUtilities.addValidationError(fieldName+"2", context, "{validation.password.empty}");
		}
		if (null == password1 || null == password2) return false;
		
		// Check if passwords are not equal
		if (!password1.equals(password2)) {
			ValidationUtilities.addValidationError(fieldName+"1", context, "{validation.password.notequal}");
			ValidationUtilities.addValidationError(fieldName+"2", context, "{validation.password.notequal}");
			return false;
		}
		
		// Check for password complexity
		Matcher m = PASSWORD_PATTERN.matcher(password1);
		if (!m.matches()) {
			ValidationUtilities.addValidationError(fieldName+"1", context, "{validation.password.tooweak}");
			ValidationUtilities.addValidationError(fieldName+"2", context, "{validation.password.tooweak}");
			return false;
		}
		
		return true;
	}
}
