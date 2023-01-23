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

package com.infineon.airocbluetoothconnect.wearable.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.infineon.airocbluetoothconnect.wearable.Const;

import java.nio.ByteOrder;

public class Utilities {

    public static long getLong(byte[] b, int n) {
        return getLong(b, n, Const.BYTE_ORDER);
    }

    private static long getLong(byte[] b, int n, ByteOrder order) {
        long v = 0;
        for (int i = 0; i < n; i++) {
            int x = order == ByteOrder.LITTLE_ENDIAN ? i : n - 1 - i;
            v |= ((long) b[x] << (i * 8) & (0xFFl << (i * 8)));
        }
        return v;
    }

    public static final class BitField {

        private long mBits;
        private int mPos;

        public BitField(long bits) {
            this.mBits = bits;
        }

        public boolean isSet() {
            return ((mBits >> mPos++) & 1) != 0;
        }

        public long getNumber(int numBits) {
            int mask = (int) (Math.pow(2, numBits) - 1);
            long res = (mBits >> mPos) & mask;
            mPos += numBits;
            return res;
        }
    }

    public static boolean isHardwareKeyboardAvailable(Context context) {
        return context.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
    }
}
