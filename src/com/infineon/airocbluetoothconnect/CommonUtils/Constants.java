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

/**
 * Constants used in the project
 */
public class Constants {

    // The value of manifest.package in AndroidManifest.xml
    public static String PACKAGE_NAME;

    /**
     * Extras Constants
     */
    public static final String EXTRA_HRM_ENERGY_EXPENDED_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HRM_ENERGY_EXPENDED_VALUE";
    public static final String EXTRA_HRM_RR_INTERVAL_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HRM_RR_INTERVAL_VALUE";
    public static final String EXTRA_HRM_HEART_RATE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HRM_HEART_RATE_VALUE";
    public static final String EXTRA_HRM_SENSOR_CONTACT_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HRM_SENSOR_CONTACT_VALUE";
    public static final String EXTRA_HRM_BODY_SENSOR_LOCATION_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HRM_BODY_SENSOR_LOCATION_VALUE";
    public static final String EXTRA_MANUFACTURER_NAME = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_MANUFACTURER_NAME";
    public static final String EXTRA_MODEL_NUMBER = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_MODEL_NUMBER";
    public static final String EXTRA_SERIAL_NUMBER = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SERIAL_NUMBER";
    public static final String EXTRA_HARDWARE_REVISION = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HARDWARE_REVISION";
    public static final String EXTRA_FIRMWARE_REVISION = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_FIRMWARE_REVISION";
    public static final String EXTRA_SOFTWARE_REVISION = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SOFTWARE_REVISION";
    public static final String EXTRA_PNP_ID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PNP_ID";
    public static final String EXTRA_SYSTEM_ID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SYSTEM_ID";
    public static final String EXTRA_REGULATORY_CERTIFICATION_DATA_LIST = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_REGULATORY_CERTIFICATION_DATA_LIST";
    public static final String EXTRA_HTM_TEMPERATURE_MEASUREMENT_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HTM_TEMPERATURE_MEASUREMENT_VALUE";
    public static final String EXTRA_HTM_TEMPERATURE_TYPE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_HTM_TEMPERATURE_TYPE_VALUE";
    public static final String EXTRA_BTL_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BTL_VALUE";
    public static final String EXTRA_CAPPROX_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_CAPPROX_VALUE";
    public static final String EXTRA_CAPSLIDER_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_CAPSLIDER_VALUE";
    public static final String EXTRA_CAPBUTTONS_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_CAPBUTTONS_VALUE";
    public static final String EXTRA_ALERT_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ALERT_VALUE";
    public static final String EXTRA_POWER_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_POWER_VALUE";
    public static final String EXTRA_RGB_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_RGB_VALUE";
    public static final String EXTRA_GLUCOSE_MEASUREMENT = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_GLUCOSE_MEASUREMENT";
    public static final String EXTRA_RECORD_ACCESS_CONTROL_POINT = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_RECORD_ACCESS_CONTROL_POINT";
    public static final String EXTRA_GLUCOSE_MEASUREMENT_CONTEXT = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_GLUCOSE_MEASUREMENT_CONTEXT";
    public static final String EXTRA_BYTE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_VALUE";
    public static final String EXTRA_BYTE_UUID_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_UUID_VALUE";
    public static final String EXTRA_BYTE_INSTANCE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_INSTANCE_VALUE";
    public static final String EXTRA_BYTE_SERVICE_UUID_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_SERVICE_UUID_VALUE";
    public static final String EXTRA_BYTE_SERVICE_INSTANCE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_SERVICE_INSTANCE_VALUE";
    public static final String EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BYTE_DESCRIPTOR_INSTANCE_VALUE";
    public static final String EXTRA_PRESURE_SYSTOLIC_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PRESURE_SYSTOLIC_VALUE";
    public static final String EXTRA_PRESURE_DIASTOLIC_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PRESURE_DIASTOLIC_VALUE";
    public static final String EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PRESURE_SYSTOLIC_UNIT_VALUE";
    public static final String EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PRESURE_DIASTOLIC_UNIT_VALUE";
    public static final String EXTRA_RSC_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_RSC_VALUE";
    public static final String EXTRA_CSC_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_CSC_VALUE";
    public static final String EXTRA_ACCX_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACCX_VALUE";
    public static final String EXTRA_ACCY_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACCY_VALUE";
    public static final String EXTRA_ACCZ_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACCZ_VALUE";
    public static final String EXTRA_STEMP_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_STEMP_VALUE";
    public static final String EXTRA_SPRESSURE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SPRESSURE_VALUE";
    public static final String EXTRA_ACC_SENSOR_SCAN_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACC_SENSOR_SCAN_VALUE";
    public static final String EXTRA_ACC_SENSOR_TYPE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACC_SENSOR_TYPE_VALUE";
    public static final String EXTRA_ACC_FILTER_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ACC_FILTER_VALUE";
    public static final String EXTRA_STEMP_SENSOR_SCAN_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_STEMP_SENSOR_SCAN_VALUE";
    public static final String EXTRA_STEMP_SENSOR_TYPE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_STEMP_SENSOR_TYPE_VALUE";
    public static final String EXTRA_SPRESSURE_SENSOR_SCAN_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SPRESSURE_SENSOR_SCAN_VALUE";
    public static final String EXTRA_SPRESSURE_SENSOR_TYPE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SPRESSURE_SENSOR_TYPE_VALUE";
    public static final String EXTRA_SPRESSURE_THRESHOLD_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SPRESSURE_THRESHOLD_VALUE";
    public static final String EXTRA_DESCRIPTOR_BYTE_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_BYTE_VALUE";
    public static final String EXTRA_DESCRIPTOR_BYTE_VALUE_UUID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_BYTE_VALUE_UUID";
    public static final String EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID";
    public static final String EXTRA_DESCRIPTOR_VALUE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_VALUE";
    public static final String EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID";
    public static final String EXTRA_DESCRIPTOR_REPORT_REFERENCE_TYPE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_DESCRIPTOR_REPORT_REFERENCE_TYPE";
    public static final String EXTRA_CHARACTERISTIC_ERROR_MESSAGE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_CHARACTERISTIC_ERROR_MESSAGE";
    /**
     * Links
     */
    public static final String LINK_CONTACT_US = "https://www.infineon.com/cms/en/about-infineon/company/contacts/";
    public static final String LINK_BLE_PRODUCTS = "https://www.infineon.com/cms/en/product/wireless-connectivity/airoc-bluetooth-le-bluetooth-multiprotocol/";
    public static final String LINK_CYPRESS_HOME = "https://www.infineon.com/";
    public static final String LINK_CYSMART_MOBILE = "https://www.infineon.com/cms/en/design-support/tools/utilities/wireless-connectivity/airoc-bluetooth-connect-app-mobile-app/";

