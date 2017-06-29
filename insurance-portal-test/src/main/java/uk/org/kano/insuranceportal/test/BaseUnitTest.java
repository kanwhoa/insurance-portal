package uk.org.kano.insuranceportal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import uk.org.kano.insuranceportal.config.test.TestConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(
		classes = { TestConfiguration.class }
	)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Ignore
public class BaseUnitTest {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected ObjectMapper objectMapper;

	/**
	 * Setup the test
	 */
	@Before
	public void initialize() throws Exception {
		log.debug("Test initialising");
	}
	
	/**
	 * Clean up after the test execution
	 * 
	 * @throws Exception
	 */
	@After
	public void finalise() throws Exception {
		log.debug("Test finalising");
	}
	
	/**
	 * Log an object to the output
	 * @param o
	 */
	protected void logObject(String message, Object o) {
		ObjectWriter writer = objectMapper.writer();	
		try {
			log.info((null == message?"Object: ":message)+(null==o?"null":writer.writeValueAsString(o)));
		} catch (JsonProcessingException e) {
			log.error("Tried to write object of type "+o.getClass().getName()+" but failed", e);
		}
	}

	/**
	 * Log and object with a default message
	 * @param o
	 */
	protected void logObject(Object o) {
		logObject(null, o);
	}

}
