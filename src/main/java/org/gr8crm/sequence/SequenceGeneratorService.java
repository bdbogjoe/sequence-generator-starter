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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class SequenceGeneratorService {

    private static final Log log = LogFactory.getLog(SequenceGeneratorService.class);

    private static final long NO_TENANT = 0;

    private final SequenceGenerator sequenceGenerator;

    @Autowired
    public SequenceGeneratorService(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public SequenceStatus initialize(String name) {
        return initialize(name, null, NO_TENANT);
    }

    public SequenceStatus initialize(String name, String group) {
        return initialize(name, group, NO_TENANT);
    }

    public SequenceStatus initialize(String name, String group, long tenant) {
        return sequenceGenerator.create(tenant, name, group);
    }

    public String nextNumber(String name) {
        return nextNumber(name, null, NO_TENANT);
    }

    public String nextNumber(String name, String group) {
        return nextNumber(name, group, NO_TENANT);
    }

    public String nextNumber(String name, String group, long tenant) {
        return sequenceGenerator.nextNumber(tenant, name, group);
    }

    public long nextNumberLong(String name) {
        return nextNumberLong(name, null, NO_TENANT);
    }

    public long nextNumberLong(String name, String group) {
        return nextNumberLong(name, group, NO_TENANT);
    }

    public long nextNumberLong(String name, String group, long tenant) {
        return sequenceGenerator.nextNumberLong(tenant, name, group);
    }

    public boolean setNextNumber(long tenant, String name, String group, long currentNumber, long newNumber) {
        if (currentNumber != newNumber) {
            final SequenceStatus status = status(name, group, tenant);
            if (sequenceGenerator.update(tenant, name, group, null, currentNumber, newNumber, status.getIncrement()) != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Sequence [" + name + "] in tenant [" + tenant + "] changed from [" + currentNumber + "] to [" + newNumber + "]");
                }
                return true;
            }
        }
        return false;
    }

    public void setIncrement(long tenant, String name, String group, int newIncrement) {
        final SequenceStatus status = status(name, group, tenant);
        if (sequenceGenerator.update(tenant, name, group, null, status.getNumber(), status.getNumber(), newIncrement) != null) {
            if (log.isDebugEnabled()) {
                log.debug("Sequence [" + name + "] in tenant [" + tenant + "] changed increment from [" + status.getIncrement() + "] to [" + newIncrement + "]");
            }
        }
    }

    public SequenceStatus status(String name) {
        return status(name, null, NO_TENANT);
    }

    public SequenceStatus status(String name, String group) {
        return status(name, group, NO_TENANT);
    }

    public SequenceStatus status(String name, String group, long tenant) {
        return sequenceGenerator.status(tenant, name, group);
    }

    public void shutdown() {
        sequenceGenerator.shutdown();
    }

    public Stream<SequenceStatus> statistics(long tenant) {
        return sequenceGenerator.statistics(tenant);
    }

}
