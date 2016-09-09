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
 * Sequence configuration.
 */
public class SequenceConfiguration {
    private final String app;
    private final long tenant;
    private final String name;
    private final String group;
    private final String format;
    private final long start;
    private final int increment;

    public SequenceConfiguration(String app, long tenant, String name, String group, String format, long start, int increment) {
        this.app = app;
        this.tenant = tenant;
        this.name = name;
        this.group = group;
        this.format = format;
        this.start = start;
        this.increment = increment;
    }

    public String getApp() {
        return app;
    }

    public long getTenant() {
        return tenant;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getFormat() {
        return format;
    }

    public long getStart() {
        return start;
    }

    public int getIncrement() {
        return increment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceConfiguration that = (SequenceConfiguration) o;

        if (tenant != that.tenant) return false;
        if (start != that.start) return false;
        if (increment != that.increment) return false;
        if (app != null ? !app.equals(that.app) : that.app != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        return format != null ? format.equals(that.format) : that.format == null;

    }

    @Override
    public int hashCode() {
        int result = app != null ? app.hashCode() : 0;
        result = 31 * result + (int) (tenant ^ (tenant >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + increment;
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private  String app;
        private  long tenant;
        private  String name;
        private  String group;
        private  String format = "%d";
        private  long start = 0;
        private  int increment = 1;

        public Builder withApp(String app) {
            this.app = app;
            return this;
        }

        public Builder withTenant(long tenant) {
            this.tenant = tenant;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder withFormat(String format) {
            this.format = format;
            return this;
        }

        public Builder withStart(long start) {
            this.start = start;
            return this;
        }

        public Builder withIncrement(int increment) {
            this.increment = increment;
            return this;
        }

        public SequenceConfiguration build() {
            if(this.app == null) {
                throw new IllegalArgumentException("application name is not set");
            }
            if(this.name == null) {
                throw new IllegalArgumentException("sequence name is not set");
            }
            if(this.increment == 0) {
                throw new IllegalArgumentException("increment must be non-zero");
            }
            if(this.format == null) {
                this.format = "%d";
            }
            return new SequenceConfiguration(this.app, this.tenant, this.name, this.group, this.format, this.start, this.increment);
        }
    }
}
