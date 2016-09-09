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

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for the sequence-generator prefix.
 */
@ConfigurationProperties(prefix = "sequence-generator")
public class SequenceGeneratorProperties {

    public static final String DEFAULT_SEQUENCE = "default";

    private Map<String, Entry> sequences;

    public Map<String, Entry> getSequences() {
        return sequences;
    }

    public void setSequences(Map<String, Entry> sequences) {
        this.sequences = sequences;
    }

    public Entry getSequence(String name) {
        Entry entry;
        if(sequences == null) {
            sequences = new HashMap<>();
        }
        entry = sequences.get(name);
        if(entry == null) {
            if(name.equals(DEFAULT_SEQUENCE)) {
                entry = new Entry();
            } else {
                entry = new Entry(getDefault());
            }
            sequences.put(name, entry);
        }
        return entry;
    }

    public void setSequence(String name, String group, boolean create, String format, int start, int increment) {
        Entry entry = getSequence(name);
        if(group != null) {
            entry = entry.getGroup(group);
        }
        entry.setCreate(create);
        entry.setFormat(format);
        entry.setStart(start);
        entry.setIncrement(increment);
    }

    public Entry getDefault() {
        return getSequence(DEFAULT_SEQUENCE);
    }

    public void setDefault(boolean create, String format, int start, int increment) {
        Entry standard = getSequence(DEFAULT_SEQUENCE);
        standard.setCreate(create);
        standard.setFormat(format);
        standard.setStart(start);
        standard.setIncrement(increment);
    }

    public static class Entry {
        private Entry parent;
        private boolean create;
        private String format = "%d";
        private int start = 0;
        private int increment = 1;
        private Map<String, Entry> groups;

        public Entry() {
        }

        public Entry(Entry clone) {
            this.parent = clone.parent;
            this.create = clone.create;
            this.format = clone.format;
            this.start = clone.start;
            this.increment = clone.increment;
        }

        public Entry(Entry parent, boolean create, String format, int start, int increment) {
            this.parent = parent;
            this.create = create;
            this.format = format;
            this.start = start;
            this.increment = increment;
            this.groups = new HashMap<>();
        }

        public boolean isCreate() {
            return create;
        }

        public void setCreate(boolean create) {
            this.create = create;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getIncrement() {
            return increment;
        }

        public void setIncrement(int increment) {
            this.increment = increment;
        }

        public Map<String, Entry> getGroups() {
            return groups;
        }

        public void setGroups(Map<String, Entry> groups) {
            this.groups = groups;
        }

        public Entry getGroup(String name) {
            Entry entry;
            if(groups == null) {
                groups = new HashMap<>();
            }
            entry = groups.get(name);
            if(entry == null) {
                entry = new Entry(this);
                groups.put(name, entry);
            }
            return entry;
        }

        public void setGroup(String name, boolean create, String format, int start, int increment) {
            Entry entry = getGroup(name);
            entry.setCreate(create);
            entry.setFormat(format);
            entry.setStart(start);
            entry.setIncrement(increment);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "create=" + create +
                    ", format='" + format + '\'' +
                    ", start=" + start +
                    ", increment=" + increment +
                    ", groups=" + groups +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SequenceGeneratorProperties{" +
                "sequences=" + sequences +
                '}';
    }
}
