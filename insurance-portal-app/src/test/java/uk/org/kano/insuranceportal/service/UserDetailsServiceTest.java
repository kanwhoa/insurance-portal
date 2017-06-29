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

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import uk.org.kano.insuranceportal.config.TestDataConfiguration;
import uk.org.kano.insuranceportal.config.app.ServiceConfiguration;
import uk.org.kano.insuranceportal.config.web.WebConfiguration;
import uk.org.kano.insuranceportal.test.BaseUnitTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestDataConfiguration.class, WebConfiguration.class, ServiceConfiguration.class})
@WebAppConfiguration
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
@Ignore
public class UserDetailsServiceTest extends BaseUnitTest {
	private Logger logger = LoggerFactory.getLogger(UserDetailsServiceTest.class);
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	public UserDetailsService userService;
	
	@Autowired
	public DataSource appDataSource;
	
	/**
	 * Before each test, refresh the database
	 * @throws SQLException 
	 * @throws IOException 
	 */
	@Before
	public void loadData() throws IOException, SQLException {
		/*TestUtility.runSqlFile(
				appDataSource.getConnection(),
				"/"+this.getClass().getName().replaceAll("\\.",  "/")+".sql"
			);*/
	}
	
	@Test
	public void basicTest() throws Exception {
		logger.info("Starting basic test");
		UserDetails user = null;
		
		// Get by ID
		//user = userService.loadUserByUsername("");
		//assertThat(user, notNullValue());
	}
}
