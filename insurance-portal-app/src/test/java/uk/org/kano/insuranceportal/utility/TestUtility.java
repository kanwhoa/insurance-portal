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
package uk.org.kano.insuranceportal.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.RunScript;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * Setup a basic environment for testing
 * @author timh
 *
 */
public class TestUtility {
	// TODO: reuse the main configured converter
	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		AnnotationIntrospectorPair pair = new AnnotationIntrospectorPair(primary, secondary);
		mapper.setAnnotationIntrospector(pair);
		return mapper.writeValueAsBytes(object);
	}

	/**
	 * Run SQL statements from a file
	 * @param conn The database connection
	 * @param file The SQL file
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void runSqlFile(Connection conn, String file) throws IOException, SQLException {
		URL url = TestUtility.class.getResource(file);
		if (null == url) throw new FileNotFoundException("SQL file not found");
		
		BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
		try {
			RunScript.execute(conn, r);
		} catch (SQLException e) {
			throw e;
		} finally {
			r.close();			
		}
	}
}
