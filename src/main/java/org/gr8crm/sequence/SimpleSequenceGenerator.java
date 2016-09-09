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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Created by goran on 2016-08-20.
 */
@Component
@ConfigurationProperties(prefix = "sequence")
public class SimpleSequenceGenerator implements SequenceGenerator {

    private static final String KEY_SEPARATOR = "/";

    private static final Map<String, Entry> sequences = new HashMap<>();

    private String key(SequenceConfiguration config) {
        return key(config.getApp(), config.getTenant(), config.getName(), config.getGroup());
    }

    private String key(String app, long tenant, String name, String group) {
        Objects.requireNonNull(app, "application name must be specified");
        if (app.contains(KEY_SEPARATOR)) {
            throw new IllegalArgumentException("application name cannot contain " + KEY_SEPARATOR);
        }
        List<String> tmp = new ArrayList<>(4);

        tmp.add(app);

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
    public SequenceStatus create(final SequenceConfiguration config) {
        final String key = key(config);
        final Entry entry = new Entry(config);
        sequences.put(key, entry);
        return entry.status();
    }

    @Override
    public boolean delete(String app, long tenant, String name, String group) {
        final String key = key(app, tenant, name, group);
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
    public String nextNumber(String app, long tenant, String name, String group) {
        final String key = key(app, tenant, name, group);
        Entry n = sequences.get(key);
        if (n == null) {
            throw new IllegalArgumentException("No such sequence: " + key);
        }
        return n.getFormattedAndIncrement();
    }

    @Override
    public long nextNumberLong(String app, long tenant, String name, String group) {
        final String key = key(app, tenant, name, group);
        Entry n = sequences.get(key);
        if (n == null) {
            throw new IllegalArgumentException("No such sequence: " + key);
        }
        return n.getAndIncrement();
    }

    @Override
    public SequenceStatus update(String app, long tenant, String name, String group, long current, long newCurrent) {
        final String key = key(app, tenant, name, group);
        final Entry n = sequences.get(key);
        if (n == null) {
            throw new IllegalArgumentException("No such sequence: " + key);
        }

        n.set(current, newCurrent);

        return n.status();
    }

    @Override
    public SequenceStatus status(String app, long tenant, String name, String group) {
        final String key = key(app, tenant, name, group);
        final Entry n = sequences.get(key);
        if (n == null) {
            throw new IllegalArgumentException("No such sequence: " + key);
        }

        return n.status();
    }

    @Override
    public Stream<SequenceStatus> statistics(String app, long tenant) {
        return sequences.values().stream()
                .filter(entry -> entry.config.getApp().equals(app) && entry.config.getTenant() == tenant)
                .map(Entry::status);
    }

    @Override
    public void shutdown() {
        sequences.clear();
    }

    private static class Entry {
        private SequenceConfiguration config;
        private AtomicLong number;

        Entry(SequenceConfiguration config) {
            this.config = config;
            this.number = new AtomicLong(config.getStart());
        }

        String getFormattedAndIncrement() {
            return String.format(this.config.getFormat(), getAndIncrement());
        }

        long getAndIncrement() {
            return this.number.getAndAdd(this.config.getIncrement());
        }

        long set(long current, long start) {
            if (this.number.get() == current) {
                this.number.set(start);
            }
            return this.number.get();
        }

        SequenceStatus status() {
            return new SequenceStatus(config, number.get());
        }
    }
}
