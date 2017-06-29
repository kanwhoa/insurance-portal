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
package uk.org.kano.insuranceportal.config.app;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import uk.org.kano.insuranceportal.utility.StartupUtilities;

/**
 * Configure framework utilities, object converters, encoders etc.
 *    
 * @author timh
 *
 */
@Configuration
public class UtilityConfiguration {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment environment;

	/**
	 * Setup the password encoder to encrypt hash the passwords before being stored on their final
	 * resting place.
	 * 
	 * @return a BCryptPasswordEncoder setup with 10 rounds.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
	
	/**
	 * Configure a global ObjectMapper for Jackson
	 * 
	 * @return
	 */
	@Bean
	public ObjectMapper objectMapper() {
		// Jackson object mapping
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		AnnotationIntrospectorPair pair = new AnnotationIntrospectorPair(primary, secondary);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setAnnotationIntrospector(pair);
		
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// Pretty print if this is defined as a test environment
		if (environment.acceptsProfiles(StartupUtilities.PROFILE_TEST, StartupUtilities.PROFILE_UNITTEST)) {
			logger.info("Test environment detected, pretty printing JSON");
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		}
		return objectMapper;
	}
	
	/**
	 * Configure message converters. We are doing this here rather than in the web layer as there
	 * are a number of places where this is useful, especially in {@link WebSecurityConfiguration#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)}
	 * <br>
	 * 
	 * <ol>
	 * <li>Add Jackson converters for JSON</li>
	 * <li>Add JAXB for XML</li>
	 * <li>Add image converters for image types (serving dynamic PNGs etc)</li>
	 * <li>Add a generic string handler</li>
	 * </ol>
	 * 
	 * @return the list of message converters
	 */
	@Bean
	public List<HttpMessageConverter<?>> messageConverters(ObjectMapper objectMapper) {
		List<HttpMessageConverter<?>> ret = new ArrayList<>();
		
		MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
		jacksonConverter.setObjectMapper(objectMapper);
		
		ret.add(jacksonConverter);
		ret.add(new Jaxb2RootElementHttpMessageConverter());
		ret.add(new BufferedImageHttpMessageConverter());
		ret.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
		return ret;
	}

	/**
	 * Filter the environment properties for a specific start key and then remove it.
	 * FIXME: precedence ordering
	 * @param key The key to filter by
	 * @return
	 */
	private Properties filterProperties(String key) {
		Properties retProps = new Properties();
		if (!key.endsWith(".")) key = key+".";
		
		for(Iterator<org.springframework.core.env.PropertySource<?>> it = ((AbstractEnvironment)environment).getPropertySources().iterator(); it.hasNext(); ) {
			org.springframework.core.env.PropertySource<?> propertySource = it.next();
			
			if (propertySource instanceof EnumerablePropertySource) {
				for (String k: ((EnumerablePropertySource<?>)propertySource).getPropertyNames()) {
					if (k.startsWith(key)) {
						retProps.put(k.substring(key.length()), propertySource.getProperty(k));
					}
				}
			}
		}
		
		return retProps;
	}
	
	/**
	 * Get the ORM specific properties by copying from the main environment
	 */
	@Bean
	public Properties ormProperties() {
		return filterProperties("orm.");
	}
}
