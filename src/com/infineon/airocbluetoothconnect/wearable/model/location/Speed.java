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

package com.infineon.airocbluetoothconnect.wearable.model.location;

import com.infineon.airocbluetoothconnect.wearable.model.ValueWithUnit;

// Unit is in meters per second with a resolution of 1/100
public class Speed extends ValueWithUnit<Speed, Speed.Unit> {

    private static final Unit DEFAULT_UNIT = Unit.METERS_PER_SECOND;
    private static final double EXPONENT = -2;
    private static final double MILES_IN_1_KM = 0.621371;
    private static final double M_IN_1_KM = 1000;
    private static final double SEC_IN_1_HR = 3600;

    public static class Unit extends ValueWithUnit.Unit {

        public static final Unit METERS_PER_SECOND = new Unit("m/s");
        public static final Unit KILOMETERS_PER_HOUR = new Unit("km/h");
        public static final Unit MILES_PER_HOUR = new Unit("miles/h");
        private static final Unit[] ALL_UNITS = {METERS_PER_SECOND, KILOMETERS_PER_HOUR, MILES_PER_HOUR};

        private Unit(String text) {
            super(text);
        }
    }

    public Speed() {
        super(DEFAULT_UNIT);
    }

    @Override
    public Unit[] getSupportedUnits() {
        return Unit.ALL_UNITS;
    }

    @Override
    protected double convert(double value, Unit from, Unit to) {
        if (from == Unit.METERS_PER_SECOND) {
            if (to == Unit.KILOMETERS_PER_HOUR) {
                value *= SEC_IN_1_HR / M_IN_1_KM;
            }
            // to miles/h
            else {
                // first convert m/s to km/h...
                value = convert(value, from, Unit.KILOMETERS_PER_HOUR);
                // ...then convert km/h to miles/h
                value = convert(value, Unit.KILOMETERS_PER_HOUR, to);
            }
        } else if (from == Unit.KILOMETERS_PER_HOUR) {
            if (to == Unit.METERS_PER_SECOND) {
                value *= M_IN_1_KM / SEC_IN_1_HR;
            }
            // to miles/h
            else {
                value *= MILES_IN_1_KM;
            }
        }
        // from miles/h
        else {
            if (to == Unit.METERS_PER_SECOND) {
                // first convert miles/h to km/h...
                value = convert(value, from, Unit.KILOMETERS_PER_HOUR);
                // ...then convert km/h to m/s
                value = convert(value, Unit.KILOMETERS_PER_HOUR, to);
            }
            // to km/h
            else {
                value /= MILES_IN_1_KM;
            }
        }
        return value;
    }

    @Override
    protected Unit getDefaultUnit() {
        return DEFAULT_UNIT;
    }

    @Override
    protected double getExponent() {
        return EXPONENT;
    }
}
