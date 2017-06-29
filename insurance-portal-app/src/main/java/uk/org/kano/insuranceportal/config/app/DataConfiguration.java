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

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;

import liquibase.integration.spring.SpringLiquibase;
import uk.org.kano.insuranceportal.utility.StartupUtilities;

@Configuration
@EnableJpaRepositories(
		basePackages = {
				"uk.org.kano.insuranceportal.repository"
		}
)
@EnableTransactionManagement
public class DataConfiguration implements TransactionManagementConfigurer {
	private Logger logger = LoggerFactory.getLogger(DataConfiguration.class);

	@Autowired
	private Properties ormProperties;
	
	@Autowired
	private Environment environment;
	
	/**
	 * DataSource for the JPA setup.
	 * @return
	 */
	@Bean
	public DataSource appDataSource() {
		String dataSourceLocation = ormProperties.getProperty("datasource.location");
		if (null == dataSourceLocation) {
			throw new BeanCreationException("No datasource JNDI location specified");
		}
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource(dataSourceLocation);
	}
	
	/**
	 * Automatically generate or update the database configuration. Check dbchangelog.xml in
	 * the package root to see the database schema.
	 * 
	 * @param appDataSource
	 * @return The Liquibase bean
	 */
	@Bean
	public SpringLiquibase liquibase(DataSource appDataSource) {
		if (!Boolean.parseBoolean(environment.getProperty("liquibase.enabled", "true"))) {
			return null;
		}
		SpringLiquibase liquibase = new SpringLiquibase();

		liquibase.setContexts(StringUtils.join(environment.getActiveProfiles(), ", "));
		liquibase.setDataSource(appDataSource);
		liquibase.setChangeLog("classpath:"+StartupUtilities.getPackageRoot()+"/dbchangelog.xml");
		return liquibase;
	}
	
	/**
	 * This is required as a JTA data source would normally only give us the EntityManager,
	 * However, JPA data repositories needs the EntityManagerFactor, so we need to build it
	 * ourselves. Explicit dependency on Liquibase to make sure the tables are created first
	 * and then the EntityManager can validate.
	 *  
	 * @return The JTA EntityManagerFactory
	 */
	@Bean
	@DependsOn("liquibase")
	public EntityManagerFactory entityManagerFactory() {
		logger.info("Creating EntityManagerFactory bean");

		String vendorAdapterClass = ormProperties.getProperty("vendoradapter");
		if (null == vendorAdapterClass) {
			throw new BeanCreationException("No JPA vendor adapter class specified");
		}
		
		AbstractJpaVendorAdapter vendorAdapter = null;
		try {
			Class<?> vendorAdapterClazz = Class.forName(vendorAdapterClass);
			vendorAdapter = (AbstractJpaVendorAdapter)vendorAdapterClazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new BeanCreationException("Unable to load the JPA adapter", e);
		} catch (InstantiationException e) {
			throw new BeanCreationException("Unable to load the JPA adapter", e);
		} catch (IllegalAccessException e) {
			throw new BeanCreationException("Unable to load the JPA adapter", e);
		}
		if (Boolean.parseBoolean(ormProperties.getProperty("debug", "false"))) {
			vendorAdapter.setGenerateDdl(true);
			vendorAdapter.setShowSql(true);
		}
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("uk.org.kano.insuranceportal.model");
		factory.setMappingResources("META-INF/orm.xml");
		factory.setJtaDataSource(appDataSource());
		factory.setJpaProperties(ormProperties);
		factory.afterPropertiesSet();
		
		if (!PersistenceUnitTransactionType.JTA.equals(factory.getPersistenceUnitInfo().getTransactionType())) {
			throw new BeanCreationException("Entity Manager Factory is not JTA aware");
		}
		
		logger.info("Created EntityManagerFactory bean");
		return factory.getObject();
	}
	
	/**
	 * This should lookup the platform transaction manager appropriate for the environment
	 * @return
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		logger.info("Creating transaction manager");
		return new JtaTransactionManagerFactoryBean().getObject();
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
}
