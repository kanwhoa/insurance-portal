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
package uk.org.kano.insuranceportal.model.internal;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

/**
 * This is class is returned by REST services when there is a problem with request object
 * 
 * This class is meant to support the showing of error messages on the view layer so we must
 * return a localised error message. We could alternatively do this by returning an error code
 * for each error, but that would mean managing a disparate list of error codes to errors somewhere
 * in the frontend. The advantage is that we could change the error messages quickly, however, they
 * could not be dynamic and as they don't change often we'll assume that people choose the error
 * message text carefully the first time.
 * 
 * Note that fields can have multiple error messages. For example a password may not match the field
 * copy and may not meet the minimum complexity. Therefore we will structure the error object in that
 * way.
 * 
 * TODO: make this locale aware
 * 
 * @author timh
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "validationFieldErrorType", propOrder = {"name", "errors"})
public class FieldError {
	@XmlID
	@XmlAttribute
	private String name;
	
	@XmlElement
	private List<String> errors = new ArrayList<>();
	
	private FieldError(String name) {
		Assert.notNull(name, "Name cannot be null");
		this.name = name;
	}
	public void addError(String error) {
		Assert.notNull(name, "Error cannot be null");
		errors.add(error);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	
	/**
	 * Get an established field, or create a new one
	 * @param name
	 * @return
	 */
	private static FieldError getFieldError(List<FieldError> errors, String name) {
		Assert.notNull(errors, "Error list cannot be null");
		Assert.notNull(name, "Field name cannot be null");
		FieldError field=null;
		for (FieldError f: errors) {
			if (name.equals(f.getName())) {
				field = f;
				break;
			}
		}
		
		if (null == field) {
			field = new FieldError(name);
			errors.add(field);
		}
		
		return field;
	}
		
	/**
	 * Factory builder method
	 * @param bindingResult
	 * @return
	 */
	public static List<FieldError> generateFromBindingResult(BindingResult bindingResult) {
		List<FieldError> ret = new ArrayList<>();
		
		// If there are global errors, then the error was too great, and chances are
		// someone is doing something fishy. Therefore, just return an empty object
		if (bindingResult.hasGlobalErrors()) {
			return null;
		}
		
		// Evaluate the field errors
		for (org.springframework.validation.FieldError fe: bindingResult.getFieldErrors()) {
			FieldError f = getFieldError(ret, fe.getField());
			f.addError(fe.getDefaultMessage());
		}

		return ret;
	}
}
