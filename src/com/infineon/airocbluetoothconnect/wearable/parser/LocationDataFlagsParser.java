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

public class LocationDataFlagsParser {

    public enum PositionStatus {
        NO_POSITION, POSITION_OK, ESTIMATED_POSITION, LAST_KNOWN_POSITION;

        public static PositionStatus parse(int value) {
            return PositionStatus.values()[value];
        }
    }

    public enum SpeedAndDistanceFormat {
        _2D, _3D;

        public static SpeedAndDistanceFormat parse(int value) {
            return SpeedAndDistanceFormat.values()[value];
        }
    }

    public enum ElevationSource {
        POSITIONING_SYSTEM, BAROMETRIC_AIR_PRESSURE, DATABASE_SERVICE, OTHER;

        public static ElevationSource parse(int value) {
            return ElevationSource.values()[value];
        }
    }

    public enum HeadingSource {
        HEADING_BASED_ON_MAGNETIC_COMPASS, HEADING_BASED_ON_MOVEMENT;

        public static HeadingSource parse(int value) {
            return HeadingSource.values()[value];
        }
    }

    private static final int NUM_BYTES = 2;

    public static LocationDataFlags parse(byte[] b) {
        return parse((ByteBuffer) ByteBuffer.allocate(NUM_BYTES).order(Const.BYTE_ORDER).put(b, 0, NUM_BYTES).rewind());
    }

    // consumes initial 2 bytes
    public static LocationDataFlags parse(ByteBuffer bb) {
        Utilities.BitField bf = new Utilities.BitField(bb.getShort());
        return new LocationDataFlags(bf.isSet(), bf.isSet(), bf.isSet(), bf.isSet(), bf.isSet(), bf.isSet(), bf.isSet(),
                PositionStatus.parse((int) bf.getNumber(2)),
                SpeedAndDistanceFormat.parse((int) bf.getNumber(1)),
                ElevationSource.parse((int) bf.getNumber(1)),
                HeadingSource.parse((int) bf.getNumber(2)));
    }

    public static class LocationDataFlags {

        public final boolean mIsInstantaneousSpeed;
        public final boolean mIsTotalDistance;
        public final boolean mIsLocation;
        public final boolean mIsElevation;
        public final boolean mIsHeading;
        public final boolean mIsRollingTime;
        public final boolean mIsUtcTime;
        public final PositionStatus mPositionStatus;
        public final SpeedAndDistanceFormat mSpeedAndDistanceFormat;
        public final ElevationSource mElevationSource;
        public final HeadingSource mHeadingSource;

        public LocationDataFlags(boolean isInstantaneousSpeed, boolean isTotalDistance, boolean isLocation, boolean isElevation, boolean isHeading, boolean isRollingTime, boolean isUtcTime, PositionStatus positionStatus, SpeedAndDistanceFormat speedAndDistanceFormat, ElevationSource elevationSource, HeadingSource headingSource) {
            this.mIsInstantaneousSpeed = isInstantaneousSpeed;
            this.mIsTotalDistance = isTotalDistance;
            this.mIsLocation = isLocation;
            this.mIsElevation = isElevation;
            this.mIsHeading = isHeading;
            this.mIsRollingTime = isRollingTime;
            this.mIsUtcTime = isUtcTime;
            this.mPositionStatus = positionStatus;
            this.mSpeedAndDistanceFormat = speedAndDistanceFormat;
            this.mElevationSource = elevationSource;
            this.mHeadingSource = headingSource;
        }
    }
}
