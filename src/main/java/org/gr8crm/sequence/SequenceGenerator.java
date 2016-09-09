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

import java.util.stream.Stream;

/**
 * Sequence Generator interface.
 */
public interface SequenceGenerator {
    /**
     * Create a new sequence.
     *
     * @param tenant tenant ID
     * @param name   sequence name
     * @param group  sub-sequence
     * @return current sequence status
     */
    SequenceStatus create(long tenant, String name, String group);

    /**
     * Delete a sequence.
     *
     * @param tenant tenant ID
     * @param name   sequence name
     * @param group  sub-sequence
     * @return true if sequence was removed
     */
    boolean delete(long tenant, String name, String group);

    /**
     * Get next unique number formatted.
     *
     * @param tenant tenant ID
     * @param name   sequence name
     * @param group  sub-sequence
     * @return formatted number
     */
    String nextNumber(long tenant, String name, String group);

    /**
     * Get next unique (raw) number.
     *
     * @param tenant tenant ID
     * @param name   sequence name
     * @param group  sub-sequence
     * @return number as a long
     */
    long nextNumberLong(long tenant, String name, String group);

    /**
     * Update sequence.
     *
     * @param tenant  tenant ID
     * @param name    sequence name
     * @param group   sub-sequence
     * @param format  number format
     * @param current current number
     * @param start   new number
     * @param increment increment
     * @return sequence status if sequence was updated, null otherwise
     */
    SequenceStatus update(long tenant, String name, String group, String format, long current, long start, int increment);

    /**
     * Current status of a sequence.
     *
     * @param tenant tenant ID
     * @param name   sequence name
     * @param group  sub-sequence
     * @return current status
     */
    SequenceStatus status(long tenant, String name, String group);

    /**
     * Get sequence statistics.
     *
     * @param tenant tenant ID
     * @return statistics for all sequences in the tenant
     */
    Stream<SequenceStatus> statistics(long tenant);

    /**
     * Shutdown the sequence generator.
     * Implementations can close connections, do cleanup, etc.
     */
    void shutdown();
}
