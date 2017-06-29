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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.org.kano.insuranceportal.model.internal.Empty;
import uk.org.kano.insuranceportal.service.ContactUsService;

/**
 * Used by the Contact Us pages to retrieve social login details. As the page is flexible,
 * we will return a map of objects so the front-end can pick and choose.
 * 
 * @author timh
 *
 */
@RestController
@RequestMapping("/services")
public class ContactUsController {
	
	@Autowired
	private ContactUsService contactUsService;
	
	/**
	 * Simple contact us handler. For this, we will take unstructured data as we will want to do
	 * A/B testing on the front. This means we cannot tie it directly to a model. This also means that
	 * the font view has to do validation of the data and this service will just trust it.
	 * 
	 * @return nothing if successful (200 OK).
	 */
	@RequestMapping(value="/contactus", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Empty> contactUs(@RequestBody Map<String, String> requestAttributes) {
		contactUsService.createContact(requestAttributes);
		return new ResponseEntity<Empty>(new Empty(), HttpStatus.OK);
	}

}
