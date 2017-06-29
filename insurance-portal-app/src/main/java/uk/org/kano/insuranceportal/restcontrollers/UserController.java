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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import uk.org.kano.insuranceportal.model.domain.Person;
import uk.org.kano.insuranceportal.model.domain.Policy;
import uk.org.kano.insuranceportal.model.domain.PolicyHolder;
import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority;
import uk.org.kano.insuranceportal.model.internal.User;
import uk.org.kano.insuranceportal.model.internal.UserBind;
import uk.org.kano.insuranceportal.model.internal.UserRegistration;
import uk.org.kano.insuranceportal.model.internal.validators.Password;
import uk.org.kano.insuranceportal.service.PersonService;
import uk.org.kano.insuranceportal.service.PolicyService;
import uk.org.kano.insuranceportal.service.UserService;
import uk.org.kano.insuranceportal.utility.LoginUtilities;
import uk.org.kano.insuranceportal.utility.ValidationUtilities;

/**
 * Used by the registration page to create the accounts. This is designed to be loosely coupled
 * so that the page itself can be hosted on a separate CMS. However, we will pass objects in and
 * out of the controllers and validate those.<br><br>
 * 
 * Note we are going to keep email separate from the account username so that we can detect
 * people signing up with the same username, tell them that that account exists without leaking
 * information about a pre-existing account. I.e. we can tell them that an account exists with that
 * email, but not tell them the ID. Similarly, we can detect multiple usernames without exposing the
 * email, so we protect the users accounts.<br><br>
 * 
 * Similarly, we can bind a local account to a social account & visa versa.<br><br>
 * 
 * TODO: we should probably define a special log file AUDIT or similar to hold user related actions
 * away from the standard log file
 * 
 * @author timh
 *
 */
