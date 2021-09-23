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

package com.cypress.cysmart.RDKEmulatorView;


import java.util.HashMap;

/**
 * Report and Report Reference class for the states of the Report
 */
public class ReportAttributes {

    //Report References id
    public static String MOUSE_REPORT_REFERENCE = "1";
    public static final String MOUSE_REPORT_REFERENCE_STRING = "Report ID: MOUSE_REPORT_REFERENCE";
    public static String KEYBOARD_REPORT_REFERENCE = "2";
    public static final String KEYBOARD_REPORT_REFERENCE_STRING = "Report ID: KEYBOARD_REPORT_REFERENCE";
    public static String MULTIMEDIA_REPORT_REFERENCE = "3";
    public static final String MULTIMEDIA_REPORT_REFERENCE_STRING = "Report ID: MULTIMEDIA_REPORT_REFERENCE";
    public static String POWER_REPORT_REFERENCE = "4";
    public static final String POWER_REPORT_REFERENCE_STRING = "Report ID: POWER_REPORT_REFERENCE";
    public static String AUDIO_REPORT_REFERENCE_CONTROL = "31";
    public static final String AUDIO_REPORT_REFERENCE_CONTROL_STRING = "Report ID: AUDIO_REPORT_REFERENCE_CONTROL";
    public static String AUDIO_REPORT_REFERENCE_DATA = "30";
    public static final String AUDIO_REPORT_REFERENCE_DATA_STRING = "Report ID: AUDIO_REPORT_REFERENCE_DATA";

    //Report Reference Types
    public static String INPUT_REPORT_TYPE = "1";
    public static final String INPUT_REPORT_TYPE_STRING = "Report Type: Input Report";
    public static String OUTPUT_REPORT_TYPE = "2";
    public static final String OUTPUT_REPORT_TYPE_STRING = "Report Type: Output Report";
    public static String FEATURE_REPORT_TYPE = "3";
    public static final String FEATURE_REPORT_TYPE_STRING = "Report Type: Feature Report";

    //Report Values String Compare
    public static String POWER = "3000";
    public static String VOLUME_PLUS = "e900";
    public static String VOLUME_MINUS = "ea00";
    public static String CHANNEL_PLUS = "9c00";
    public static String CHANNEL_MINUS = "9d00";
    public static String MICROPHONE = "ff01";
    public static String MICROPHONE_UP = "ff00";
    public static String MICROPHONE_SYNC = "FE";
    public static String RETURN = "00009e0000000000";
    public static String RETURN_UP = "0000000000000000";
    public static String SOURCE = "8700";
    public static String LEFT_CLICK_DOWN = "0100000000";
    public static String LEFT_RIGHT_CLICK_UP = "0000000000";
    public static String RIGHT_CLICK_DOWN = "0200000000";
    public static String GESTURE_ON = "0000FF0000";

    //Report Refernce Defalut values
    public static String REPORT_REF_ID = "Report Reference ID not found";
    public static String REPORT_TYPE = "Report Type not found";


    private static HashMap<String, String> mReferenceAttributes = new HashMap<String, String>();
    private static HashMap<String, String> mReferenceAttributesType = new HashMap<String, String>();
    private static HashMap<String, Integer> mReportvalues = new HashMap<String, Integer>();

    static {

        mReferenceAttributes.put(MOUSE_REPORT_REFERENCE, MOUSE_REPORT_REFERENCE_STRING);
        mReferenceAttributes.put(KEYBOARD_REPORT_REFERENCE, KEYBOARD_REPORT_REFERENCE_STRING);
        mReferenceAttributes.put(MULTIMEDIA_REPORT_REFERENCE, MULTIMEDIA_REPORT_REFERENCE_STRING);
        mReferenceAttributes.put(POWER_REPORT_REFERENCE, POWER_REPORT_REFERENCE_STRING);
        mReferenceAttributes.put(AUDIO_REPORT_REFERENCE_CONTROL, AUDIO_REPORT_REFERENCE_CONTROL_STRING);
        mReferenceAttributes.put(AUDIO_REPORT_REFERENCE_DATA, AUDIO_REPORT_REFERENCE_DATA_STRING);

        mReferenceAttributesType.put(INPUT_REPORT_TYPE, INPUT_REPORT_TYPE_STRING);
        mReferenceAttributesType.put(OUTPUT_REPORT_TYPE, OUTPUT_REPORT_TYPE_STRING);
        mReferenceAttributesType.put(FEATURE_REPORT_TYPE, FEATURE_REPORT_TYPE_STRING);

        mReportvalues.put(POWER, 101);
        mReportvalues.put(VOLUME_PLUS, 102);
        mReportvalues.put(VOLUME_MINUS, 103);
        mReportvalues.put(CHANNEL_PLUS, 104);
        mReportvalues.put(CHANNEL_MINUS, 105);
        mReportvalues.put(MICROPHONE, 106);
        mReportvalues.put(LEFT_CLICK_DOWN, 107);
        mReportvalues.put(RIGHT_CLICK_DOWN, 108);
        mReportvalues.put(RETURN, 109);
        mReportvalues.put(SOURCE, 110);
        mReportvalues.put(MICROPHONE_UP, 201);
        mReportvalues.put(LEFT_RIGHT_CLICK_UP, 202);
        mReportvalues.put(RETURN_UP, 203);

    }

    public static String lookupReportReferenceID(String reference) {
        String name = mReferenceAttributes.get(reference);
        return name == null ? "" + reference : name;
    }

    public static String lookupReportReferenceType(String referenceType) {
        String name = mReferenceAttributesType.get(referenceType);
        return name == null ? "Reserved for future use" : name;
    }

    public static int lookupReportValues(String reportValue) {
        int returnValueDefault = 0;
        Integer value = mReportvalues.get(reportValue);
        if (value != null) {
            return value;
        } else {
            return returnValueDefault;
        }

    }
}
