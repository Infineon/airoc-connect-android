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

package com.cypress.cysmart.BLEProfileDataParserClasses;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse Red/Green/Blue/Alpha
 */
public class RGBParser {

    private static final Pattern PATTERN = Pattern.compile("(\\d+),(\\d+),(\\d+),(\\d+)");

    /**
     * Parse RGBA value from the characteristic
     *
     * @param characteristic
     * @return {@link String}
     */
    public static String getRGBAString(BluetoothGattCharacteristic characteristic) {
        int red = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        int green = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        int blue = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 2);
        int intensity = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 3);
        return String.valueOf(red + "," + green + "," + blue + "," + intensity);
    }

    /**
     * Parse string returned by the {@link #getRGBAString}
     *
     * @param string
     * @return RGBA as uint32
     */
    public static int parseRGBAString(String string) {
        Matcher m = PATTERN.matcher(string);
        if (m.matches()) {
            int red = Integer.parseInt(m.group(1), 10);
            int green = Integer.parseInt(m.group(2), 10);
            int blue = Integer.parseInt(m.group(3), 10);
            int alpha = Integer.parseInt(m.group(4), 10);
            return ((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | (alpha & 0xFF);
        }
        return 0;
    }

    public static int red(int rgba) {
        return rgba >>> 24;
    }

    public static int green(int rgba) {
        return (rgba >> 16) & 0xFF;
    }

    public static int blue(int rgba) {
        return (rgba >> 8) & 0xFF;
    }

    public static int alpha(int rgba) {
        return rgba & 0xFF;
    }
}
