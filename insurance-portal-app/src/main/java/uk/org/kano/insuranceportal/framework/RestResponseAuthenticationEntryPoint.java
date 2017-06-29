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
package uk.org.kano.insuranceportal.framework;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

/**
 * This is an authentication entry point which will respond with an HTTP 401 (unauthorised)
 * response for a specific path. Requests outside that path will be delegated to the specified
 * AuthenticationEntryPoint for further handling.
 * 
 * @see uk.org.kano.insuranceportal.config.app.WebSecurityConfiguration#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
 *  
 * @author timh
 *
 */
public class RestResponseAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
	private MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
	private Object responseObject;
	private List<HttpMessageConverter<?>> httpMessageConverters;
	
	/**
	 * Constructor. This will set the default message converts to a simple string converter.
	 * It will also set the response object to a simple String containing the textual representation
	 * of the HttpStatus property.
	 * 
	 * @param delegate The AuthenticationEntryPoint to forward any non matching requests to
	 */
	public RestResponseAuthenticationEntryPoint() {
		httpMessageConverters = new ArrayList<>();
		httpMessageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		responseObject = httpStatus.getReasonPhrase();
	}
		
	/**
	 * Set the HTTP error code to return
	 * @param code The HTTP error code.
	 * @return this
	 */
	public RestResponseAuthenticationEntryPoint setCode(HttpStatus httpStatus) {
		Assert.notNull(httpStatus, "code must be a valid HTTP error code");
		this.httpStatus = httpStatus;
		return this;
	}
	
	/**
	 * Set the response object to be converted and returned. By default this will return an empty object.
	 * @param code The HTTP error code.
	 * @return this
	 */
	public RestResponseAuthenticationEntryPoint setObject(Object responseObject) {
		Assert.notNull(httpStatus, "responseObject must not be null");
		this.responseObject = responseObject;
		return this;
	}

	/**
	 * Set the list of message converters to use.
	 * @param code The HTTP error code.
	 * @return this
	 */
	public RestResponseAuthenticationEntryPoint setHttpMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
		Assert.notNull(httpMessageConverters, "httpMessageConverters must not be null");
		Assert.isTrue(httpMessageConverters.size()>0, "httpMessageConverters must have at least one converter");
		this.httpMessageConverters = httpMessageConverters;
		return this;
	}

	/**
	 * Write the response out using the specified message converters. If no HttpMessageConverters opt
	 * for handling the type, then the response will be empty. 
	 * 
	 * @see AuthenticationEntryPoint
	 * 
	 * @param request
	 * @param response
	 * @param authException
	 * @throws IOException
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {		
		response.setCharacterEncoding(mediaType.toString());
		
		// There is probably a better way to do this, but it fairly cheap and cheerful
		ServletServerHttpResponse springResponse = new ServletServerHttpResponse(response);
		springResponse.setStatusCode(httpStatus);
		for (HttpMessageConverter<?> converter: httpMessageConverters) {
			if (converter.canWrite((Class<?>)responseObject.getClass(), mediaType)) {
				((HttpMessageConverter<Object>)converter).write(responseObject, mediaType, springResponse);
				break;
			}
		}
	}

}
