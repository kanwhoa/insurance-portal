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
import uk.org.kano.insuranceportal.model.domain.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
	public Person findByPersonId(String id);
	public Set<Person> findByPersonIdIn(Set<String> ids);

	/**
	 * Find a set of people by the role they play on a policy
	 * @param policyId
	 * @return
	 */
	@Query("SELECT p FROM Person p INNER JOIN p.rolesOnPolicies r WHERE r.policyId = :#{#policyId} AND TYPE(r) = :#{#role}")
	public Set<Person> findByPolicyRole(@Param("policyId") String policyId, @Param("role") Class<? extends AbstractPolicyRole> role);
}
