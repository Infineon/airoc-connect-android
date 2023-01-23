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

package com.infineon.airocbluetoothconnect.wearable.parser;

import com.infineon.airocbluetoothconnect.wearable.Const;
import com.infineon.airocbluetoothconnect.wearable.utils.Utilities;

import java.nio.ByteBuffer;

public class MotionFeatureParser {

    private static final int NUM_BYTES = 2;

    public static MotionFeature parse(byte[] b) {
        return parse((ByteBuffer) ByteBuffer.allocate(NUM_BYTES).order(Const.BYTE_ORDER).put(b, 0, NUM_BYTES).rewind());
    }

    public static MotionFeature parse(ByteBuffer bb) {
        Utilities.BitField s = new Utilities.BitField(bb.getShort());
        return new MotionFeature(s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet(), s.isSet());
    }

    public static class MotionFeature {

        public final boolean mIsAcc;
        public final boolean mIsMag;
        public final boolean mIsGyr;
        public final boolean mIsOrientation;
        public final boolean mIsSteps;
        public final boolean mIsCalories;
        public final boolean mIsSleep;
        public final boolean mIsDuration;
        public final boolean mIsDistance;
        public final boolean mIsSpeed;
        public final boolean mIsFloors;

        public MotionFeature(boolean isAcc, boolean isMag, boolean isGyr, boolean isOrientation, boolean isSteps,
                             boolean isCalories, boolean isSleep, boolean isDuration, boolean isDistance, boolean isSpeed, boolean isFloors) {
            this.mIsAcc = isAcc;
            this.mIsMag = isMag;
            this.mIsGyr = isGyr;
            this.mIsOrientation = isOrientation;
            this.mIsSteps = isSteps;
            this.mIsCalories = isCalories;
            this.mIsSleep = isSleep;
            this.mIsDuration = isDuration;
            this.mIsDistance = isDistance;
            this.mIsSpeed = isSpeed;
            this.mIsFloors = isFloors;
        }
    }
}
