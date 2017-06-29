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

import uk.org.kano.insuranceportal.model.internal.RoleGrantedAuthority;

/**
 * This needs to exist to pull back the existing roles from the database. If we just create new role
 * objects, then when we persist the User object, it tries to create new roles, but this fails because
 * we have intentionally turned off cascade. We could get around that by creating new roles for each new
 * user, but that would be a big waste of space.
 * 
 * @author timh
 *
 */
public interface RoleGrantedAuthorityRepository extends JpaRepository<RoleGrantedAuthority, Long> {
	/**
	 * Get a role from the set of existing roles
	 * @param role
	 * @return
	 */
	public RoleGrantedAuthority findByRole(RoleGrantedAuthority.Role role);
}