    /**
     * Descriptor constants
     */
    public static final String FIRST_BIT_KEY_VALUE = "FIRST BIT VALUE KEY";
    public static final String SECOND_BIT_KEY_VALUE = "SECOND BIT VALUE KEY";
    public static final String EXTRA_SILICON_ID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SILICON_ID";
    public static final String EXTRA_SILICON_REV = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SILICON_REV";
    public static final String EXTRA_APP_VALID = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_APP_VALID";
    public static final String EXTRA_APP_ACTIVE = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_APP_ACTIVE";
    public static final String EXTRA_START_ROW = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_START_ROW";
    public static final String EXTRA_END_ROW = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_END_ROW";
    public static final String EXTRA_SEND_DATA_ROW_STATUS = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SEND_DATA_ROW_STATUS";
    public static final String EXTRA_PROGRAM_ROW_STATUS = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_PROGRAM_ROW_STATUS";
    public static final String EXTRA_VERIFY_ROW_STATUS = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_VERIFY_ROW_STATUS";
    public static final String EXTRA_VERIFY_ROW_CHECKSUM = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_VERIFY_ROW_CHECKSUM";
    public static final String EXTRA_VERIFY_CHECKSUM_STATUS = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_VERIFY_CHECKSUM_STATUS";
    public static final String EXTRA_SET_ACTIVE_APP = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_SET_ACTIVE_APP";
    public static final String EXTRA_VERIFY_APP_STATUS = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_VERIFY_APP_STATUS";
    public static final String EXTRA_VERIFY_EXIT_BOOTLOADER = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_VERIFY_EXIT_BOOTLOADER";
    public static final String EXTRA_ERROR_OTA = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_ERROR_OTA";

    //CYACD2 constants
    public static final String EXTRA_BTLDR_SDK_VER = "com.infineon.airocbluetoothconnect.backgroundservices." +
            "EXTRA_BTLDR_SDK_VER";

    /**
     * Shared Preference Status HandShake Status
     */
    public static final String PREF_BOOTLOADER_STATE = "PREF_BOOTLOADER_STATE";
    public static final String PREF_PROGRAM_ROW_NO = "PREF_PROGRAM_ROW_NO";
    public static final String PREF_PROGRAM_ROW_START_POS = "PREF_PROGRAM_ROW_START_POS";
    public static final String PREF_ARRAY_ID = "PREF_EXTRA_ARRAY_ID";
    /**
     * OTA File Selection Extras
     */
    public static final String REQ_FILE_COUNT = "REQ_FILE_COUNT";
    public static final String SELECTION_FLAG = "SELECTION_FLAG";
    public static final String ARRAYLIST_SELECTED_FILE_PATHS = "ARRAYLIST_SELECTED_FILE_PATHS";
    public static final String ARRAYLIST_SELECTED_FILE_NAMES = "ARRAYLIST_SELECTED_FILE_NAMES";
    public static final String EXTRA_ACTIVE_APP = "EXTRA_ACTIVE_APP";
    public static final byte ACTIVE_APP_NO_CHANGE = -1;
    public static final String EXTRA_SECURITY_KEY = "EXTRA_SECURITY_KEY";
    public static final long NO_SECURITY_KEY = -1;
    public static final int SECURITY_KEY_SIZE = 6;
    /**
     * Shared Preference Status File Status
     */
    public static final String PREF_OTA_FILE_ONE_NAME = "PREF_OTA_FILE_ONE_NAME";
    public static final String PREF_OTA_FILE_TWO_PATH = "PREF_OTA_FILE_TWO_PATH";
    public static final String PREF_OTA_FILE_TWO_NAME = "PREF_OTA_FILE_TWO_NAME";
    public static final String PREF_OTA_ACTIVE_APP_ID = "PREF_OTA_ACTIVE_APP_ID";
    public static final String PREF_OTA_SECURITY_KEY = "PREF_OTA_SECURITY_KEY";
    public static final String PREF_DEV_ADDRESS = "PREF_DEV_ADDRESS";
    public static final String PREF_IS_CYACD2_FILE = "PREF_IS_CYACD2_FILE";
    public static final String PREF_MTU_NEGOTIATED = "PREF_MTU_NEGOTIATED";
    /**
     * Shared Preference Status File Status
     */
    public static final String PREF_CLEAR_CACHE_ON_DISCONNECT = "PREF_CLEAR_CACHE_ON_DISCONNECT";
    public static final String PREF_UNPAIR_ON_DISCONNECT = "PREF_UNPAIR_ON_DISCONNECT";
    public static final boolean PREF_DEFAULT_UNPAIR_ON_DISCONNECT = false;

