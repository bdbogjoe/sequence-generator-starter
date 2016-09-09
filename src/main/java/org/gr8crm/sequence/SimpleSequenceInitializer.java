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

package org.gr8crm.sequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A sequence initializer that gets 'format', 'start' and 'increment' from configuration
 * @see SequenceGeneratorProperties
 */
@Component
public class SimpleSequenceInitializer implements SequenceInitializer {

    private SequenceGeneratorProperties properties;

    @Autowired
    public SimpleSequenceInitializer(SequenceGeneratorProperties properties) {
        this.properties = properties;
    }

    @Override
    public SequenceStatus initialize(long tenant, String name, String group) {
        SequenceGeneratorProperties.Entry entry = properties.getSequence(name);
        if(group != null) {
            entry = entry.getGroup(group);
        }
        if(entry.getIncrement() == 0) {
            throw new IllegalArgumentException("increment must be non-zero");
        }
        return new SequenceStatus(name, group, entry.getFormat(), entry.getStart(), entry.getIncrement());
    }
}
