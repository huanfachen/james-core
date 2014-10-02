/*
 * Copyright 2014 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.core.problems.constraints.validations;

import org.jamesframework.core.problems.constraints.Validation;

/**
 * Represents a validation of a subset solution. Contains a reference to the
 * validation object produced when checking the general constraints and also
 * indicates whether the subset has a valid size. It can be checked wether the
 * subset passed validation, possibly ignoring its size.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class SubsetValidation implements Validation {

    // valid size
    private final boolean validSize;
    // general constraint validation
    private final Validation constraintValidation;

    /**
     * Create a subset validation.
     * 
     * @param validSize indicates whether the subset has a valid size
     * @param constraintValidation validation object produced when checking the general constraints
     */
    public SubsetValidation(boolean validSize, Validation constraintValidation) {
        this.validSize = validSize;
        this.constraintValidation = constraintValidation;
    }
    
    /**
     * Get the underlying constraint validation object.
     * 
     * @return constraint validation object
     */
    public Validation getConstraintValidation(){
        return constraintValidation;
    }
    
    /**
     * Check whether the subset solution passed validation. If <code>checkSize</code>
     * is <code>false</code> the size of the subset is ignored and only the general
     * constraints are checked.
     * 
     * @param checkSize indicates whether the size should be validated
     * @return <code>true</code> if the subset solution is valid, possibly ignoring its size
     */
    public boolean passed(boolean checkSize) {
        return constraintValidation.passed() && (!checkSize || validSize);
    }
    
    /**
     * Check whether the subset solution passed validation, taking into
     * account both its size and the general constraint validation.
     * 
     * @return <code>true</code> if the subset has a valid size and satisfies all constraints
     */
    @Override
    public boolean passed(){
        return passed(true);
    }

}