    /**
     * Shared preference of the google developer api key
     */
    public static final String PREF_GOOGLE_API_KEY = "PREF_GOOGLE_API_KEY";

    /**
     * Pair the device upon connect
     */
    public static final String PREF_PAIR_ON_CONNECT = "PREF_PAIR_ON_CONNECT";
    public static final String PREF_WAIT_FOR_PAIRING_REQUEST_FROM_PERIPHERAL_SECONDS = "PREF_WAIT_FOR_PAIRING_REQUEST_FROM_DEVICE_SECONDS";
    public static final String PREF_LOCATION_REQUIRED_DONT_ASK_AGAIN = "PREF_LOCATION_REQUIRED_DONT_ASK_AGAIN";

    /**
     * Graph constants
     */
    public static final int TEXT_SIZE_XHDPI = 24;
    public static final int TEXT_SIZE_XXHDPI = 30;
    public static final int TEXT_SIZE_XXXHDPI = 40;
    public static final int TEXT_SIZE_HDPI = 20;
    public static final int TEXT_SIZE_LDPI = 13;
    public static final int GRAPH_MARGIN_40 = 40;
    public static final int GRAPH_MARGIN_90 = 90;
    public static final int GRAPH_MARGIN_25 = 25;
    public static final int GRAPH_MARGIN_10 = 10;
    public static final int GRAPH_MARGIN_30 = 30;
    public static final int GRAPH_MARGIN_50 = 50;
    public static final int GRAPH_MARGIN_35 = 35;
    public static final int GRAPH_MARGIN_100 = 100;
    public static final int GRAPH_MARGIN_20 = 20;
    public static final int GRAPH_MARGIN_70 = 70;
    public static final int GRAPH_MARGIN_130 = 130;
    /**
     * Magic numbers
     */
    public static final int FIRST_BITMASK = 0x01;
    /**
     * OTA flags
     */
    public static final boolean OTA_ENABLED = true;
    public static final boolean GMS_ENABLED = true;
    /**
     * Fragment Tags
     */
    public static String HOME_PAGE_TABS_FRAGMENT_TAG = "home page tabs";
    public static String PROFILE_SCANNING_FRAGMENT_TAG = "profile scanning";
    public static String PAIRED_PROFILES_FRAGMENT_TAG = "paired profiles";
    public static String ABOUT_FRAGMENT_TAG = "About";
    public static String GLUCOSE_ADDITIONAL_FRAGMENT_TAG = "glucose additional tag";
    public static String DATALOGER_HISTORY = "Data Logger";
    public static String FRAGMENT_DATA_LOGER_TAG = "Data Logger tag";
    public static String PROFILE_CONTROL_FRAGMENT_TAG = "Services";
    public static String SERVICE_DISCOVERY_FRAGMENT_TAG = "Services_discovery";
    public static String FRAGMENT_TAG_SETTINGS = "Settings";
    public static String GATTDB_SELECTED_SERVICE = "gatt db service";
    public static String GATTDB_SELECTED_CHARACTERISTIC = "selected characterisitics";
    /**
     * DataLogger constants
     */
    public static String DATA_LOGGER_FILE_PATH = "file name";
    public static String DATA_LOGGER_SHOW_HISTORY_FILES_FLAG = "Data Logger Flag";

    /**
     * Glucose Bundle constants
     */
    public static String GLS_CARB_ID = "Carbohydrate id";
    public static String GLS_CARB_UNITS = "Carbohydrate units";
    public static String GLS_MEAL = "Meal";
    public static String GLS_TESTER = "Tester";
    public static String GLS_HEALTH = "Health";
    public static String GLS_EXERCISE_DURATION = "Exercise Duration";
    public static String GLS_EXERCISE_INTENSITY = "Exercise intensity";
    public static String GLS_MEDICATION_ID = "Medication id";
    public static String GLS_MEDICATION_QUANTITY = "Medication quantity";
    public static String GLS_MEDICATION_UNIT = "Medication unit";
    public static String GLS_HBA1C = "hba1c";

}
