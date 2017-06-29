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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * Class to define what type of role a person has on a policy. This could be a simple string
 * type, but we've made it into a class hierarchy so that we can have specific operations on the
 * class return different levels of access. I.e. we can define what a person can or cannot on the policy
 * object. E.g. a person may be able to increase the policy value, or not. Obviously no role means no
 * access.<br>
 * This means we can centralise all of the policy security controls in one place and use Spring Security's
 * evaluator to determine whether the operation should proceed.
 * <br>
 * Note that we have created one-to-one mapping from here to the policy. This means that the person 
 * would need multiple subclass object, for example, have two {@link PolicyHolder} objects, one for
 * each policy they play the role of policy holder on. We could have creates a one-to-many mapping, but
 * from a business sense, this makes things more explicit.
 * 
 * @author timh
 *
 */
@Entity
@Table(name="PersonPolicyRole")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="roleType", discriminatorType=DiscriminatorType.STRING, length=2)
@Access(AccessType.FIELD)
@IdClass(AbstractPolicyRoleKey.class)
@PrimaryKeyJoinColumns({
	@PrimaryKeyJoinColumn(name="personId", referencedColumnName="personId"),
	@PrimaryKeyJoinColumn(name="policyId", referencedColumnName="policyId")
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "policyRoleType", propOrder = {
		"contractId"
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "role")
public abstract class AbstractPolicyRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="personId", nullable=false, length=16)
	@XmlTransient
	private String personId;
	
	@Id
	@Column(name="policyId", nullable=false, length=16)
	@XmlAttribute
	private String policyId;
	
	// reverse mappings - here will will use the PrimaryKeyJoinColumn so we can access the ID without
	// accessing the complete object
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="personId", referencedColumnName="personId", insertable=false, updatable=false)
	@XmlTransient
	private Person person;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="policyId", referencedColumnName="policyId", insertable=false, updatable=false)
	@XmlTransient
	private Policy policy;
	
	/**
	 * Private default constructor for JPA
	 */
	protected AbstractPolicyRole() {
	}
	
	/**
	 * Public constructor for adding a role
	 * @param person
	 * @param policy
	 */
	public AbstractPolicyRole(Person person, Policy policy) {
		Assert.notNull(person, "A person owning the relationship must be specified");
		Assert.notNull(policy, "A policy the role is effective on must be specified");
		this.personId = person.getPersonId();
		this.person = person;
		this.policyId = policy.getPolicyId();
		this.policy = policy;
	}
	
	// Getters
	public String getPersonId() {
		return personId;
	}

	public String getPolicyId() {
		return policyId;
	}

	public Person getPerson() {
		return person;
	}

	public Policy getPolicy() {
		return policy;
	}
}
