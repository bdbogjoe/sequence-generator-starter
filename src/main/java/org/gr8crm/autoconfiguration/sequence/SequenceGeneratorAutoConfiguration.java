/*
 * Copyright (c) 2016 Goran Ehrsson.
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
 */

package org.gr8crm.autoconfiguration.sequence;

import org.gr8crm.sequence.SequenceGenerator;
import org.gr8crm.sequence.SequenceGeneratorProperties;
import org.gr8crm.sequence.SequenceInitializer;
import org.gr8crm.sequence.SimpleSequenceGenerator;
import org.gr8crm.sequence.SimpleSequenceInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * AutoConfiguration for SequenceGenerator.
 */
@Configuration
@ComponentScan(basePackages = "org.gr8crm.sequence")
@EnableConfigurationProperties(SequenceGeneratorProperties.class)
public class SequenceGeneratorAutoConfiguration {

    @Autowired
    private SequenceGeneratorProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public SequenceInitializer sequenceInitializer() {
        return new SimpleSequenceInitializer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SequenceGenerator sequenceGenerator(SequenceInitializer sequenceInitializer) {
        return new SimpleSequenceGenerator(sequenceInitializer);
    }
/*
    protected static class TestSequenceInitializer implements SequenceInitializer {

        private static final String DEFAULT_FORMAT = "%d";
        private static final long DEFAULT_START = 1L;
        private static final int DEFAULT_INCREMENT = 1;

        public static String format = DEFAULT_FORMAT;
        public static long start = DEFAULT_START;
        public static int increment = DEFAULT_INCREMENT;

        public static void configure(long s, int inc, String f) {
            start = s;
            format = f;
            increment = inc;
        }

        public static void reset() {
            format = DEFAULT_FORMAT;
            start = DEFAULT_START;
            increment = DEFAULT_INCREMENT;
        }

        @Override
        public SequenceStatus initialize(long tenant, String name, String group) {
            return new SequenceStatus(name, group, format, start, increment);
        }
    }
    */
}
