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
package uk.org.kano.insuranceportal.model.internal.validators;

/**
 * A interface telling the validation to check the password objects. To use this,
 * set it as a group on the validation annotations, e.g. @ValidationAnnotation(groups={Password.class})
 * then call the {@link uk.org.kano.insuranceportal.utility.ValidationUtilities#validateObject(org.springframework.validation.Validator, Object, String, Object...) validateObject}
 * with this class as the final argument.
 * 
 * @author timh
 *
 */
public interface Password {

}
