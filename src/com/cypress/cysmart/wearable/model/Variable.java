/*
 * (c) 2014-2020, Cypress Semiconductor Corporation or a subsidiary of 
 * Cypress Semiconductor Corporation.  All rights reserved.
 * 
 * This software, including source code, documentation and related 
 * materials ("Software"),  is owned by Cypress Semiconductor Corporation 
 * or one of its subsidiaries ("Cypress") and is protected by and subject to 
 * worldwide patent protection (United States and foreign), 
 * United States copyright laws and international treaty provisions.  
 * Therefore, you may use this Software only as provided in the license 
 * agreement accompanying the software package from which you 
 * obtained this Software ("EULA").
 * If no EULA applies, Cypress hereby grants you a personal, non-exclusive, 
 * non-transferable license to copy, modify, and compile the Software 
 * source code solely for use in connection with Cypress's 
 * integrated circuit products.  Any reproduction, modification, translation, 
 * compilation, or representation of this Software except as specified 
 * above is prohibited without the express written permission of Cypress.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, NONINFRINGEMENT, IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Cypress 
 * reserves the right to make changes to the Software without notice. Cypress 
 * does not assume any liability arising out of the application or use of the 
 * Software or any product or circuit described in the Software. Cypress does 
 * not authorize its products for use in any products where a malfunction or 
 * failure of the Cypress product may reasonably be expected to result in 
 * significant property damage, injury or death ("High Risk Product"). By 
 * including Cypress's product in a High Risk Product, the manufacturer 
 * of such system or application assumes all risk of such use and in doing 
 * so agrees to indemnify Cypress against all liability.
 */

package com.cypress.cysmart.wearable.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// TODO: immutability
public class Variable<V extends ValueWithUnit<V, U>, U extends ValueWithUnit.Unit> {

    public enum Id {

        ACT_STEPS("act.steps"),
        ACT_DURATION("act.dura"),
        ACT_CALORIES("act.calo"),
        ACT_DISTANCE("act.dist"),
        ACT_SPEED("act.speed"),
        ACT_FLOORS("act.floor"),
        ACT_SLEEP("act.sleep"),
        ENV_TEMPERATURE("env.temp"),
        ENV_UV("env.uv"),
        ENV_AIR_QUALITY("env.airq"),
        ENV_PRESSURE("env.pres"),
        ENV_ALTITUDE("env.altd"),
        ENV_MAGNETIC_DECLINATION("env.magnetic_declination"),
        //--
        LOC_TOTAL_DISTANCE("loc.total_distance"),
        LOC_HEADING("loc.heading"),
        LOC_ROLLING_TIME("loc.rolling_time"),
        LOC_UTC_TIME("loc.utc_time"), // TODO
        //--
        LOC_POSITION("loc.pos"),
        LOC_ALTITUDE("loc.altd"),
        LOC_SPEED("loc.speed"),
        VOICE_RECORD("voice.rec"),
        VOICE_DATA("voice.data"),
        VOICE_TEXT("voice.text"),
        SYS_TIME("sys.time"),
        SYS_BATTERY("sys.batt"),
        SYS_HAPTICS("sys.hap");

        private String mId;

        Id(String id) {
            this.mId = id;
        }

        @Override
        public String toString() {
            return mId;
        }
    }

    protected final PropertyChangeSupport mPropertyChangeSupport = new PropertyChangeSupport(this);
    public Category mCategory;
    public final Id mId;
    public final String mName;

    private V mValueWithUnit;
    public final boolean mHasTarget;
    private final double mMinValue = 0; // TODO
    private double mMaxValue; // TODO

    public Variable(Id id, String name, boolean hasTarget, V valueWithUnit, PropertyChangeListener listener) {
        this.mId = id;
        this.mName = name;
        this.mHasTarget = hasTarget;
        this.mValueWithUnit = valueWithUnit;
        addPropertyChangeListener(listener);
    }

    public double getValue() {
        return mValueWithUnit.getValue();
    }

    public String getValueString() {
        return mValueWithUnit.getValueString();
    }

    public void setUnresolvedValue(double unresolvedValue) {
        if (mValueWithUnit.getUnresolvedValueInDefaultUnit() != unresolvedValue) {
            mValueWithUnit.setUnresolvedValueInDefaultUnit(unresolvedValue);
            mPropertyChangeSupport.firePropertyChange("value", null, null); // TODO: optimize
        }
    }

    public double getMinValue() {
        return mMinValue;
    }

    public double getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(double maxValue) {
        if (this.mMaxValue != maxValue) {
            this.mMaxValue = maxValue;
            mPropertyChangeSupport.firePropertyChange("maxValue", null, null); // TODO: optimize
        }
    }

    public U[] getSupportedUnits() {
        return mValueWithUnit.getSupportedUnits();
    }

    public U getUnit() {
        return mValueWithUnit.getUnit();
    }

    public void setUnit(U unit) {
        if (getUnit() != unit) {
            mValueWithUnit.setUnit(unit);
            mPropertyChangeSupport.firePropertyChange("unit", null, null); // TODO: optimize
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        mPropertyChangeSupport.removePropertyChangeListener(listener);
    }
}
