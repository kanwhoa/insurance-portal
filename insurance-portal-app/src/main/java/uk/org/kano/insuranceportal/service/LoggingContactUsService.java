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
package uk.org.kano.insuranceportal.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A basic implementation of the Contact Us interface. This just drops all of the data to the log file.
 * This is only available in development.
 * 
 * @author timh
 *
 */
@Profile("dev")
@Transactional
@Service
public class LoggingContactUsService implements ContactUsService {
	private Logger logger = LoggerFactory.getLogger(LoggingContactUsService.class);
	
	@Override
	public void createContact(Map<String, String> attributes) {
		logger.info("Got new contact:");
		for (String key: attributes.keySet()) {
			logger.info(" --> "+key+": "+attributes.get(key));
		}
	}

}
