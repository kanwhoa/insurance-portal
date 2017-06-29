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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

/**
 * Model of a user.
 * 
 * Some key design decisions<br>
 * <o1>
 * <li>The email is not unique. This could allow a person with two different social accounts bound to the same email, i.e. one day signing in with Facebook and the next Google.
 *     However, there are a couple of more valid business reasons. The first is a family that shares the same email address. Recently this has become less likely as Gmail
 *     addresses are free, however, it still occurs. The second is that someone has two personas. I.e. their public/family account and their private account for their alter ego.
 *     Surprisingly this happens a lot, often the two personas may have different physical addresses, but the same electronic address/phone. Therefore, we will do the following.
 *     Firstly the same social account can be bound to multiple Enterprise identities, which helps lower fraud and identify places where there is duplication. We can set up some
 *     alert triggers on this for investigation. Secondly, we will disallow multiple social IDs binding to the same Enterprise ID. This prevents using multiple social providers
 *     but cuts down on a lot of opportunities where an attacker may be trying to steal an account.</li>
 * <li>The username must be unique, obviously</li>
 * <li>After registration, the user is automatically logged in. However, they cannot bind to an Enterprise ID
 *     until they have verified their email address. This is done to allow ease of processing. For example, in an ecommerce setup
 *      we may want to keep the user on the user journey of purchase without intervention. An intervention to validate the email address
 *      will cause a marked drop in the conversion pipeline (suspect, use A/B testing to prove). In any case, the functions available to a
 *      non-bound user are still minimal. Some business environments may want to restrict this to avoid competitors getting access to the
 *      post-login environment and having a look around and therefore losing the IP/competitive advantage. However, breaking news,
 *      all your competitor needs to do is buy your product to get in.</li>
 * <li>We will create a default constructor that does nothing, this is only to keep JPA happy. Just to make sure no-one else uses it,
 *      it will be private</li>
 * </ol>
 * 
 * TODO: last login - make it work
 * TODO: version not being updated on save
 * 
 * @author timh
 *
 */
@Entity
@Table(name="USERS")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "userInformation", propOrder = {
		"username",
		"name",
		"email",
		"emailConfirmed",
		"lastLogin"
})
public class User extends SocialUser {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UserSequence")
	@SequenceGenerator(name="UserSequence", initialValue=100000, allocationSize=37, sequenceName="seq_USERS")
	@XmlTransient
	private long id;
	
	@Version
	@Column(nullable=false)
	@XmlTransient
    private long version;
	
	@Column(nullable=false)
	@XmlElement
	private String name;

	@Column(nullable=false)
	@XmlElement
	private String email;
	
	@Column(nullable=false)
	@XmlElement
	private boolean emailConfirmed;
	
	@Column(name="created", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	private Date createdTime;

	@Column(name="modified", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	private Date modifiedTime;

	@Column(name="passwordChanged", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	private Date passwordChangedTime;

	@Column(name="lastLogin", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	private Date lastLoginTime;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
			name="USERSPERSONIDS",
			joinColumns=@JoinColumn(name="USERID")
	  )
	@Column(name="personId")
	@XmlElement
	private Set<String> personIds = new HashSet<>();

	
	/**
	 * Constructor
	 * @param username
	 * @param password
	 * @param authorities
	 */
	public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		emailConfirmed = false;
    }
	
	/**
	 * Default constructor to keep JPA happy. When JPA uses it, it will set the state of the object
	 * through reflection so no need to worry. Therefore, we will set the internal state to dummy values.
	 */
	private User() {
		super("x", "x", new HashSet<GrantedAuthority>());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}

	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}
	
	public void addPersonId(String personId) {
		personIds.add(personId);
	}
	
	public Set<String> getPersonIds() {
		return Collections.unmodifiableSet(personIds);
	}

	// Metadata fields
	public long getVersion() {
		return version;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public Date getPasswordChangedTime() {
		return passwordChangedTime;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	@PrePersist
	public void prePersist() {
		Date now = new Date();
		this.createdTime = now;
		this.modifiedTime = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedTime = new Date();
	}

}
