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
package uk.org.kano.insuranceportal.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableJpaRepositories(
		basePackages = {
				"uk.org.kano.insuranceportal.repository"
		}
)
@EnableTransactionManagement
public class TestDataConfiguration implements TransactionManagementConfigurer {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Properties ormProperties;
	
	@Bean
	public InitialContextFactoryBuilder jndiBuilder() {
		SimpleNamingContextBuilder builder;
		try {
			builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
		} catch (NamingException e) {
			throw new BeanCreationException("Unable to setup JNDI", e);
		}
		return builder;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);
		
		// Override Liquibase
		logger.info("Overriding LiquiBase and forcing create-drop");
		ormProperties.remove("hibernate.hbm2ddl.auto");
		ormProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("uk.org.kano.insuranceportal.model");
		factory.setDataSource(appDataSource());
		factory.setJpaProperties(ormProperties);
		factory.setMappingResources("META-INF/orm.xml");
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean
	public DataSource appDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
		ds.setUsername("sa");
		ds.setPassword("sa");
		return ds;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		logger.info("Creating transaction manager");
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
	
}
