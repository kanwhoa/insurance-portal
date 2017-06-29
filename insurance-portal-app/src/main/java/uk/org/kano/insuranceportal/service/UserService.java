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

import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority;
import uk.org.kano.insuranceportal.model.internal.User;

/**
 * An interface for managing the User objects. This is supplemental to the standard
 * Spring interfaces.
 * 
 * @author timh
 *
 */
public interface UserService {
	
	/**
	 * Get an updatable version of the object
	 * 
	 * @param id The id of the user
	 * @return An updatable User object
	 */
	public User findUserById(long id);
	
	/**
	 * Create a new user
	 * @param user The User object to create 
	 * @return The saved & updated user object
	 */
	public User createUser(User user);
	
	/**
	 * Change a user's password
	 * @param user The user to modify
	 * @param password The new (non-null) password
	 * @return The saved & updated user object
	 */
	public User changePassword(User user, String password);
	
	/**
	 * Check to see if a user exists. Performance method to avoice unmarshalling the whole object
	 * and checking for null.
	 * 
	 * @param username The username to check for
	 * @return true if the user exists
	 */
	public boolean userExists(String username);
	
	/**
	 * Get a persisted role. This avoids "unsaved" warnings
	 * 
	 * @param role The role to return
	 * @return The Role as a GrantedAuthority
	 */
	public RoleGrantedAuthority getRole(RoleGrantedAuthority.Role role);
	
	/**
	 * Save updates to a user
	 * 
	 * @param user The user to update
	 * @return The saves copy of the user
	 */
	public User save(User user);
}
