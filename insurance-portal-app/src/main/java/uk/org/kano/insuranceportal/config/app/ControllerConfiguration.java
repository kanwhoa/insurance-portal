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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import uk.org.kano.insuranceportal.utility.StartupUtilities;

/**
 * Configure the service classes
 * @author timh
 *
 */
@Configuration
@PropertySource("classpath:uk/org/kano/insuranceportal/application.properties")
public class ControllerConfiguration {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
		
	/**
	 * The localised messages. This is used in the JSP files as well as the javax.validation messages.
	 * Using the reloadable version so that we can load from UTF-8 encoded files (plain or XML). The
	 * cache timeout is left to the default of -1. Using XML files to allow the saving of UTF-8 encoded
	 * messages easily
	 * 
	 * @return A message source
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

		messageSource.setBasename("classpath:"+StartupUtilities.getPackageRoot()+"/messages");
		messageSource.setUseCodeAsDefaultMessage(true);
		logger.debug("Loading message source from:");
		if (logger.isDebugEnabled()) {
			for (String basename: messageSource.getBasenameSet()) {
				logger.debug("  "+basename);
			}
		}
		
		return messageSource;
	}
	
	/**
	 * Bootstrap the JSR303 validator with our own message source. Note that the type
	 * must match else it will not get auto injected to the {@link uk.org.kano.insuranceportal.config.web.WebConfiguration#getValidator()} call.
	 * 
	 * Note that there are a number of validators instantiated and they all seem to use a different entry point
	 * so it doesn't look like this bean is being picked up consistently. However, it is being picked up for the
	 * JSR303 validation. The other instances appear to be in the EntityManager.
	 * 
	 * @return A validator configured to our message source.
	 */
	@Bean
	public LocalValidatorFactoryBean validator(MessageSource messageSource) {
		LocalValidatorFactoryBean ret = new LocalValidatorFactoryBean();
		ret.setValidationMessageSource(messageSource);
		return ret;
	}
}
