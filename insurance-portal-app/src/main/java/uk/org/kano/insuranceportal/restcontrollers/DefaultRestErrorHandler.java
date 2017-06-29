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
package uk.org.kano.insuranceportal.restcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import uk.org.kano.insuranceportal.errors.ObjectNotFoundException;
import uk.org.kano.insuranceportal.errors.UserNotBoundException;
import uk.org.kano.insuranceportal.model.internal.Empty;
import uk.org.kano.insuranceportal.model.internal.FieldError;

/**
 * A set of default error handlers. Each controller can override/define additional error handlers
 * using the same approach. However, these are a set of generic ones that will be used in common.
 * 
 * @author timh
 *
 */
@ControllerAdvice(basePackageClasses={DefaultRestErrorHandler.class})
public class DefaultRestErrorHandler {
	private Logger logger = LoggerFactory.getLogger(DefaultRestErrorHandler.class);
	
	@Autowired
	private Environment environment;
	
	/**
	 * Exception handler for validation errors arising from @Valid
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<FieldError>> handleException(MethodArgumentNotValidException exception) {
		return new ResponseEntity<>(FieldError.generateFromBindingResult(exception.getBindingResult()), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Exception handler for validation errors arising throwing a BindException
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<List<FieldError>> handleException(BindException exception) {
		return new ResponseEntity<>(FieldError.generateFromBindingResult(exception), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler for services that don't find an object
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<Empty> handleException(ObjectNotFoundException exception) {
		return new ResponseEntity<>(new Empty(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Exception handler for for when a user calls a service that needs to be registered
	 * but has not registered a product yet.
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(UserNotBoundException.class)
	public ResponseEntity<Empty> handleException(UserNotBoundException exception) {
		return new ResponseEntity<>(new Empty(), HttpStatus.FORBIDDEN);
	}
	
	/**
	 * Default error handler so that we don't end up returning stack traces to the user. In debug mode,
	 * this will return a simple String[][3] list, being [exception name, message, stack trace].
	 * 
	 * If the application is not in development, the return will be blank. If in development, it will
	 * return the stack trace to the view.
	 * 
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<List<String[]>> handleException(Exception exception) {
		List<String[]> exceptions = new ArrayList<>();
		logger.error("Caught unhandled error", exception);
		
		// Return a blank to the user if not in development
		if (environment.acceptsProfiles("!dev"))
			return new ResponseEntity<>(exceptions, HttpStatus.INTERNAL_SERVER_ERROR);
		
		parseExceptionList(exception, exceptions);
		return new ResponseEntity<>(exceptions, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Generate a human readable form of the Exception
	 * 
	 * @param t
	 * @param exceptions
	 */
	private void parseExceptionList(Throwable t, List<String[]> exceptions) {
		String[] entry = new String[3];
		entry[0] = t.getClass().getCanonicalName();
		entry[1] = t.getLocalizedMessage();
		
		StringBuffer st = new StringBuffer();
		for (StackTraceElement e: t.getStackTrace()) {
			st.append(e.toString());
			st.append('\n');
		}
		entry[2] = st.toString().trim();
		exceptions.add(entry);
		
		if (null != t.getCause()) parseExceptionList(t.getCause(), exceptions);
	}
}
