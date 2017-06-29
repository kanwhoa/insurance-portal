/**
 * Copyright 2017 Tim Hurman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.kano.insuranceportal.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.org.kano.insuranceportal.model.domain.ProductIllustration;
import uk.org.kano.insuranceportal.model.internal.ProductCalculationMode;
import uk.org.kano.insuranceportal.test.AppBaseUnitTest;

public class ProductServiceTest extends AppBaseUnitTest {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private DatatypeFactory dataTypeFactory;

	/**
	 * FIXME: as time goes on these tests will fail because the comparison is against now.
	 */
	@Test
	public void happyCase() {
		GregorianCalendar baseDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		baseDate.set(2010, 1, 1);
		
		ProductIllustration pi = new ProductIllustration();
		pi.setId("test1");
		pi.setProduct("Product1");
		pi.setSumAssured(BigDecimal.valueOf(200000L));
		pi.setGender("male");
		pi.setDateOfBirth(dataTypeFactory.newXMLGregorianCalendar("1980-01-01Z"));
		pi.setSmoker(Boolean.FALSE);
		pi.setPaymentMode("Monthly");
		logObject("Before calling: ", pi);
		
		productService.setBaseCalendar(baseDate);
		pi = productService.runProductRules(pi, ProductCalculationMode.BY_SUM_ASSURED);

		logObject("After calling: ", pi);
		assertThat("Invalid insured age", pi.getInsuredAge(), equalTo(30));
		assertThat("Invalid rate", pi.getRate(), equalTo(new BigDecimal("0.49")));
	}
}
