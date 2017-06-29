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
package uk.org.kano.insuranceportal.service;

import java.util.Calendar;

import uk.org.kano.insuranceportal.model.domain.ProductIllustration;
import uk.org.kano.insuranceportal.model.internal.ProductCalculationMode;

/**
 * Interface to the KIE/Drools instance
 * 
 * @author timh
 *
 */
public interface ProductService {
	/**
	 * Run an object against the product ruleset
	 * 
	 * @param in
	 * @return
	 */
	public ProductIllustration runProductRules(ProductIllustration in, ProductCalculationMode mode);
	
	/**
	 * This is to override the base calendar time/date from where all date differences are calculated
	 * @param now
	 */
	public void setBaseCalendar(Calendar now);
}
