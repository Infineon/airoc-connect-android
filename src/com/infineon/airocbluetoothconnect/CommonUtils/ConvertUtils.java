/*
 * Copyright 2014-2023, Cypress Semiconductor Corporation (an Infineon company) or
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

package com.infineon.airocbluetoothconnect.CommonUtils;

public class ConvertUtils {

    public static int byteToIntUnsigned(byte b) {
        /**
         * AND with 0xFF to prevent returning negative value
         */
        return b & 0xFF;
    }

    public static long intToLongUnsigned(int i) {
        return i & 0xFFFFFFFFl;
    }

    public static long byteArrayToLongLittleEndian(byte[] bytes) {
        return intToLongUnsigned(byteArrayToIntLittleEndian(bytes));
    }

    public static int byteArrayToIntLittleEndian(byte[] bytes) {
        return byteArrayToIntLittleEndian(bytes, 0, bytes.length);
    }

    public static int byteArrayToIntLittleEndian(byte[] bytes, int offset, int length) {
        int value = 0;
        if (offset >= 0 && length > 0 && offset + length <= bytes.length) {
            for (int i = offset + length - 1; i >= offset; i--) {
                value <<= 8;
                value += (byteToIntUnsigned(bytes[i]));
            }
        }
        return value;
    }

    public static byte[] hexStringToByteArrayLittleEndian(String string, int offset, int length) {
        return hexStringToByteArray(string, offset, length, length, true);
    }

    public static byte[] hexStringToByteArrayLittleEndian(String string, int offset, int length, int expectedLength) {
        return hexStringToByteArray(string, offset, length, expectedLength, true);
    }

    public static byte[] hexStringToByteArrayBigEndian(String string, int offset, int length) {
        return hexStringToByteArray(string, offset, length, length, false);
    }

    public static byte[] hexStringToByteArrayBigEndian(String string, int offset, int length, int expectedLength) {
        return hexStringToByteArray(string, offset, length, expectedLength, false);
    }

    private static byte[] hexStringToByteArray(String string, int offset, int length, int expectedLength, boolean isLittleEndian) {
        boolean isOddNumChars = length % 2 == 1;
        int numBytes = length / 2 + length % 2;
        int expectedNumBytes = expectedLength / 2 + expectedLength % 2;

        int maxNumBytes = Math.max(numBytes, expectedNumBytes);
        byte[] bytes = new byte[maxNumBytes];

        for (int i = 0, n = numBytes; i < n; i++) {
            int idx;
            if (isLittleEndian) {
                idx = i;
            } else {
                idx = n - 1 - i;
            }

            boolean isHalfByte;
            if (isLittleEndian) {
                isHalfByte = i == n - 1 & isOddNumChars;
            } else {
                isHalfByte = i == 0 & isOddNumChars;
            }

            byte b = hexStringToByte(string, offset, isHalfByte);

            if (isLittleEndian && isHalfByte) {
                b <<= 4;
            }
            bytes[idx] = b;

            if (isHalfByte) {
                offset++;
            } else {
                offset += 2;
            }
        }

        if (expectedNumBytes < numBytes) {
            byte[] tmp = new byte[expectedNumBytes];
            System.arraycopy(bytes, 0, tmp, 0, tmp.length);
            bytes = tmp;
        }

        return bytes;
    }

    public static byte hexStringToByte(String string, int offset, boolean isHalfByte) {
        String s = string.substring(offset, offset + (isHalfByte ? 1 : 2));
        byte value = 0;
        for (int i = 0; i < s.length(); i++) {
            value <<= 4;
            value += charToByte(s.charAt(i));
        }
        return value;
    }

    public static byte charToByte(char value) {
        if ('0' <= value && value <= '9')
            return (byte) (value - '0');
        if ('a' <= value && value <= 'f')
            return (byte) (10 + value - 'a');
        if ('A' <= value && value <= 'F')
            return (byte) (10 + value - 'A');
        return 0;
    }

    public static byte[] byteArraySubset(byte[] bytes, int offset, int length) {
        byte[] subset;
        if (offset >= 0 && length >= 0 && offset + length <= bytes.length) {
            subset = new byte[length];
            System.arraycopy(bytes, offset, subset, 0, length);
        } else {
            subset = new byte[0];
        }
        return subset;
    }

    public static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value >> (i * 8));
        }
        return bytes;
    }

    /**
     * Byte swap a single int value.
     *
     * @param value Value to byte swap.
     * @return Byte swapped representation.
     */
    public static int swapShort(int value) {
        int b1 = (value >> 0) & 0xff;
        int b2 = (value >> 8) & 0xff;
        return 0xFFFF & (b1 << 8 | b2 << 0);
    }
}
