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

import java.io.Serializable;

import javax.mail.internet.AddressException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import uk.org.kano.insuranceportal.model.internal.validators.Password;
import uk.org.kano.insuranceportal.model.internal.validators.UserNotExists;
import uk.org.kano.insuranceportal.model.internal.validators.ValidPassword;
import uk.org.kano.insuranceportal.utility.SanitisationUtilities;

/**
 * A class for registering a user. This is basically a Person object but allows the user to
 * add a user ID an password.
 * 
 * This only collects very minimal details to create a "shell account". When a user establishes
 * themselves in the enterprise context, the links through to the person will become apparent.
 * 
 * @author timh
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "userRegistrationType", propOrder = {
		"isSocialLogin",
		"username",
		"name",
		"password1",
		"password2",
		"email"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@ValidPassword(groups={Password.class}) // Don't do this as part of the default check as it may be a social account
public class UserRegistration implements PasswordManager, Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private boolean isSocialLogin;
	
	@XmlElement
	@NotNull
	@Size(min=5, max=256)
	@UserNotExists
	private String username;

	@XmlElement
	@NotNull
	@Size(min=5, max=100)
	private String name;

	// No need to supply @NotNull here, the @ValidPassword will handle it first
	@XmlElement
	private String password1;
	
	@XmlElement
	private String password2;

	@XmlElement
	@NotNull
	// See http://emailregex.com/
	@Pattern(regexp="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", message="{validation.email.invalid}")
	private String email;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getEmail() {
		return email;
	}
	
	/**
	 * This will sanitise the email on calling.
	 * 
	 * @param email The email address
	 * @throws IllegalArgumentException If the email is not valid/cannot be parsed.
	 */
	public void setEmail(String email) {
		try {
			this.email = SanitisationUtilities.sanitiseEmailAddress(email);
		} catch (AddressException e) {
			throw new IllegalArgumentException("Invalid email address", e);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSocialLogin() {
		return isSocialLogin;
	}

	public void setSocialLogin(boolean isSocialLogin) {
		this.isSocialLogin = isSocialLogin;
	}
}
