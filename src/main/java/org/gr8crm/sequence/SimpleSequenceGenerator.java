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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by goran on 2016-08-20.
 */
@Component
@ConfigurationProperties(prefix = "sequence")
public class SimpleSequenceGenerator implements SequenceGenerator {

    private static final String KEY_SEPARATOR = "/";
    private static final String DEFAULT_FORMAT = "%d";

    private static final Map<String, Entry> sequences = new HashMap<>();

    private SequenceInitializer sequenceInitializer;

    @Value("${autoInitialize:false}")
    private boolean autoInitialize;

    @Autowired
    public SimpleSequenceGenerator(SequenceInitializer sequenceInitializer) {
        this.sequenceInitializer = sequenceInitializer;
    }

    private String key(long tenant, String name, String group) {
        List<String> tmp = new ArrayList<>(3);

        tmp.add(String.valueOf(tenant));

        if (name != null) {
            tmp.add(name);
        }
        if (group != null) {
            tmp.add(group);
        }
        return String.join(KEY_SEPARATOR, tmp);
    }

    @Override
    public SequenceStatus create(long tenant, String name, String group) {
        final SequenceStatus status = sequenceInitializer.initialize(tenant, name, group);
        sequences.put(key(tenant, name, group), new Entry(name, group, status.getFormat(), status.getNumber(), status.getIncrement()));
        return status;
    }

    @Override
    public boolean delete(long tenant, String name, String group) {
        final String key = key(tenant, name, group);
        if (sequences.containsKey(key)) {
            synchronized (sequences) {
                if (sequences.containsKey(key)) {
                    sequences.remove(key);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String nextNumber(long tenant, String name, String group) {
        final String key = key(tenant, name, group);
        Entry n = sequences.get(key);
        if (n == null) {
            if(!autoInitialize) {
                throw new IllegalArgumentException("No such sequence: " + key);
            }
            synchronized (sequences) {
                n = sequences.get(key);
                if (n == null) {
                    // TODO Initializing can be an expensive operation so its bad to do it inside a synchronized block.
                    final SequenceStatus status = sequenceInitializer.initialize(tenant, name, group);
                    n = new Entry(name, group, status.getFormat(), status.getNumber(), status.getIncrement());
                    sequences.put(key, n);
                }
            }
        }
        return n.getFormattedAndIncrement();
    }

    @Override
    public long nextNumberLong(long tenant, String name, String group) {
        final String key = key(tenant, name, group);
        Entry n = sequences.get(key);
        if (n == null) {
            if(!autoInitialize) {
                throw new IllegalArgumentException("No such sequence: " + key);
            }
            synchronized (sequences) {
                n = sequences.get(key);
                if (n == null) {
                    // TODO Initializing can be an expensive operation so its bad to do it inside a synchronized block.
                    final SequenceStatus status = sequenceInitializer.initialize(tenant, name, group);
                    n = new Entry(name, group, status.getFormat(), status.getNumber(), status.getIncrement());
                    sequences.put(key, n);
                }
            }
        }
        return n.getAndIncrement();
    }

    @Override
    public SequenceStatus update(long tenant, String name, String group, String format, long current, long newCurrent, int newIncrement) {
        final Entry n = sequences.get(key(tenant, name, group));
        if (n == null) {
            throw new IllegalArgumentException("No such sequence: " + key(tenant, name, group));
        }

        return new SequenceStatus(name, group, format, n.set(current, newCurrent), newIncrement);
    }

    @Override
    public SequenceStatus status(long tenant, String name, String group) {
        final Entry n = sequences.get(key(tenant, name, group));
        return n != null ? n.status() : null;
    }

    @Override
    public Stream<SequenceStatus> statistics(long tenant) {
        return sequences.values().stream().map(Entry::status);
    }

    @Override
    public void shutdown() {
        sequences.clear();
    }

    private static class Entry {
        private String name;
        private String group;
        private String format;
        private int increment;
        private AtomicLong number;

        Entry(String name, String group, String format, long number, int increment) {
            this.name = name;
            this.group = group;
            this.format = format != null ? format : DEFAULT_FORMAT;
            this.increment = increment;
            this.number = new AtomicLong(number);
        }

        String getFormattedAndIncrement() {
            return String.format(this.format, getAndIncrement());
        }

        long getAndIncrement() {
            return this.number.getAndAdd(this.increment);
        }

        long set(long current, long start) {
            if (this.number.get() == current) {
                this.number.set(start);
            }
            return this.number.get();
        }

        SequenceStatus status() {
            return new SequenceStatus(name, group, format, number.get(), increment);
        }
    }
}
