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
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A dummy domain model to hold the product calculation data. In reality will use ACORD
 * @author timh
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "productType", propOrder = {
})
public class ProductIllustration implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlID
	@XmlAttribute
	private String id;
	
	@XmlElement
	private String product;
	
	@XmlElement
	private String gender;
	
	@XmlElement
	private Boolean smoker;
	
	@XmlElement
	@XmlSchemaType(name="date")
	private XMLGregorianCalendar dateOfBirth;
	
	@XmlElement
	private Integer insuredAge;

	@XmlElement
	private BigDecimal sumAssured;
	
	@XmlTransient
	private BigDecimal rate;

	@XmlElement
	private BigDecimal basePremium;

	@XmlElement
	private BigDecimal annualPremium;
	
	@XmlElement
	private BigDecimal firstPremium;
	
	@XmlElement
	private String paymentMode;

	// Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Boolean getSmoker() {
		return smoker;
	}

	public void setSmoker(Boolean smoker) {
		this.smoker = smoker;
	}

	public XMLGregorianCalendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(XMLGregorianCalendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Integer getInsuredAge() {
		return insuredAge;
	}

	public void setInsuredAge(Integer insuredAge) {
		this.insuredAge = insuredAge;
	}

	public BigDecimal getSumAssured() {
		return sumAssured;
	}

	public void setSumAssured(BigDecimal sumAssured) {
		this.sumAssured = sumAssured;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getAnnualPremium() {
		return annualPremium;
	}

	public void setAnnualPremium(BigDecimal annualPremium) {
		this.annualPremium = annualPremium;
	}

	public BigDecimal getFirstPremium() {
		return firstPremium;
	}

	public void setFirstPremium(BigDecimal firstPremium) {
		this.firstPremium = firstPremium;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public BigDecimal getBasePremium() {
		return basePremium;
	}

	public void setBasePremium(BigDecimal basePremium) {
		this.basePremium = basePremium;
	}	
}
