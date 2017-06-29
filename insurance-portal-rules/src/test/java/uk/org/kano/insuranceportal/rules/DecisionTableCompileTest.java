/**
 * Copyright 2017 Tim Hurman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.kano.insuranceportal.rules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class DecisionTableCompileTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void compileProduct1() throws IOException {
		SpreadsheetCompiler comp = new SpreadsheetCompiler();
		
		String xlsFile = "/uk/org/kano/insuranceportal/rules/product/Product1BySumAssured.xls";
		URL xlsUrl = this.getClass().getResource(xlsFile);
		assertThat("Product 1 decision table not found", xlsUrl, notNullValue());

		logger.info("Loading decision table "+xlsUrl);
		String drl = comp.compile(true, xlsUrl.openStream(), InputType.XLS);
		
		// Cannot name .drl as it appears in teh test class path and causes unit tests to fail
		String drlFile = xlsUrl.getPath().replaceAll("\\.xls$", ".txt");
		logger.info("Writing to DRL file "+drlFile);
		
		FileOutputStream fos = new FileOutputStream(new File(drlFile), false);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		
		osw.write(drl);
		osw.flush();
		osw.close();
	}

}
