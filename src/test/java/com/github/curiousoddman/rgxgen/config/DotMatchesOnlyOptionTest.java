package com.github.curiousoddman.rgxgen.config;

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


import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.model.RgxGenCharsDefinition;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DotMatchesOnlyOptionTest {


    @Test
    void doesNotFailWithoutPropertySet() {
        RgxGen rgxGen = assertDoesNotThrow(() -> new RgxGen("."));
        assertDoesNotThrow(() -> rgxGen.generate());
        assertDoesNotThrow(() -> rgxGen.generateNotMatching());
        assertDoesNotThrow(rgxGen::getUniqueEstimation);
    }

    @Test
    void verifyOptionMatters() {
        RgxGenProperties properties = new RgxGenProperties();
        String permittedCharacters = "abc";
        RgxGenOption.DOT_MATCHES_ONLY.setInProperties(properties, RgxGenCharsDefinition.of(permittedCharacters));
        Random random = new Random(100500);
        RgxGen rgxGen = new RgxGen(".");
        for (int i = 0; i < 100; i++) {
            String generatedValue = rgxGen.generate(random);
            assertTrue(permittedCharacters.contains(generatedValue));
        }
    }

    @Test
    void verifyCorrectlyEstimatesCount() {
        RgxGenProperties properties = new RgxGenProperties();
        String permittedCharacters = "abc";
        RgxGenOption.DOT_MATCHES_ONLY.setInProperties(properties, RgxGenCharsDefinition.of(permittedCharacters));
        RgxGen rgxGen = new RgxGen(".");
        assertEquals(BigInteger.valueOf(3), rgxGen.getUniqueEstimation().get());
    }
}
