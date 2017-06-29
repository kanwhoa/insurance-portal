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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Some utilities for sanitising data. These are distinct from the validators as they
 * may end up changing some data. In the example of an email, they can remove the name part
 * so that we only have the email.
 * 
 * @author timh
 *
 */
public class SanitisationUtilities {
	
	/**
	 * This calls @link {@link #sanitiseEmailAddress(String, boolean)} with the second argument
	 * set to true.
	 * 
	 * @param addressAsString
	 * @return
	 * @throws AddressException 
	 */
	public static String sanitiseEmailAddress(String addressAsString) throws AddressException {
		return sanitiseEmailAddress(addressAsString, true);
	}
	
	/**
	 * Sanitise an email address. This will take a string containing an email address, extract
	 * parse it into an InternetAddress and convert it back. It will convert any Unicode escapes
	 * into Unicode and so is not transport safe. Reparse back into an InternetAddress for sending.
	 * 
	 * @param addressAsString The email address.
	 * @param removeMetadata if true, return the address only, else the whole email structure.
	 * @return
	 * @throws AddressException 
	 */
	public static String sanitiseEmailAddress(String addressAsString, boolean removeMetadata) throws AddressException {
		final InternetAddress[] addresses;
		final InternetAddress result;
		
		addresses = addressAsString != null ? InternetAddress.parse(addressAsString) : new InternetAddress[0];
		
		if (addresses.length == 1) {
			result = addresses[0];
		} else if (addresses.length == 0) {
			result = null;
		} else {
			throw new AddressException("There was multiple addresses provided");
		}
		
		return removeMetadata?result.getAddress():result.toUnicodeString();
	}

}
