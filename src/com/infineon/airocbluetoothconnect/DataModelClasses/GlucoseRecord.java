/*
 * Copyright 2014-2022, Cypress Semiconductor Corporation (an Infineon company) or
 * an affiliate of Cypress Semiconductor Corporation.  All rights reserved.
 *
 * This software, including source code, documentation and related
 * materials ("Software") is owned by Cypress Semiconductor Corporation
 * or one of its affiliates ("Cypress") and is protected by and subject to
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

package com.infineon.airocbluetoothconnect.DataModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

public class GlucoseRecord implements Parcelable {
    public static final int UNIT_kgpl = 0;
    public static final int UNIT_molpl = 1;

    /**
     * Record sequence number
     */
    public int sequenceNumber;
    /**
     * The base time of the measurement
     */
    public String time;
    /**
     * Time offset of the record
     */
    public int timeOffset;
    /**
     * The glucose concentration. 0 if not present
     */
    public float glucoseConcentration;
    /**
     * Concentration unit. One of the following: {@link GlucoseRecord#UNIT_kgpl}, {@link GlucoseRecord#UNIT_molpl}
     */
    public int unit;
    /**
     * The type of the record. 0 if not present
     */
    public String type;
    /**
     * The sample location. 0 if unknown
     */
    public String sampleLocation;
    /**
     * Sensor status annunciation flags. 0 if not present
     */
    public int status;

    public GlucoseRecord(Parcel in) {
        super();
        readFromParcel(in);
    }

    public boolean context;

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

    public static final Parcelable.Creator<GlucoseRecord> CREATOR = new Parcelable.Creator<GlucoseRecord>() {
        public GlucoseRecord createFromParcel(Parcel in) {
            return new GlucoseRecord(in);
        }

        public GlucoseRecord[] newArray(int size) {

            return new GlucoseRecord[size];
        }

    };

    public void readFromParcel(Parcel in) {
        sequenceNumber = in.readInt();
        time = in.readString();
        timeOffset = in.readInt();
        glucoseConcentration = in.readFloat();
        unit = in.readInt();
        type = in.readString();
        sampleLocation = in.readString();
        status = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(sequenceNumber);
        parcel.writeString(time);
        parcel.writeInt(timeOffset);
        parcel.writeFloat(glucoseConcentration);
        parcel.writeInt(unit);
        parcel.writeString(type);
        parcel.writeString(sampleLocation);
        parcel.writeInt(status);
    }

}
