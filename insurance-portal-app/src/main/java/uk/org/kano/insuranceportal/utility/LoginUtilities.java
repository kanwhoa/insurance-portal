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

import java.security.SecureRandom;

import org.apache.commons.lang.CharUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import uk.org.kano.insuranceportal.model.internal.User;

/**
 * Password and login utility class
 * @author timh
 *
 */
public class LoginUtilities {
	private static SecureRandom secureRandom = new SecureRandom();
	
	/**
	 * Generate a random password of specified length
	 * @param length The length of the password to generate
	 * @return The random password
	 */
	public static String generateRandomPassword(int length) {
		Assert.isTrue(length>0, "Must specify a length greater than 0");
		
		// This is pretty slow, but it happens infrequently
		StringBuffer newPassword = new StringBuffer();
		for (int i=0; i<length; i++) {
			char c = (char)secureRandom.nextInt(127);
			if (CharUtils.isAsciiPrintable(c)) {
				newPassword.append(c);
			} else {
				i--;
			}
		}
		return newPassword.toString();
	}

	/**
	 * Generate a long (32 character) random password
	 * @return The random password
	 */
	public static String generateRandomPassword() {
		return generateRandomPassword(32);
	}
	
	/**
	 * Get the currently logged in user.
	 * @return The User that is logged in, or null if not logged in.
	 */
	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null == auth || !auth.isAuthenticated()) return null;
		return (User)auth.getPrincipal();
	}

}
