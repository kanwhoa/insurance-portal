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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import uk.org.kano.insuranceportal.framework.RestResponseAuthenticationEntryPoint;
import uk.org.kano.insuranceportal.framework.RestResponseLogoutSuccessHandler;
import uk.org.kano.insuranceportal.model.internal.Empty;

/**
 * Configure the spring security context.
 * TODO: logout not working
 *    
 * @author timh
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userService;
	
	@Autowired
	private List<HttpMessageConverter<?>> messageConverters;
	
	@Autowired
	private ServletContext servletContext;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
				.antMatchers("/static/**");
	}
	
	/**
	 * Configure the security for the application. Note this sets up the 401 response
	 * for the REST services.
	 * TODO: add something to record the last login time
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Use our special hander to return a REST response rather than a redirect
		RestResponseAuthenticationEntryPoint restAuthEntry = new RestResponseAuthenticationEntryPoint()
				.setHttpMessageConverters(messageConverters)
				.setObject(new Empty());
		
		// Our special handler to return the next URL (homepage) on success.
		URI nextUrl = null;
		try {
			nextUrl = new URI(servletContext.getContextPath()); // Send to the root.
		} catch (URISyntaxException e) {
			throw new RuntimeException("Context root appears bad: "+servletContext.getContextPath(), e);
		}

		RestResponseLogoutSuccessHandler restLogoutResponse = new RestResponseLogoutSuccessHandler()
				.setHttpMessageConverters(messageConverters)
				.setObject(nextUrl.normalize());
		
		// XSRF Setup - bad practice, but needed for PhoneGap's proxy
		CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
		cookieCsrfTokenRepository.setCookiePath("/");
		
		// CORS setup - FIXME, this allows all, it should be from this host only and only for services
		UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.applyPermitDefaultValues();
		// TODO: Test - not putting header on response
		corsConfiguration.addAllowedHeader("X-XSRF-TOKEN");
		corsConfigurationSource.registerCorsConfiguration("/services/**", corsConfiguration);
		corsConfigurationSource.registerCorsConfiguration("/login", corsConfiguration);
		
		// HTTP Security setup
		http
			.csrf() // Setup cookie based CRSF for Angular - FIXME
				//.csrfTokenRepository(cookieCsrfTokenRepository)
				//.and()
				.disable()
			.cors()
				.configurationSource(corsConfigurationSource)
				.and()
			.authorizeRequests() // which URLs do not need to be authorised.
				.antMatchers(
						"/",
						"/static/**",
						"/auth/**",
						"/login",
						"/services/logout",
						"/signup",
						"/signup/**",
						"/contactus",
						"/services/contactus",
						"/user/register",
						"/services/user/register",
						// Temp rules
						"/services/product/calculator/**"
					).permitAll()
				.anyRequest().hasRole("USER")
				.and()
			.formLogin() // Form login for non-social
				.loginPage("/login")
				.loginProcessingUrl("/login/authenticate")
				.failureUrl("/login?error=bad_credentials")
				.permitAll()
				.and()
			.logout()
				// What to do on logout, and where the logout page is. As CSRF is active, this only supports POST.
				// This leads to a strange situation. Since our backend is on a different host, we cannot extract
				// the cookie. Therefore, we need to use Angular to do the CSRF compliant POST, to a web service
				// and let it handle the redirect URL. Note that the logoutSuccessUrl does nothing here, infact we
				// disable it so the service call returns 200.
				.logoutUrl("/services/logout")
				.logoutSuccessHandler(restLogoutResponse)
				.invalidateHttpSession(true)
				.and()
			.exceptionHandling() // Make sure services respond with a 401, not a redirect
				.defaultAuthenticationEntryPointFor(restAuthEntry, new AntPathRequestMatcher("/services/**"))
				.and()
			.apply(new SpringSocialConfigurer());		
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			//.inMemoryAuthentication()
			//.withUser("user").password("password").roles("USER");
			.userDetailsService(userService)
			.passwordEncoder(passwordEncoder);
	}
}
