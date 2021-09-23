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

package com.cypress.cysmart.wearable.parser;

import com.cypress.cysmart.wearable.Const;

import java.nio.ByteBuffer;

import static com.cypress.cysmart.wearable.parser.MotionFeatureParser.MotionFeature;

public class MotionDataParser {

    private static final int NUM_BYTES = 48;

    public static MotionData parse(byte[] b) {
        ByteBuffer bb = (ByteBuffer) ByteBuffer.allocate(NUM_BYTES).order(Const.BYTE_ORDER).put(b).rewind();
        MotionFeature f = MotionFeatureParser.parse(bb);
        short accX = f.mIsAcc ? bb.getShort() : Short.MIN_VALUE;
        short accY = f.mIsAcc ? bb.getShort() : Short.MIN_VALUE;
        short accZ = f.mIsAcc ? bb.getShort() : Short.MIN_VALUE;
        short magX = f.mIsMag ? bb.getShort() : Short.MIN_VALUE;
        short magY = f.mIsMag ? bb.getShort() : Short.MIN_VALUE;
        short magZ = f.mIsMag ? bb.getShort() : Short.MIN_VALUE;
        short gyrX = f.mIsGyr ? bb.getShort() : Short.MIN_VALUE;
        short gyrY = f.mIsGyr ? bb.getShort() : Short.MIN_VALUE;
        short gyrZ = f.mIsGyr ? bb.getShort() : Short.MIN_VALUE;
        float roll = f.mIsOrientation ? bb.getFloat() : Float.MIN_VALUE;
        float yaw = f.mIsOrientation ? bb.getFloat() : Float.MIN_VALUE;
        float pitch = f.mIsOrientation ? bb.getFloat() : Float.MIN_VALUE;
        int steps = f.mIsSteps ? bb.getShort() : Integer.MIN_VALUE;
        short lastSyncHrs = bb.get(); // TODO
        int calories = f.mIsCalories ? bb.getShort() : Integer.MIN_VALUE;
        long sleep = f.mIsSleep ? bb.getInt() : Long.MIN_VALUE;
        int duration = f.mIsDuration ? bb.getShort() : Short.MIN_VALUE;
        int distance = f.mIsDistance ? bb.getShort() : Short.MIN_VALUE;
        int speed = f.mIsSpeed ? bb.getShort() : Short.MIN_VALUE;
        short floors = f.mIsFloors ? bb.get() : Short.MIN_VALUE;

        return new MotionData(accX, accY, accZ, magX, magY, magZ, gyrX, gyrY, gyrZ, roll, yaw, pitch, steps,
                lastSyncHrs, calories, sleep, duration, distance, speed, floors);
    }

    // TODO: store uint8 in byte
    public static class MotionData {

        public final short mAccX;
        public final short mAccY;
        public final short mAccZ;
        public final short mMagX;
        public final short mMagY;
        public final short mMagZ;
        public final short mGyrX;
        public final short mGyrY;
        public final short mGyrZ;
        public final float mRoll;
        public final float mYaw;
        public final float mPitch;
        public final int mSteps;
        public final short mLastSyncHrs;
        public final int mCalories;
        public final long mSleep;
        public final int mDuration;
        public final int mDistance;
        public final int mSpeed;
        public final short mFloors;

        public MotionData(short accX, short accY, short accZ, short magX, short magY, short magZ, short gyrX,
                          short gyrY, short gyrZ, float roll, float yaw, float pitch, int steps, short lastSyncHrs,
                          int calories, long sleep, int duration, int distance, int speed, short floors) {
            this.mAccX = accX;
            this.mAccY = accY;
            this.mAccZ = accZ;
            this.mMagX = magX;
            this.mMagY = magY;
            this.mMagZ = magZ;
            this.mGyrX = gyrX;
            this.mGyrY = gyrY;
            this.mGyrZ = gyrZ;
            this.mRoll = roll;
            this.mYaw = yaw;
            this.mPitch = pitch;
            this.mSteps = steps;
            this.mLastSyncHrs = lastSyncHrs;
            this.mCalories = calories;
            this.mSleep = sleep;
            this.mDuration = duration;
            this.mDistance = distance;
            this.mSpeed = speed;
            this.mFloors = floors;
        }
    }
}
