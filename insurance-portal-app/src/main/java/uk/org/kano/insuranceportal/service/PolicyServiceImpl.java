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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import uk.org.kano.insuranceportal.model.domain.AbstractPolicyRole;
import uk.org.kano.insuranceportal.model.domain.Policy;
import uk.org.kano.insuranceportal.repository.PolicyRepository;

/**
 * A default {@link PersonService}.
 *  
 * @author timh
 *
 */
@Transactional
@Service
public class PolicyServiceImpl implements PolicyService {
	@Autowired
	private PolicyRepository policyRepository;
	
	@Override
	public Policy findPolicy(String id) {
		Assert.notNull(id, "Invalid policy ID");
		return policyRepository.findByPolicyId(id);
	}

	@Override
	public Set<Policy> findPolicyByPerson(String id) {
		Assert.notNull(id, "Invalid person ID");
		return policyRepository.findByPersonId(id);
	}

	@Override
	public Set<Policy> findPolicyByPersonRole(String id, Class<? extends AbstractPolicyRole> role) {
		Assert.notNull(id, "Invalid person ID");
		Assert.notNull(role, "Invalid role");
		return policyRepository.findByPersonIdAndRole(id, role);
	}

	@Override
	public Set<Policy> findPolicyByPersons(Set<String> ids) {
		Assert.notNull(ids, "Invalid person ID");
		Assert.notEmpty(ids, "Invalid person ID");
		return policyRepository.findByPersonIds(ids);
	}

	@Override
	public Set<Policy> findPolicyByPersonsRole(Set<String> ids, Class<? extends AbstractPolicyRole> role) {
		Assert.notNull(ids, "Invalid person ID");
		Assert.notNull(ids, "Invalid person ID");
		Assert.notNull(role, "Invalid role");
		return policyRepository.findByPersonIdsAndRole(ids, role);
	}
}
