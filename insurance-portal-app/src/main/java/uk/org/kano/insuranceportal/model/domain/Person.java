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
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.Assert;

/**
 * This is a sample "Enterprise" object that would be retrieved from a data warehouse, source system
 * or maybe a direct JDBC connection. This would often come back over a web services connection via an
 * ESB. The key difference on this type of object is that there is a System ID, which represents the
 * key in the store, however, there is also an "Business ID" (or "Business Key"), this would represent they object in the
 * source system. For example, in a typical financial organisation, there could be many source systems,
 * typical examples being (for insurance) the policy administration system, the claim system, the CRM etc.
 * <br>
 * Each source system usually has their own business key which you cannot change. Most organisations then build a
 * cross-reference centrally which holds each of the source system keys and their enterprise equivalent. The "Enterprise
 * Key" then becomes the master to find the individual in all systems. Typically, CRM systems are aware
 * of the source system business key as they need to interface with multiple systems as well. However, in most cases,
 * the source system is only aware of their own key.
 * <br>
 * For this object, we will be dealing with the Enterprise key, which has also been supplemented with some
 * basic personal information. If we want to change/update the data, the ESB will translate that key to the
 * source system keys and perform the synchronised transaction to update where necessary.
 * <br>
 * The system key is just so JPA (in our implementation) can keep track of the object. The role and policy objects
 * will take the lead from the person class - i.e. customer focussed.
 * 
 * @author timh
 *
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "personType", propOrder = {
		"givenName",
		"familyName"
})
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@XmlTransient
	private long id;
	
	@Basic
	@Column(nullable=false, length=16)
	@XmlAttribute
	private String personId;
	
	@Basic
	@Column(nullable=false)
	@XmlElement
	private String givenName;

	@Basic
	@Column(nullable=false)
	@XmlElement
	private String familyName;
	
	@Basic
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	@XmlTransient
	private Date dateOfBirth;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="person")
	@XmlElement
	private Set<AbstractPolicyRole> rolesOnPolicies;
	
	// Getters and Setters
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}
	
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String firstName) {
		this.givenName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String lastName) {
		this.familyName = lastName;
	}
	
	public Set<AbstractPolicyRole> getRolesOnPolicies() {
		return Collections.unmodifiableSet(rolesOnPolicies);
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	/**
	 * Determine if this user has a specified role on a policy
	 * 
	 * @param role The class of the role we are looking for
	 * @param policyId The policy which it should be on
	 * @return true if this Person has the specified role, false otherwise.
	 */
	public boolean hasRoleOnPolicy(Class<AbstractPolicyRole> role, String policyId) {
		Assert.notNull(role, "Role type must not be null");
		Assert.notNull(policyId, "Policy Id must not be null");
		for (AbstractPolicyRole r: rolesOnPolicies) {
			if (role == r.getClass() && policyId.equals(r.getPolicyId())) return true;
		}
		return false;
	}
}
