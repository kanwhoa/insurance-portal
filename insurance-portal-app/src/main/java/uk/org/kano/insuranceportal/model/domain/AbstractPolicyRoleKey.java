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
package uk.org.kano.insuranceportal.model.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Composite key class for the role
 * @author timh
 *
 */
class AbstractPolicyRoleKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private String personId;
	private String policyId;
	
	/**
	 * Default constructor
	 */
	public AbstractPolicyRoleKey() {
	}
	
	/**
	 * Known constructor
	 */
	public AbstractPolicyRoleKey(String personId, String policyId) {
		this.personId = personId;
		this.policyId = policyId;
	}
	
	/**
	 * Override the equals() test
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) { return false; }
		
		AbstractPolicyRoleKey rhs = (AbstractPolicyRoleKey)obj;
		return new EqualsBuilder()
				.append(personId, rhs.personId)
				.append(policyId, rhs.policyId)
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(974723, 6027)
			.append(personId)
			.append(policyId)
			.toHashCode();
	}
}
