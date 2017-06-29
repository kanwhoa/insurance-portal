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

import uk.org.kano.insuranceportal.Application;

/**
 * Methods to help the application container startup and manage itself.
 * 
 * @author timh
 *
 */
public class StartupUtilities {
	/**
	 * Define the current environment as production.
	 */
	public static final String PROFILE_PROD = "prod";
	/**
	 * Define the current environment as test. Not if, this or {@link StartupUtilities#PROFILE_PROD}
	 * is not specified, the app defaults to a production environment.
	 */
	public static final String PROFILE_TEST = "test";
	/**
	 * Define the current environment as a unit test.
	 */
	public static final String PROFILE_UNITTEST = "unittest";

	/**
	 * Get the root package environment
	 * @return The '/' separated path of the Application class. 
	 */
	public static String getPackageRoot() {
		return Application.class.getPackage().getName().replaceAll("\\.", "/");
	}
}
