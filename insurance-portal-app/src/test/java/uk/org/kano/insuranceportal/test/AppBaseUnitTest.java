package uk.org.kano.insuranceportal.test;

import org.junit.Before;
import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;

import uk.org.kano.insuranceportal.config.TestDataConfiguration;
import uk.org.kano.insuranceportal.config.app.KieConfiguration;
import uk.org.kano.insuranceportal.config.app.ServiceConfiguration;
import uk.org.kano.insuranceportal.config.app.UtilityConfiguration;

/**
 * Configure the KIE test configuration to look at the right Maven directories
 * @author timh
 *
 */
@ContextConfiguration(
		classes = { ServiceConfiguration.class, UtilityConfiguration.class, KieConfiguration.class, TestDataConfiguration.class }
)
@Ignore
public class AppBaseUnitTest extends BaseUnitTest {

	/**
	 * Setup the test
	 */
	@Before
	public void initialize() throws Exception {
		super.initialize();
	}

}
