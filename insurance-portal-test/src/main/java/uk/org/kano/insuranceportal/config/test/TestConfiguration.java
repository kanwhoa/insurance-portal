package uk.org.kano.insuranceportal.config.test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {	
	/**
	 * For creating XML objects
	 * @return
	 * @throws DatatypeConfigurationException 
	 */
	@Bean
	public DatatypeFactory dataTypeFactory() throws DatatypeConfigurationException {
		return DatatypeFactory.newInstance();
	}
}
