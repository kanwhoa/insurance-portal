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

/**
 * An interface for storing the results of the "contact us" service. The idea being that people can
 * create a class implementing this interface. It will then be dynamically loaded from the environment
 * configuration.
 *  
 * @author timh
 *
 */
public interface ContactUsService {
	/**
	 * Store a contact.
	 * 
	 * @param attributes The set of attributes passed to the service.
	 */
	public void createContact(Map<String, String> attributes);
}
