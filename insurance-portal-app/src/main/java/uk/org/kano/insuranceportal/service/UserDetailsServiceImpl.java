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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority;
import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority.Role;
import uk.org.kano.insuranceportal.model.internal.User;
import uk.org.kano.insuranceportal.repository.RoleGrantedAuthorityRepository;
import uk.org.kano.insuranceportal.repository.UserRepository;

/**
 * This is a service for finding users in the user store, however that
 * may be implemented (JDBC, JPA, LDAP etc).
 * 
 * @author hophVWK
 *
 */
@Service("SocialUserDetailsService")
public class UserDetailsServiceImpl implements UserService, UserDetailsService, SocialUserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleGrantedAuthorityRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Find a User object in the user repository.
	 * @see UserDetailsService#loadUserByUsername(String)
	 */
	@Override
	@Transactional(
			propagation=Propagation.REQUIRES_NEW,
			readOnly=true,
			noRollbackFor={
					UsernameNotFoundException.class
			}
	)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findUser(username);
		if (null == user) throw new UsernameNotFoundException("No user found with username: " + username);
		return user;
	}

	/**
	 * Find a User object in the user repository. Performs the same as {@link #loadUserByUsername(String)}.
	 * @see SocialUserDetailsService#loadUserByUserId(String)
	 */
	@Override
	@Transactional(
			propagation=Propagation.REQUIRES_NEW,
			readOnly=true,
			noRollbackFor={
					UsernameNotFoundException.class
			}
	)
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		User user = findUser(userId);
		if (null == user) throw new UsernameNotFoundException("No user found with username: " + userId);
		return user;
	}

	/**
	 * Find a User object in the user repository. Performs the same as {@link #loadUserByUsername(String)}
	 * except that it returns an updatable object.
	 * 
	 * @see SocialUserDetailsService#loadUserByUserId(String)
	 */
	@Override
	@Transactional
	public User findUserById(long id) {
		return userRepository.findById(id);
	}
	
	/**
	 * Helper method for {@link #loadUserByUserId(String)} and {@link #loadUserByUsername(String)}
	 * @param username
	 * @return
	 */
	private User findUser(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * Create a new user. This function will encrypt the password before saving it.
	 * 
	 * @param user the User to save
	 * @return the saves User object
	 */
	@Override
	@Transactional
	public User createUser(User user) {
		// Get the password out, then update the password as a separate transaction. This is so
		// that we can keep all of the password management here.
		String password = user.getPassword();
		user.eraseCredentials();
		
		// Create the user first
		user = userRepository.save(user);
		// Then update the password
		user = updatePassword(user, password);
		password = null;
		return user;
	}

	/**
	 * Update a user's password and return the new version of the user.
	 * 
	 * @param user The User to save
	 * @return The saved User object
	 */
	@Override
	@Transactional
	public User changePassword(User user, String password) {
		user = updatePassword(user, password);
		password = null;
		return user;
	}

	
	/**
	 * Change the password for a user. 
	 * @param user The user to change the password for
	 * @param password The new password (not encoded)
	 * @return The updated User object (with the new version)
	 */
	private User updatePassword(User user, String password) {
		Assert.notNull(password, "Password cannot be empty");
		String encodedPassword = passwordEncoder.encode(password);
		password = null;
		userRepository.updatePassword(user, encodedPassword);
		encodedPassword = null;
		return userRepository.findById(user.getId());
	}
	
	
	/**
	 * Check to see if a user exists.
	 * @see UserService#userExists(String)
	 */
	@Override
	@Transactional
	public boolean userExists(String username) {
		return userRepository.userExists(username);
	}

	/**
	 * Get a role from the store
	 * @see UserService#getRole(Role)
	 */
	@Override
	@Transactional
	public RoleGrantedAuthority getRole(Role role) {
		return roleRepository.findByRole(role);
	}

	/**
	 * Save the user object back
	 * @see UserService#save(User)
	 */
	@Override
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}
}
