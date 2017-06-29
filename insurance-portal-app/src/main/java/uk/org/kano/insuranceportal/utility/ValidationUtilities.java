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
package uk.org.kano.insuranceportal.utility;

import javax.validation.ConstraintValidatorContext;

import org.springframework.validation.BindException;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * A set of utilities to aid validation.
 * @author timh
 *
 */
public class ValidationUtilities {

	/**
	 * See {@link #addValidationError(String, ConstraintValidatorContext, String) addValidationError} with null third argument.
	 * @param field
	 * @param context
	 */
	public static void addValidationError(String field, ConstraintValidatorContext context) {
		addValidationError(field, context, null);
	}

	/**
	 * Store a validation problem on a field
	 * @param field The field name which has the error.
	 * @param context The context from the validation.
	 * @param messageTemplate The message to use or null to use the default.
	 */
	public static void addValidationError(String field, ConstraintValidatorContext context, String messageTemplate) {		
		context.buildConstraintViolationWithTemplate((null!=messageTemplate)?messageTemplate:context.getDefaultConstraintMessageTemplate())
			.addPropertyNode(field)
			.addConstraintViolation();
	}
	
	/**
	 * Validate an object from the JSR 303 validator annotations. If the function returns, the validation succeeded.
	 * @param validator The Spring validator reference
	 * @param object The object to validate.
	 * @param name The name of the object.
	 * @param validationHints Groups of validation tests to trigger
	 * @throws BindException When validation fails.
	 */
	public static void validateObject(Validator validator, Object object, String name, Object... validationHints) throws BindException {
		BindException bindException = new BindException(object, name);
		ValidationUtils.invokeValidator(validator, object, bindException, validationHints);
		if (bindException.hasErrors()) throw bindException;
	}
}
