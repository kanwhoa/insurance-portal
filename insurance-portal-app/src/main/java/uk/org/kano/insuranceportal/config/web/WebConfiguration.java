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
package uk.org.kano.insuranceportal.config.web;

import java.util.List;
import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Web configuration class
 * https://www.petrikainulainen.net/programming/spring-framework/adding-social-sign-in-to-a-spring-mvc-web-application-configuration/
 * 
 * @author timh
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan(
		basePackages = {
				"uk.org.kano.insuranceportal.controllers",
				"uk.org.kano.insuranceportal.restcontrollers"
		}
	)
public class WebConfiguration extends WebMvcConfigurerAdapter {
	//private Logger logger = LoggerFactory.getLogger(WebConfiguration.class);
	
	/**
	 * Allow spring to work as the default servlet.
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	/**
	 * Add some default views/controllers that have no logic
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// User related
		registry.addViewController("/login").setViewName("support/login");
		registry.addViewController("/contactus").setViewName("support/contactus");
		registry.addRedirectViewController("/signup", "/user/register");
		registry.addViewController("/user/register").setViewName("user/register");
		registry.addViewController("/user/bind").setViewName("user/bind");
		
		// Main app
		registry.addViewController("/").setViewName("index");
	}
	
	/**
	 * Set the resource handlers for static resources.
	 * TODO: make the cache period longer
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
			.addResourceLocations("/WEB-INF/static/")
			.setCachePeriod(60);
	}
	
	/**
	 * Potentially add an interceptor to change locales on ?lang-xx query strings
	 * This is disabled for the moment as it has problems with web services (see overview doc).
	 * In preference, we have enabled the AcceptHeaderLocaleResolver bean to enable locale changes.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//registry.addInterceptor(new LocaleChangeInterceptor());
	}
	
	
	@Autowired
	private List<HttpMessageConverter<?>> messageConverters;
	/**
	 * Load the message converters from the pre-configured bean.
	 * 
	 * @see uk.org.kano.insuranceportal.config.app.ServiceConfiguration#messageConverters()
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
		httpMessageConverters.clear();
		httpMessageConverters.addAll(messageConverters);
	}
	
	/**
	 * Create the ability to extract the Accept-Language header and setup the locale for the request.
	 * If there is no header, the locale will be the platform default. If no message pack for that, it
	 * will resort the the message file default.
	 * 
	 * TODO: confirm this is working in the validators.
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver ret = new AcceptHeaderLocaleResolver();
		return ret;
	}
	
	/**
	 * Configure the error mapping. All unhandled errors will be handled by the general error page.
	 * Specific errors can be configures otherwise.
	 * @return
	 */
	@Bean
	public SimpleMappingExceptionResolver exceptionResolver() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

		Properties exceptionMappings = new Properties();
		exceptionMappings.put("java.lang.Exception", "errors/general");
		exceptionMappings.put("java.lang.RuntimeException", "errors/general");
		exceptionResolver.setExceptionMappings(exceptionMappings);

		Properties statusCodes = new Properties();
		statusCodes.put("errors/404", "404");
		statusCodes.put("errors/general", "500");
		exceptionResolver.setStatusCodes(statusCodes);

		return exceptionResolver;
	}

	/**
	 * Configure the view resolver to look for /WEB-ING/jsp/**.jspx.
	 * @return
	 */
	@Bean
	public ViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jspx");
		return viewResolver;
	}
	
	/**
	 * Add CORS handlers to the REST controllers.
	 * TODO: pick up the correct domain for CORS from the configuration file
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/services/**")
			.allowedMethods("GET", "POST")
			.allowCredentials(true)
			.maxAge(3600);
		// TODO: allowed origins, cannot be * with authentication
	}
	
	@Autowired
	private LocalValidatorFactoryBean validator;
	
	/**
	 * Use our pre-configured bean for validation, this is required because we have overriden the
	 * message source for the validation messages.
	 */
	@Override
	public Validator getValidator() {
		return validator;
	}
		
	/**
	 * Setup the REST handlers to use Apache HttpComponents. This allows pooling and pipelining.
	 */
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
	
	/**
	 * Use Apache HttpComponents in preference to the Java native IO. This allows pooling and pipelining.
	 * @param httpClient
	 * @return
	 */
	@Bean
	public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}
	
	/**
	 * Use Apache HttpComponents in preference to the Java native IO. This allows pooling and pipelining.
	 * @return
	 */
	@Bean
	public HttpClient httpClient() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		return HttpClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setMaxConnTotal(DEFAULT_MAX_TOTAL_CONNECTIONS)
				.setMaxConnPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE)
				.build();
	}
}
