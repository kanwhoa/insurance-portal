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
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import uk.org.kano.insuranceportal.model.internal.validators.UserNotExists;

/**
 * A class for handling communication to the view for binding a User to a Person.
 * 
 * @author timh
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "userBindType", propOrder = {
		"isSocialLogin",
		"username",
		"name",
		"password1",
		"password2",
		"email"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBind implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	@NotNull
	@Size(max=255)
	@UserNotExists
	private String givenName;

	@XmlElement
	@NotNull
	@Size(max=255)
	private String familyName;

	@XmlElement
	@NotNull
	private Date dateOfBirth;
	
	@XmlElement
	@NotNull
	@Pattern(regexp="[A-Z][0-9]{6}")
	private String policyId;

	@XmlElement
	@NotNull
	private Date policyStartDate;

	// Getters and setters
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public Date getPolicyStartDate() {
		return policyStartDate;
	}

	public void setPolicyStartDate(Date policyStartDate) {
		this.policyStartDate = policyStartDate;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

}
