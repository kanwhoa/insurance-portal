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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.org.kano.insuranceportal.errors.ObjectNotFoundException;
import uk.org.kano.insuranceportal.errors.UserNotBoundException;
import uk.org.kano.insuranceportal.model.domain.Person;
import uk.org.kano.insuranceportal.model.internal.User;
import uk.org.kano.insuranceportal.service.PersonService;
import uk.org.kano.insuranceportal.utility.LoginUtilities;

/**
 * This is one of the "Enterprise" services. It returns the person who holds the account, which
 * is different from the User object. When this is called, the user must be logged in, and have
 * "bound" their account to an Enterprise object. If no binding exists, then return empty. To return
 * or manage the user object, check the {@link UserController} class.
 * 
 * @author timh
 *
 */
@RestController
@RequestMapping("/services")
public class PersonController {
	private Logger logger = LoggerFactory.getLogger(PersonController.class);
	
	@Autowired
	private PersonService personService;
	
	/**
	 * Find my personal information. The person must be bound to call this. There is an interesting side effect
	 * from the business model in that an account may be bound to more than one Person object. This was intentional,
	 * see {@link Person} for more information on the reasons.
	 * 
	 * @throws ObjectNotFoundException When the person is not found
	 */
	@RequestMapping(value="/people/me", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<Person>> getMyInformation() throws UserNotBoundException {
		logger.info("Returning callers personal information");
		
		User sysUser = LoginUtilities.getCurrentUser();
		Set<String> personIds = sysUser.getPersonIds(); 
		if (null == personIds || 0 == personIds.size()) throw new UserNotBoundException();
		
		Set<Person> people = personService.findPerson(personIds);
		if (null == people) people = new HashSet<Person>();
		
		return new ResponseEntity<Set<Person>>(people, HttpStatus.OK);
	}

	/**
	 * Find another user's personal information. This is where we need security. The security rules will
	 * look as follows: TODO
	 * 
	 * <ol>
	 * <li>If the personId requested is the caller, then return full info</li>
	 * <li>If the personId requested plays a role on a policy that the caller is a policy holder of, then
	 *     return personal information, but sliced, only relating to this policy. Do not show the person's
	 *      other policies.</li>
	 * <li>Otherwise return 403 not authorised with an empty object</li>
	 * </ol>
	 * 
	 * @param personId the enterprise ID of the person's information the caller wishes to view.
	 * @throws ObjectNotFoundException When the person is not found
	 */
	@RequestMapping(value="/people/{personId:[A-Z0-9]+}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> getPersonInformation(@PathVariable String personId) throws ObjectNotFoundException {
		User user = LoginUtilities.getCurrentUser();
		logger.info("Person \""+user.getName()+"\" requesting personal information for "+personId);
		Person p = personService.findPerson(personId);
		
		if (null == p) throw new ObjectNotFoundException();
		return new ResponseEntity<Person>(p, HttpStatus.OK);
	}

}

