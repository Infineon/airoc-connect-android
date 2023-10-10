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

public class CheckSumUtils {

    /* Checksum type is a basic inverted summation of all bytes */
    public final static int SUM_CHECKSUM = 0x00;
    /* 16-bit CRC checksum using the CCITT implementation */
    public final static int CRC_CHECKSUM = 0x01;

    /**
     * Method to calculate 2's complement
     */
    public static int calculateCheckSum2(int checkSumType, byte[] data, int dataLength) {
        int checkSum = 0;
        if (checkSumType == CRC_CHECKSUM) {
            return CRC16.crc16(data, dataLength);
        } else {
            while (dataLength-- > 0) {
                checkSum += ConvertUtils.byteToIntUnsigned(data[dataLength]);
            }
        }
        return 1 + (~checkSum);
    }

    public static long crc32(byte[] buf, long size) {
        return CRC32.crc32(buf, size);
    }

    /**
     * MMethod to calculate 2's complement in Verify row command
     *
     * @param datalen
     * @param data
     * @return
     */
    public static int calculateChecksumVerifyRow(int datalen, byte[] data) {
        int checkSum = 0;
        while (datalen-- > 0) {
            /**
             * AND each value with 0xFF to remove the negative value for summation
             */
            checkSum += (data[datalen] & 0xFF);
        }
        return checkSum;
    }

    private static class CRC16 {

//        private static final int[] table = {
//                0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
//                0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
//                0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
//                0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
//                0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
//                0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
//                0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
//                0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
//                0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
//                0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
//                0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
//                0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
//                0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
//                0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
//                0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
//                0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
//                0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
//                0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
//                0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
//                0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
//                0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
//                0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
//                0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
//                0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
//                0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
//                0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
//                0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
//                0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
//                0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
//                0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
//                0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
//                0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
//        };

//        /**
//         * CRC checkSum Method
//         *
//         * @param data
//         * @return
//         */
//        private static int crc16(int dataLength, byte[] data) {
//            int crc = 0x0000;
//            for (int i = 0; i < dataLength; i++) {
//                byte b = data[i];
//                /**
//                 * AND each value with 0xFF to remove the negative value for summation
//                 */
//                crc = (crc >>> 8) ^ table[((crc ^ b) & 0xff)];
//            }
//            return crc;
//        }

        /**
         * CRC checkSum Method
         *
         * @param data
         * @return
         */
        private static int crc16(byte[] data, int dataLength) {
            int crc = 0xffff;
            int i, tmp;
            if (dataLength == 0) {
                return (~crc);
            }

            for (int h = 0; h < dataLength; ++h) {
                for (i = 0, tmp = (0x00ff & data[h]); i < 8; i++, tmp >>= 1) {
                    if (((crc & 0x0001) ^ (tmp & 0x0001)) > 0) {
                        crc = (crc >> 1) ^ 0x8408;
                    } else {
                        crc >>= 1;
                    }
                }
            }

            crc = ~crc;
            tmp = crc;

            crc = (crc << 8) | (tmp >> 8 & 0xFF);
            crc = 0xffff & crc;

            return crc;
        }
    }

    private static class CRC32 {

        private static final long g0 = 0x82F63B78l;
        private static final long g1 = (g0 >> 1) & 0x7FFFFFFFl;
        private static final long g2 = (g0 >> 2) & 0x3FFFFFFFl;
        private static final long g3 = (g0 >> 3) & 0x1FFFFFFFl;

        private static final long table[] = {
                0, g3, g2, (g2 ^ g3),
                g1, (g1 ^ g3), (g1 ^ g2), (g1 ^ g2 ^ g3),
                g0, (g0 ^ g3), (g0 ^ g2), (g0 ^ g2 ^ g3),
                (g0 ^ g1), (g0 ^ g1 ^ g3), (g0 ^ g1 ^ g2), (g0 ^ g1 ^ g2 ^ g3),
        };

        private static long crc32(byte[] buf, long size) {
            int pos = 0;
            long crc = 0xFFFFFFFFl;
            while (size != 0) {
                int i;
                --size;
                crc = crc ^ (buf[pos] & 0xFFl);
                ++pos;
                for (i = 1; i >= 0; i--) {
                    crc = (crc >> 4) ^ table[(int) (crc & 0xFl)];
                }
            }
            return (~crc) & 0xFFFFFFFFl;
        }
    }
}
