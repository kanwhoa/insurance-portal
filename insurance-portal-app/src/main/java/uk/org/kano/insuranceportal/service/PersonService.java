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

import java.util.Set;

import uk.org.kano.insuranceportal.model.domain.AbstractPolicyRole;
import uk.org.kano.insuranceportal.model.domain.Person;

/**
 * Interface for retrieving Person objects from the JPA store. Note that this could be backed by a
 * web service in a real implementation
 * 
 * @author timh
 *
 */
public interface PersonService {
	/**
	 * Find by the enterprise ID
	 * @param id
	 * @return
	 */
	public Person findPerson(String id);
	/**
	 * Find by multiple enterprise IDs
	 * @param id
	 * @return
	 */
	public Set<Person> findPerson(Set<String> ids);
	
	/**
	 * Find people who have a specific role on a policy
	 * @param policyId
	 * @param role
	 * @return
	 */
	public Set<Person> getPeopleFromPolicyRole(String policyId, Class<? extends AbstractPolicyRole> role);
}
