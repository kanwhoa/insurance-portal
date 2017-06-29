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

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.org.kano.insuranceportal.model.domain.AbstractPolicyRole;
import uk.org.kano.insuranceportal.model.domain.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
	public Policy findByPolicyId(String id);
	
	/**
	 * Find a set of policies from the person who plays a role on them
	 * @param id
	 * @return
	 */
	@Query("SELECT p FROM Policy p INNER JOIN p.roles r WHERE r.personId = :#{#personId}")
	public Set<Policy> findByPersonId(@Param("personId") String id);

	/**
	 * Find a set of policies from the person who plays a role on them with a specified role
	 * @param id
	 * @return
	 */
	@Query("SELECT p FROM Policy p INNER JOIN p.roles r WHERE r.personId = :#{#personId} AND TYPE(r) = :#{#role}")
	public Set<Policy> findByPersonIdAndRole(@Param("personId") String id, @Param("role") Class<? extends AbstractPolicyRole> role);

	
	/**
	 * Find a set of policies from the person who plays a role on them (multiple personIds)
	 * 
	 * @param id
	 * @return
	 */
	@Query("SELECT p FROM Policy p INNER JOIN p.roles r WHERE r.personId IN :#{#personId}")
	public Set<Policy> findByPersonIds(@Param("personId") Set<String> id);

	/**
	 * Find a set of policies from the person who plays a role on them (multiple personIds)
	 * with a specified role.
	 * 
	 * @param id
	 * @return
	 */
	@Query("SELECT p FROM Policy p INNER JOIN p.roles r WHERE r.personId IN :#{#personId} AND TYPE(r) = :#{#role}")
	public Set<Policy> findByPersonIdsAndRole(@Param("personId") Set<String> id, @Param("role") Class<? extends AbstractPolicyRole> role);
}
