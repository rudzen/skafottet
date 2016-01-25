/*
 * Copyright 2016 Rudy Alex Kohn
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

package com.undyingideas.thor.skafottet.broadcastrecievers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 16-01-2016, 17:15.
 * Project : skafottet
 * Generic Battery information DTO
 * @author rudz
 */
public class BatteryDTO implements Parcelable {

    private int health;
    private int iconSmall;
    private int level;
    private int plugged;
    private boolean present;
    private int scale;
    private int status;
    private String tech;
    private int temperature;
    private int voltage;

    public BatteryDTO(final int health, final int iconSmall, final int level, final int plugged, final boolean present, final int scale, final int status, final String tech, final int temperature, final int voltage) {
        this.health = health;
        this.iconSmall = iconSmall;
        this.level = level;
        this.plugged = plugged;
        this.present = present;
        this.scale = scale;
        this.status = status;
        this.tech = tech;
        this.temperature = temperature;
        this.voltage = voltage;
    }

    private BatteryDTO(final Builder builder) {
        health = builder.health;
        iconSmall = builder.iconSmall;
        level = builder.level;
        plugged = builder.plugged;
        present = builder.present;
        scale = builder.scale;
        status = builder.status;
        tech = builder.tech;
        temperature = builder.temperature;
        voltage = builder.voltage;
    }

    public static Builder newBuilder() { return new Builder(); }

    public int getHealth() { return health; }

    public void setHealth(final int health) { this.health = health; }

    public int getIconSmall() { return iconSmall; }

    public void setIconSmall(final int iconSmall) { this.iconSmall = iconSmall; }

    public int getLevel() { return level; }

    public void setLevel(final int level) { this.level = level; }

    public int getPlugged() { return plugged; }

    public void setPlugged(final int plugged) { this.plugged = plugged; }

    public boolean isPresent() { return present; }

    public void setPresent(final boolean present) { this.present = present; }

    public int getScale() { return scale; }

    public void setScale(final int scale) { this.scale = scale; }

    public int getStatus() { return status; }

    public void setStatus(final int status) { this.status = status; }

    public String getTech() { return tech; }

    public void setTech(final String tech) { this.tech = tech; }

    public int getTemperature() { return temperature; }

    public void setTemperature(final int temperature) { this.temperature = temperature; }

    public int getVoltage() { return voltage; }

    public void setVoltage(final int voltage) { this.voltage = voltage; }


    /* parcel code */

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(health);
        dest.writeInt(iconSmall);
        dest.writeInt(level);
        dest.writeInt(plugged);
        dest.writeByte(present ? (byte) 1 : (byte) 0);
        dest.writeInt(scale);
        dest.writeInt(status);
        dest.writeString(tech);
        dest.writeInt(temperature);
        dest.writeInt(voltage);
    }

    private BatteryDTO(final Parcel in) {
        health = in.readInt();
        iconSmall = in.readInt();
        level = in.readInt();
        plugged = in.readInt();
        present = in.readByte() != 0;
        scale = in.readInt();
        status = in.readInt();
        tech = in.readString();
        temperature = in.readInt();
        voltage = in.readInt();
    }

    public static final Creator<BatteryDTO> CREATOR = new BatteryDTOCreator();

    private static class BatteryDTOCreator implements Creator<BatteryDTO> {
        @Override
        public BatteryDTO createFromParcel(final Parcel source) {return new BatteryDTO(source);}

        @Override
        public BatteryDTO[] newArray(final int size) {return new BatteryDTO[size];}
    }

    /**
     * {@link BatteryDTO} builder static inner class.
     */
    @SuppressWarnings({"PublicInnerClass", "ClassNamingConvention", "ReturnOfThis"})
    public static final class Builder {
        private int health;
        private int iconSmall;
        private int level;
        private int plugged;
        private boolean present;
        private int scale;
        private int status;
        private String tech;
        private int temperature;
        private int voltage;

        private Builder() { }

        /**
         * Sets the {@code health} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code health} to set
         * @return a reference to this Builder
         */
        public Builder health(final int val) {
            health = val;
            return this;
        }

        /**
         * Sets the {@code iconSmall} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code iconSmall} to set
         * @return a reference to this Builder
         */
        public Builder iconSmall(final int val) {
            iconSmall = val;
            return this;
        }

        /**
         * Sets the {@code level} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code level} to set
         * @return a reference to this Builder
         */
        public Builder level(final int val) {
            level = val;
            return this;
        }

        /**
         * Sets the {@code plugged} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code plugged} to set
         * @return a reference to this Builder
         */
        public Builder plugged(final int val) {
            plugged = val;
            return this;
        }

        /**
         * Sets the {@code present} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code present} to set
         * @return a reference to this Builder
         */
        public Builder present(final boolean val) {
            present = val;
            return this;
        }

        /**
         * Sets the {@code scale} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val
         *         the {@code scale} to set
         * @return a reference to this Builder
         */
        public Builder scale(final int val) {
            scale = val;
            return this;
        }

        /**
         * Sets the {@code status} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code status} to set
         * @return a reference to this Builder
         */
        public Builder status(final int val) {
            status = val;
            return this;
        }

        /**
         * Sets the {@code tech} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code tech} to set
         * @return a reference to this Builder
         */
        public Builder tech(final String val) {
            tech = val;
            return this;
        }

        /**
         * Sets the {@code temperature} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code temperature} to set
         * @return a reference to this Builder
         */
        public Builder temperature(final int val) {
            temperature = val;
            return this;
        }

        /**
         * Sets the {@code voltage} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code voltage} to set
         * @return a reference to this Builder
         */
        public Builder voltage(final int val) {
            voltage = val;
            return this;
        }

        /**
         * Returns a {@link BatteryDTO} built from the parameters previously set.
         *
         * @return a {@link BatteryDTO} built with parameters of this {@code BatteryDTO.Builder}
         */
        public BatteryDTO build() { return new BatteryDTO(this); }
    }
}
