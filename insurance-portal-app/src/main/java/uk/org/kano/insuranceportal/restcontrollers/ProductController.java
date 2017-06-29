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
package uk.org.kano.insuranceportal.restcontrollers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.org.kano.insuranceportal.model.domain.ProductIllustration;
import uk.org.kano.insuranceportal.model.internal.ProductCalculationMode;
import uk.org.kano.insuranceportal.service.ProductService;

/**
 * Product calculator service
 * 
 * @author timh
 *
 */
@RestController
@RequestMapping("/services")
public class ProductController {
	private Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@Autowired
	private ProductService productService;
		
	/**
	 * Run the product calculators on an object. We don't care what the product is, all products
	 * are loaded into the same memory space. That may change if it gets really complicated.
	 * 
	 */
	@RequestMapping(value="/product/calculator/sa", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ProductIllustration> doProductCalculationBySumAssured(@RequestBody ProductIllustration p) {
		logger.debug("Calling product calculator");
		
		productService.runProductRules(p, ProductCalculationMode.BY_SUM_ASSURED);
		
		return new ResponseEntity<>(p, HttpStatus.OK);
	}
}

