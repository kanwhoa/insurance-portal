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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.kie.api.cdi.KSession;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import uk.org.kano.insuranceportal.model.domain.ProductIllustration;
import uk.org.kano.insuranceportal.model.internal.ProductCalculationMode;

/**
 * Interface to the KIE/Drools instance. We will use a stateful session to ensure that changes to the
 * facts are monitored and rules re-triggered. The Spring scope is "prototype" meaning that each instance
 * called gets a fresh session (i.e. clean).
 * 
 * As the rules for products can be large, an AgendaFilter will filter the product rules names. It will only
 * fire rules that start with "Generic" or the product name. This also prevents prefixing all tables with the
 * product name.
 * 
 * @author timh
 *
 */
@Service
public class ProductServiceImpl implements ProductService {
	private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
	private Logger kieLogger = LoggerFactory.getLogger("kie");
	
	
	@KSession("product_pricing_session")
	private StatelessKieSession productPricingSession;
	
	private static final String OLIFE_NAME="olife";
	private static final String CALENDAR_NAME="now";
	private static final String LOGGER_NAME="logger";
	
	/**
	 * This is to override the base date/time
	 */
	private Calendar now = null;
	
	/**
	 * Run an object against the product ruleset
	 * TODO: Validation
	 * 
	 * @param in
	 * @return
	 */
	public ProductIllustration runProductRules(ProductIllustration olife, ProductCalculationMode mode) {
		logger.debug("Calling ruleset");
		Assert.notNull(olife.getProduct(), "Product name must not be null");
		Assert.notNull(mode, "Mode cannot be null");
		
		RuleNameMatchesAgendaFilter filter = new RuleNameMatchesAgendaFilter("(?i)^(?:Generic|"+olife.getProduct()+mode.toString()+").*$");		
		FireAllRulesCommand fireCommand = new FireAllRulesCommand();
		fireCommand.setAgendaFilter(filter);
		
		// Note that this will be in the UTC. This is because dates & times don't usually include a
		// time zone. In that case, Java will default to UTC.
		Calendar c = (null == now)?new GregorianCalendar(TimeZone.getTimeZone("UTC")):now;
		
		List<Command<?>> cmds = new ArrayList<>();
		cmds.add(CommandFactory.newInsert(olife, OLIFE_NAME));
		cmds.add(CommandFactory.newSetGlobal(LOGGER_NAME, kieLogger));
		cmds.add(CommandFactory.newSetGlobal(CALENDAR_NAME, c));
		cmds.add(fireCommand);

		// Execute
		ExecutionResults results = productPricingSession.execute(CommandFactory.newBatchExecution(cmds));
		olife = (ProductIllustration)results.getValue(OLIFE_NAME);
		return olife;
	}
	
	/**
	 * This is to override the base calendar time/date from where all date differences are calculated
	 * @param now
	 */
	public void setBaseCalendar(Calendar now) {
		this.now = now;
	}
}
