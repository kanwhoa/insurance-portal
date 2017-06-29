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
import uk.org.kano.insuranceportal.model.domain.Policy;
import uk.org.kano.insuranceportal.model.internal.User;
import uk.org.kano.insuranceportal.service.PolicyService;
import uk.org.kano.insuranceportal.utility.LoginUtilities;

/**
 * This is one of the "Enterprise" services. It returns a set of Policy objects that the user has access to. Access is determined
 * dynamically as the user has to play a role on them. The policy object may be further sliced by the service layer depending on the
 * role the person plays on the policy.
 * 
 * @author timh
 *
 */
@RestController
@RequestMapping("/services")
public class PolicyController {
	private Logger logger = LoggerFactory.getLogger(PolicyController.class);
	
	@Autowired
	private PolicyService policyService;
	
	/**
	 * Find a list of policies that belong to me.
	 * 
	 * @throws ObjectNotFoundException When the person is not found
	 */
	@RequestMapping(value="/policies/mine", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<Policy>> getMyInformation() throws UserNotBoundException {
		logger.info("Returning callers policy portfolio");
		
		User sysUser = LoginUtilities.getCurrentUser();
		Set<String> personIds = sysUser.getPersonIds(); 
		if (null == personIds || 0 == personIds.size()) throw new UserNotBoundException();
		
		Set<Policy> policies = policyService.findPolicyByPersons(personIds);
		if (null == policies) policies = new HashSet<Policy>();
		
		return new ResponseEntity<Set<Policy>>(policies, HttpStatus.OK);
	}

	/**
	 * Find a policy specifically, we need to check the security here. The rules are TODO:
	 * 
	 * @param policyId the enterprise ID of the policy the caller wishes to view.
	 * @throws ObjectNotFoundException When the person is not found
	 */
	@RequestMapping(value="/people/{policyId:[A-Z0-9]+}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Policy> getPersonInformation(@PathVariable String policyId) throws ObjectNotFoundException {
		User user = LoginUtilities.getCurrentUser();
		logger.info("Person \""+user.getName()+"\" requesting policy "+policyId);
		
		Policy p = policyService.findPolicy(policyId);
		
		if (null == p) throw new ObjectNotFoundException();
		return new ResponseEntity<Policy>(p, HttpStatus.OK);
	}

}

