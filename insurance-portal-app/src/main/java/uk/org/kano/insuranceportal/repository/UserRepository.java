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
package uk.org.kano.insuranceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority;
import uk.org.kano.insuranceportal.model.internal.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findById(long id);
	public User findByUsername(String username);

	/**
	 * Update the password. BIG assumption here is that this is called with the latest object
	 * since we are going to use it to update the version.
	 * 
	 * @param id
	 * @param password
	 */
	@Modifying
	@Query("UPDATE User u SET u.password = :#{#password}, u.passwordChangedTime = CURRENT_TIMESTAMP, u.modifiedTime = CURRENT_TIMESTAMP, u.version = u.version + 1 WHERE u.id = :#{#user.id} and u.version = :#{#user.version}")
	public void updatePassword(@Param("user") User user, @Param("password") String password);

	/**
	 * Login the user. This just updates the last login time. This has a distinct tie to database held credentials
	 * as with LDAP, this would be done automatically.
	 *  
	 * @param id
	 */
	@Modifying
	@Query("UPDATE User u SET u.lastLoginTime = CURRENT_TIMESTAMP, u.version = u.version + 1 WHERE u.id = :#{#user.id} and u.version = :#{#user.version}")
	public void recordLogin(@Param("user") User user);	
	
	/**
	 * Check to see if a username exists. Convienence method returning boolean directly to avoid
	 * unmarshalling the object and pulling back the data from the database.
	 * 
	 * @param username The username to check
	 * @return True if the user exists, false otherwise.
	 */
	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :#{#username}")
	public boolean userExists(@Param("username") String username);
}
