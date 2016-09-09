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

/**
 * A sequence generator snapshot.
 */
public class SequenceStatus {

    private SequenceConfiguration configuration;
    private final long number;

    public SequenceStatus(SequenceConfiguration configuration, long number) {
        this.configuration = configuration;
        this.number = number;
    }

    public SequenceConfiguration getConfiguration() {
        return configuration;
    }

    public long getNumber() {
        return number;
    }

    String getNumberFormatted() {
        return String.format(configuration.getFormat(), number);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append(configuration.getApp());
        s.append('/');
        s.append(String.valueOf(configuration.getTenant()));
        s.append('/');
        s.append(configuration.getName());

        if (configuration.getGroup() != null) {
            s.append('/');
            s.append(configuration.getGroup());
        }
        s.append('=');
        s.append(getNumberFormatted());

        return s.toString();
    }
}
