/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.infineon.airocbluetoothconnect.DataModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class GlucoseContextRecord implements Parcelable{

    public static final int UNIT_kg = 0;
    public static final int UNIT_l = 1;

    public int sequenceNumber;

    /**
     * One of the following:<br/>
     * 0 Not present<br/>
     * 1 Breakfast<br/>
     * 2 Lunch<br/>
     * 3 Dinner<br/>
     * 4 Snack<br/>
     * 5 Drink<br/>
     * 6 Supper<br/>
     * 7 Brunch
     */
    public String carbohydrateId;
    /**
     * Number of kilograms of carbohydrate
     */
    public String carbohydrateUnits;
    /**
     * One of the following:<br/>
     * 0 Not present<br/>
     * 1 Preprandial (before meal)<br/>
     * 2 Postprandial (after meal)<br/>
     * 3 Fasting<br/>
     * 4 Casual (snacks, drinks, etc.)<br/>
     * 5 Bedtime
     */
    public String meal;
    /**
     * One of the following:<br/>
     * 0 Not present<br/>
     * 1 Self<br/>
     * 2 Health Care Professional<br/>
     * 3 Lab test<br/>
     * 15 Tester value not available
     */
    public String tester;
    /**
     * One of the following:<br/>
     * 0 Not present<br/>
     * 1 Minor health issues<br/>
     * 2 Major health issues<br/>
     * 3 During menses<br/>
     * 4 Under stress<br/>
     * 5 No health issues<br/>
     * 15 Tester value not available
     */
    public String health;
    /**
     * Exercise duration in seconds. 0 if not present
     */
    public String exerciseDuration;
    /**
     * Exercise intensity in percent. 0 if not present
     */
    public String exerciseIntensity;
    /**
     * One of the following:<br/>
     * 0 Not present<br/>
     * 1 Rapid acting insulin<br/>
     * 2 Short acting insulin<br/>
     * 3 Intermediate acting insulin<br/>
     * 4 Long acting insulin<br/>
     * 5 Pre-mixed insulin
     */
    public String medicationId;
    /**
     * Quantity of medication. See {@link #medicationUnit} for the unit.
     */
    public float medicationQuantity;

    public String medicationUnit;
    /**
     * HbA1c value. 0 if not present
     */
    public String HbA1c;


    public GlucoseContextRecord(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<GlucoseContextRecord> CREATOR = new Parcelable.Creator<GlucoseContextRecord>() {
        public GlucoseContextRecord createFromParcel(Parcel in) {
            return new GlucoseContextRecord(in);
        }

        public GlucoseContextRecord[] newArray(int size) {
            return new GlucoseContextRecord[size];
        }

    };

    public void readFromParcel(Parcel in) {
        sequenceNumber = in.readInt();
        carbohydrateId = in.readString();
        carbohydrateUnits = in.readString();
        meal = in.readString();
        tester = in.readString();
        health = in.readString();
        exerciseDuration = in.readString();
        exerciseIntensity = in.readString();
        medicationId = in.readString();
        medicationQuantity = in.readFloat();
        medicationUnit = in.readString();
        HbA1c = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sequenceNumber);
        parcel.writeString(carbohydrateId);
        parcel.writeString(carbohydrateUnits);
        parcel.writeString(meal);
        parcel.writeString(tester);
        parcel.writeString(health);
        parcel.writeString(exerciseDuration);
        parcel.writeString(exerciseIntensity);
        parcel.writeString(medicationId);
        parcel.writeFloat(medicationQuantity);
        parcel.writeString(medicationUnit);
        parcel.writeString(HbA1c);
    }

}