@RestController
@RequestMapping("/services")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private WebRequest webRequest;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PersonService personService;

	@Autowired
	private PolicyService policyService;

	private ProviderSignInUtils providerSignInUtils;
	
	/**
	 * Constructor
	 * @param connectionFactoryLocator
	 * @param connectionRepository
	 */
	@Autowired
	public UserController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository connectionRepository) {
		providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
	}
	
	/**
	 * Simple handler to get the current user information. This helps to determine if the user is
	 * logged in or not.
	 * 
	 * @return The UserRegistration object partially filled to the extent possible
	 */
	@RequestMapping(value="/user/status", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<User> getLoginState() {
		User user = LoginUtilities.getCurrentUser();
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	/**
	 * Get the data from the registered social provider and return it to the registration page or
	 * return an empty registration object if not a social user.
	 * 
	 * @return The UserRegistration object partially filled to the extent possible
	 */
	@RequestMapping(value="/user/register", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<UserRegistration> getUserData() {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
		UserRegistration user = new UserRegistration();
		user.setSocialLogin(false);
		
		// Return a blank response if this is not a social user
		if (null == connection) {
			return new ResponseEntity<UserRegistration>(user, HttpStatus.OK);
		}		

		// If the connection is good, then get the details
		UserProfile profile = connection.fetchUserProfile();
		if (null == profile) { // unable to get the profile, something weird happened, just pretend its a normal register
			return new ResponseEntity<UserRegistration>(user, HttpStatus.OK);
		}
		
		user.setSocialLogin(true);
		user.setName(profile.getName());
		user.setUsername(connection.getKey().toString());
		user.setEmail(profile.getEmail());
		return new ResponseEntity<UserRegistration>(user, HttpStatus.OK);
	}

	
	/**
	 * Complete the registration process.
	 * https://github.com/spring-projects/spring-social-samples/blob/master/spring-social-showcase/src/main/java/org/springframework/social/showcase/signup/SignupController.java
	 * <br><br>
	 * Validation of the parameter objects will occur for all Default groups on entry.<br>
	 * Note that we will never get a username duplicate error on the registration page for a social ID because
	 * once the ID is checked, the login part will try to authenticate the user. The only situation is is someone
	 * removes the user from the Users table, which means data fiddling is happening and the whole table cannot
	 * be trusted.<br>
	 * Refer to the documented user flows for high level details of what is happening.
	 * 
	 * @param user The user to register (validated)
	 * @return The location of where to send the user after registration.
	 * @throws BindException If there was a problem validating the object
	 */
	@RequestMapping(value="/user/register", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<URI> putUserData(@Valid @RequestBody UserRegistration user) throws BindException {
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
		URI nextUrl = null;
		try {
			nextUrl = new URI(webRequest.getContextPath()); // Send to the root.
		} catch (URISyntaxException e) {
			throw new RuntimeException("Context root appears bad: "+webRequest.getContextPath(), e);
		}
		
		// If the connection doesn't exist, then process this as a local user creation
		if (null != connection) {		
			user.setSocialLogin(true);
			// Override the username and password in case a smartarse decided to change it
			user.setUsername(connection.getKey().toString());
			// Setting the password to a random value effectively locks the account from direct login
			user.setPassword1(LoginUtilities.generateRandomPassword());
			// When doing a social login, drop the user back on the authenticate URL so that the
			// framework can establish the authentication properly.
			try {
				nextUrl = new URI(nextUrl.toString()+"/auth/"+connection.getKey().getProviderId());
			} catch (URISyntaxException e) {
				throw new RuntimeException("Unable to build path to social login", e);
			}
		} else {
			user.setSocialLogin(false);	

			// Validate the password fields. Notice, we do not read from the object until it has been validated.
			ValidationUtilities.validateObject(validator, user, "user", Password.class);
		}
		
		// Create and store the user object
		ArrayList<GrantedAuthority> roles = new ArrayList<>();
		// Check for role not existing
		RoleGrantedAuthority role = userService.getRole(RoleGrantedAuthority.Role.USER);
		if (null == role) throw new RuntimeException("Role \""+RoleGrantedAuthority.Role.USER+"\" not defined");
		roles.add(role);
		
		User newUser = new User(user.getUsername(), user.getPassword1(), roles);
		newUser.setEmail(user.getEmail());
		newUser.setName(user.getName());
		// If this is a social login, and the user hasn't changed their email, then treat as already validated
		if (user.isSocialLogin()) {
			UserProfile profile = connection.fetchUserProfile();
			if (null != profile && user.getEmail().equals(profile.getEmail())) newUser.setEmailConfirmed(true);
		}
		userService.createUser(newUser);
		
		// Complete the signups as required
		if (user.isSocialLogin()) {
			providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);
		} else { // Log this person in
			Authentication authentication = new UsernamePasswordAuthenticationToken(newUser, null, roles);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		logger.info("Created new user "+user.getUsername());
		
		// TODO: send an email to confirm the mail address is valid

		// Return the location to goto next		
		return new ResponseEntity<URI>(nextUrl.normalize(), HttpStatus.OK);
	}
	
	
	/**
	 * This is to bind a {@link User} to a {@link Person}. This establishes the user in the business
	 * context. However, in order to bind, we will need to ask specific questions about the {@link Person}
	 * and {@link Policy} objects. That is, our assumption is that you can find out about the person, we need
	 * to know something about the policy that only the user should know. In a more advanced model, this might
	 * include an SMS callback to the {@link Person}'s registered phone number. The level of detail is entirely
	 * a business decision. That said, asking too much detail or complex information will limit the usability
	 * and so an acceptable risk tradeoff between financial loss (and recovery) and convenience needs to be
	 * made.<br>
	 * 
	 * Usually, we would return a 404 on not found for a data object. here, we will just return a negative
	 * bind response, but no more - do not indicate which piece of information is incorrect. That seems
	 * unhelpful, but it mitigates a brute force bind. Also, on error, consider slowing the respond, but
	 * be careful of Thread.sleep(), which will make it easier to DoS the application server.
	 * <br>
	 * The check here looks at the name, which must be exactly as stored, including case. It's a business
	 * decision on whether to ignore case, but most users are comfortable with this (if told), since it
	 * is the default way credit card companies work (name must be as printed on the card).
	 * 
	 * @return A boolean TRUE if the bind succeeded.
	 */
	@RequestMapping(value="/user/bind", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> bindToPerson(@Valid @RequestBody UserBind userBind) {
		// Check policy level information
		Policy policy = policyService.findPolicy(userBind.getPolicyId());
		if (null == policy)
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
		
		if (!policy.getStartDate().equals(userBind.getPolicyStartDate()))
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
		
		// Policy information is good, get the policyholder roles to check the user.
		// We could walk the policy object relation to do this, bit it's a little safer
		// (from a JPA perspective) to issue a new query form the user side. This is because a policy
		// side query would get each user one by one, or have to slice a list. Easier to just ask
		// JPA to do that.
		Set<Person> policyholders = personService.getPeopleFromPolicyRole(policy.getPolicyId(), PolicyHolder.class);
		
		Person policyHolder = null;
		for (Person p: policyholders) {
			if (p.getGivenName().equals(userBind.getGivenName()) && p.getFamilyName().equals(userBind.getFamilyName()) && p.getDateOfBirth().equals(userBind.getDateOfBirth())) {
				policyHolder = p;
				break;
			}
		}
		// If the information doesn't match, return a failure
		if (null == policyHolder) return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
		
		// Update the user object
		User sysUser = LoginUtilities.getCurrentUser();

		// if the user hasn't validated their email, then don't allow binding. TODO: show this preemptively on the page
		if (!sysUser.isEmailConfirmed())
			return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);

		// If the person ID is already bound, short circuit the request
		if (sysUser.getPersonIds().contains(userBind.getPolicyId()))
				return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);

		// Add the personId to the linked set.
		User user = userService.findUserById(sysUser.getId());
		user.addPersonId(policyHolder.getPersonId());
		sysUser.addPersonId(policyHolder.getPersonId()); // bit iffy, since the object is read only it won't save, but still seems bad creating two branches of the same object
		userService.save(user);
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}

}
