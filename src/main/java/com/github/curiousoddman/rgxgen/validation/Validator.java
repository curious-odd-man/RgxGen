package com.github.curiousoddman.rgxgen.validation;

/* **************************************************************************
   Copyright 2019 Vladislavs Varslavans

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
/* **************************************************************************/

/**
 * Validators are introduced to tackle lookaround generation.
 * They check if the text (or part of it) matches lookahead or lookbehind patterns.
 */
public interface Validator {

    /**
     * Checks if {@code text} is valid
     *
     * @param text text to check
     * @return true if text is valid, false otherwise
     */
    boolean isValid(String text);
}
