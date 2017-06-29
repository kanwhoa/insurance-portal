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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Class to denote a policy holder
 * @author timh
 *
 */
@Entity
@DiscriminatorValue("PH")
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "PolicyHolder", propOrder = {})
public class PolicyHolder extends AbstractPolicyRole {
	@XmlTransient
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor for JPA
	 */
	@SuppressWarnings("unused")
	private PolicyHolder() {
		super();
	}
	
	/**
	 * Constructor to establish the relationship
	 * @param person
	 * @param policy
	 */
	public PolicyHolder(Person person, Policy policy) {
		super(person, policy);
	}

}
