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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import uk.org.kano.insuranceportal.service.ContactUsService;

/**
 * Configure the service classes
 * @author timh
 *
 */
@Configuration
@ComponentScan(
		basePackages = {
				"uk.org.kano.insuranceportal.service"
		}
	)
@PropertySource("classpath:uk/org/kano/insuranceportal/application.properties")
public class ServiceConfiguration {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Environment environment;
		
	/**
	 * Create the contact us bean from the class specified by the property:
	 * service.contactus
	 * 
	 * @return The contact us service.
	 */
	@Bean
	public ContactUsService contactUsService() {
		Class<?> clazz = null;
		String propertyName = "service.contactus";
		String clazzName = environment.getProperty(propertyName);
		ContactUsService service = null;
		
		if (null == clazzName) throw new FatalBeanException("Envionment property \""+propertyName+"\" is not defined");
		try {
			clazz = Class.forName(clazzName);
			Class<? extends ContactUsService> subClazz = clazz.asSubclass(ContactUsService.class);
			
			service = subClazz.newInstance();
		} catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			logger.error("Failed to load the Contact Us service class");
			throw new FatalBeanException("Unable to load class \""+clazzName+"\"", e);
		}
		
		return service;
	}
}
