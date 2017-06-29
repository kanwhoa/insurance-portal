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
package uk.org.kano.insuranceportal.config.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;

@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {
	
	@Autowired
	private DataSource appDataSource;
	
	/**
	 * These are the social connections we will use.
	 */
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
		FacebookConnectionFactory facebookConnectionFactory = new FacebookConnectionFactory(
				environment.getRequiredProperty("oauth.facebook.appid"),
				environment.getRequiredProperty("oauth.facebook.appsecret")
		);
		facebookConnectionFactory.setScope(environment.getProperty("oauth.facebook.scopes", (String)null));
		connectionFactoryConfigurer.addConnectionFactory(facebookConnectionFactory);
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	/**
	 * This stores the connection between a social user and a local user details.
	 * TODO: Convert this from JDBC to JPA
	 * FIXME - strong crypto on the encryptor
	 * 
	 * @see SocialUserDetailsService
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new JdbcUsersConnectionRepository(
				appDataSource,
				connectionFactoryLocator,
				Encryptors.noOpText()
		);
	}
	
	/**
	 * The controller that handles the /auth/** connections.
	 * 
	 * @param connectionFactoryLocator
	 * @param connectionRepository
	 * @return
	 */
	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}	
}
