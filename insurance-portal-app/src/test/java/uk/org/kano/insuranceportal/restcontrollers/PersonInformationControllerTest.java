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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.org.kano.insuranceportal.config.app.ServiceConfiguration;
import uk.org.kano.insuranceportal.config.web.WebConfiguration;
import uk.org.kano.insuranceportal.test.BaseUnitTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebConfiguration.class, ServiceConfiguration.class})
@WebAppConfiguration
@Ignore
public class PersonInformationControllerTest extends BaseUnitTest {
	private Logger logger = LoggerFactory.getLogger(PersonInformationControllerTest.class);
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
	
	/**
	 * @throws NamingException 
	 * 
	 */
	@BeforeClass
	public static void setupClass() {
	}
	
	/**
	 * Setup the test
	 */
	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	/**
	 * Simple test of the controller
	 * @throws Exception
	 */
	@Test
	public void getMyInformationTest() throws Exception {
		logger.info("Getting own personal information");

		// call the front
		MvcResult result = mockMvc.perform(get("/services/people/me")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andReturn();
		
		logger.info(result.getResponse().getContentAsString());
	}
}
