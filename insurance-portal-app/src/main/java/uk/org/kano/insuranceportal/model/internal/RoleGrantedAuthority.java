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
package uk.org.kano.insuranceportal.model.internal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * A GrantedAuthority container for our roles. Almost exactly the same as SimpleGrantedAuthority
 * except that we add an ID for storage. It is also tied explicitly to the application defined roles.
 * 
 * TODO: DB init script to load roles. Look at Liquibase
 * 
 * @see org.springframework.security.core.authority.SimpleGrantedAuthority
 * 
 * @author timh
 *
 */
@Entity
@Table(name="ROLES")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Access(AccessType.FIELD)
public class RoleGrantedAuthority implements GrantedAuthority {

	/**
	 * Available roles
	 * @author timh
	 *
	 */
	public enum Role {
		USER("ROLE_USER"),
		AGENT("ROLE_AGENT"),
		SERVICE_STAFF("ROLE_SERVICE_STAFF");
		
		private String value;
		Role(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ID", nullable=false)
	private long id;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * Usual constructor.
	 * @param role The role this represents
	 */
	public RoleGrantedAuthority(Role role) {
		Assert.notNull(role, "The role must not be null");
		this.role = role;
	}

	/**
	 * Private constructor for JPA to instantiate the object
	 */
	@SuppressWarnings("unused")
	private RoleGrantedAuthority() {
	}

	
	@Override
	public String getAuthority() {
		return role.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) { return false; }
		
		RoleGrantedAuthority rhs = (RoleGrantedAuthority) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(role, rhs.role)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(235483, 24839)
			.append(role)
			.toHashCode();
	}

	public String toString() {
		return this.role.toString();
	}
}
