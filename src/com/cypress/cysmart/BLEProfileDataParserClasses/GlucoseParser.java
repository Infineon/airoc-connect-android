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
import android.os.Handler;
import android.os.Parcel;
import android.util.SparseArray;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.UUIDDatabase;
import com.cypress.cysmart.DataModelClasses.GlucoseRecord;

import java.util.UUID;

/**
 * Class used for parsing Glucose related information
 */
public class GlucoseParser {

    private static final int UNIT_kgpl = 0;
    private static final int UNIT_molpl = 1;

    private static final int UNIT_kg = 0;
    private static final int UNIT_l = 1;

    private final static int OP_CODE_REPORT_STORED_RECORDS = 1;
    private final static int OP_CODE_DELETE_STORED_RECORDS = 2;
    private final static int OP_CODE_ABORT_OPERATION = 3;
    private final static int OP_CODE_REPORT_NUMBER_OF_RECORDS = 4;
    private final static int OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE = 5;
    private final static int OP_CODE_RESPONSE_CODE = 6;

    private final static int OPERATOR_NULL = 0;
    private final static int OPERATOR_ALL_RECORDS = 1;
    private final static int OPERATOR_LESS_THEN_OR_EQUAL = 2;
    private final static int OPERATOR_GREATER_THEN_OR_EQUAL = 3;
    private final static int OPERATOR_WITHING_RANGE = 4;
    private final static int OPERATOR_FIRST_RECORD = 5;
    private final static int OPERATOR_LAST_RECORD = 6;

    /**
     * The filter type is used for range operators ({@link #OPERATOR_LESS_THEN_OR_EQUAL}, {@link #OPERATOR_GREATER_THEN_OR_EQUAL}, {@link #OPERATOR_WITHING_RANGE}.<br/>
     * The syntax of the operand is: [Filter Type][Minimum][Maximum].<br/>
     * This filter selects the records by the sequence number.
     */
    private final static int FILTER_TYPE_SEQUENCE_NUMBER = 1;
    /**
     * The filter type is used for range operators ({@link #OPERATOR_LESS_THEN_OR_EQUAL}, {@link #OPERATOR_GREATER_THEN_OR_EQUAL}, {@link #OPERATOR_WITHING_RANGE}.<br/>
     * The syntax of the operand is: [Filter Type][Minimum][Maximum].<br/>
     * This filter selects the records by the user facing time (base time + offset time).
     */
    private final static int FILTER_TYPE_USER_FACING_TIME = 2;

    private final static int RESPONSE_SUCCESS = 1;
    private final static int RESPONSE_OP_CODE_NOT_SUPPORTED = 2;
    private final static int RESPONSE_INVALID_OPERATOR = 3;
    private final static int RESPONSE_OPERATOR_NOT_SUPPORTED = 4;
    private final static int RESPONSE_INVALID_OPERAND = 5;
    private final static int RESPONSE_NO_RECORDS_FOUND = 6;
    private final static int RESPONSE_ABORT_UNSUCCESSFUL = 7;
    private final static int RESPONSE_PROCEDURE_NOT_COMPLETED = 8;
    private final static int RESPONSE_OPERAND_NOT_SUPPORTED = 9;

    //Switch case constants
    private static final int CASE_RESERVED = 0;
    private static final int CASE_GT_CWB = 1;
    private static final int CASE_GT_CP = 2;
    private static final int CASE_GT_VWB = 3;
    private static final int CASE_GT_VP = 4;
    private static final int CASE_GT_AWB = 5;
    private static final int CASE_GT_AP = 6;
    private static final int CASE_GT_UWB = 7;
    private static final int CASE_GT_UP = 8;
    private static final int CASE_GT_ISF = 9;
    private static final int CASE_GT_CS = 10;

    private static final int CASE_GSL_FINGER = 1;
    private static final int CASE_GSL_AST = 2;
    private static final int CASE_GSL_EARLOBE = 3;
    private static final int CASE_GSL_CS = 4;
    private static final int CASE_GSL_SAMPLE = 15;

    private static final int CASE_BREAKFAST = 1;
    private static final int CASE_DINNER = 3;
    private static final int CASE_LUNCH = 2;
    private static final int CASE_SNACK = 4;
    private static final int CASE_DRINK = 5;
    private static final int CASE_SUPPER = 6;
    private static final int CASE_BRUNCH = 7;

    private static final int CASE_PRE = 1;
    private static final int CASE_POST = 2;
    private static final int CASE_FAST = 3;
    private static final int CASE_CASUAL = 4;
    private static final int CASE_BEDTIME = 5;

    private static final int CASE_SELF = 1;
    private static final int CASE_HCP = 2;
    private static final int CASE_LABTEST = 3;
    private static final int CASE_TVNA = 15;

    private static final int CASE_MIN_HEALTH = 1;
    private static final int CASE_MAJ_HEALTH = 2;
    private static final int CASE_MENSES = 3;
    private static final int CASE_STRESS = 4;
    private static final int CASE_NO_HEALTH_ISSUES = 5;
    private static final int CASE_HVNA = 15;

    private static final int CASE_RAI = 1;
    private static final int CASE_SAI = 2;
    private static final int CASE_IAI = 3;
    private static final int CASE_LAI = 4;
    private static final int CASE_PMI = 5;



    public static SparseArray<GlucoseRecord> mMeasurementRecords = new SparseArray<GlucoseRecord>();
    private static Handler mHandler = new Handler();

    public static SparseArray<GlucoseRecord> getGlucoseMeasurement(BluetoothGattCharacteristic characteristic) {
        final UUID uuid = characteristic.getUuid();
        if (UUIDDatabase.UUID_GLUCOSE_MEASUREMENT.equals(uuid)) {
            int offset = 0;
            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            final boolean timeOffsetPresent = (flags & 0x01) > 0;
            final boolean typeAndLocationPresent = (flags & 0x02) > 0;
            final int concentrationUnit = (flags & 0x04) > 0 ? GlucoseRecord.UNIT_molpl : GlucoseRecord.UNIT_kgpl;
            final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
            final boolean contextInfoFollows = (flags & 0x10) > 0;

            // create and fill the new record
            final GlucoseRecord record = new GlucoseRecord(Parcel.obtain());
            record.sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;


            record.time = (DateTimeParser.parse(characteristic, offset));
            offset += 7;

            if (timeOffsetPresent) {
                record.timeOffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
                offset += 2;
            }

            if (typeAndLocationPresent) {
                record.glucoseConcentration = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
                record.unit = (concentrationUnit);
                final int typeAndLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                record.type = getType((typeAndLocation & 0xF0) >> 4); // TODO this way or around?
                record.sampleLocation = getLocation(typeAndLocation & 0x0F);
                offset += 3;
            }

            if (sensorStatusAnnunciationPresent) {
                record.status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            }

            mMeasurementRecords.put(record.sequenceNumber, record);
            record.context = contextInfoFollows;

            return mMeasurementRecords;
        } else if (UUIDDatabase.UUID_GLUCOSE_MEASUREMENT_CONTEXT.equals(uuid)) {
            int offset = 0;
            final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            final boolean carbohydratePresent = (flags & 0x01) > 0;
            final boolean mealPresent = (flags & 0x02) > 0;
            final boolean testerHealthPresent = (flags & 0x04) > 0;
            final boolean exercisePresent = (flags & 0x08) > 0;
            final boolean medicationPresent = (flags & 0x10) > 0;
            final int medicationUnit = (flags & 0x20) > 0 ? UNIT_l : UNIT_kg;
            final boolean hbA1cPresent = (flags & 0x40) > 0;
            final boolean moreFlagsPresent = (flags & 0x80) > 0;


            final int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            offset += 2;


            GlucoseRecord record = mMeasurementRecords.get(sequenceNumber);
            if (record == null) {
                Logger.w("Context information with unknown sequence number: " + sequenceNumber);
                return null;
            }

            if (moreFlagsPresent) // not supported yet
                offset += 1;

            if (carbohydratePresent) {
                final int carbohydrateId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                final float carbohydrateUnits = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
                record.carbohydrateId = getCarbohydrate(carbohydrateId);
                record.carbohydrateUnits = (carbohydrateUnits) + (carbohydrateUnits == UNIT_kg ? "kg" : "l");
                offset += 3;
            }

            if (mealPresent) {
                final int meal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                record.meal = getMeal(meal);
                offset += 1;
            }

            if (testerHealthPresent) {
                final int testerHealth = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                final int tester = (testerHealth & 0xF0) >> 4;
                final int health = (testerHealth & 0x0F);
                record.tester = getTester(tester);
                record.health = getHealth(health);
                offset += 1;
            }

            if (exercisePresent) {
                final int exerciseDuration = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                final int exerciseIntensity = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                record.exerciseDuration = exerciseDuration + ("s");
                record.exerciseIntensity = (exerciseIntensity) + ("%");
                offset += 3;
            }

            if (medicationPresent) {
                final int medicationId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                final float medicationQuantity = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
                record.medicationId = getMedicationId(medicationId);
                record.medicationQuantity = (medicationQuantity);
                record.medicationUnit = (medicationUnit == UNIT_kg ? "kg" : "l");
                offset += 3;
            }

            if (hbA1cPresent) {
                final float HbA1c = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
                record.HbA1c = (HbA1c) + ("%");
            }
        }
        return mMeasurementRecords;
    }


    public static void onCharacteristicIndicated(final BluetoothGattCharacteristic characteristic) {
        // Record Access Control Point characteristic
        int offset = 0;
        final int opCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 2; // skip the operator

        if (opCode == OP_CODE_NUMBER_OF_STORED_RECORDS_RESPONSE) {
            // We've obtained the number of all records
            final int number = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);

            // Request the records
            if (number > 0) {
                final BluetoothGattCharacteristic racpCharacteristic = characteristic;
                setOpCode(racpCharacteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_ALL_RECORDS);
                BluetoothLeService.writeCharacteristic(racpCharacteristic);
            } else {
                Logger.e("No records");
            }
        } else if (opCode == OP_CODE_RESPONSE_CODE) {
            final int requestedOpCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            final int responseCode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 1);
            Logger.d("Response result for: " + requestedOpCode + " is: " + responseCode);
            switch (responseCode) {
                case RESPONSE_SUCCESS:
                    Logger.e("RESPONSE_SUCCESS");
                    break;
                case RESPONSE_NO_RECORDS_FOUND:
                    Logger.e("RESPONSE_SUCCESS");
                    break;
                case RESPONSE_OP_CODE_NOT_SUPPORTED:
                    Logger.e("RESPONSE_SUCCESS");
                    break;
                case RESPONSE_PROCEDURE_NOT_COMPLETED:
                    Logger.e("RESPONSE_SUCCESS");
                case RESPONSE_ABORT_UNSUCCESSFUL:
                    Logger.e("RESPONSE_SUCCESS");
                default:
                    Logger.e("RESPONSE_UNKOWN");
                    break;
            }
        }
    }


    /**
     * Writes given operation parameters to the characteristic
     *
     * @param characteristic the characteristic to write. This must be the Record Access Control Point characteristic
     * @param opCode         the operation code
     * @param operator       the operator (see {@link #OPERATOR_NULL} and others
     * @param params         optional parameters (one for >=, <=, two for the range, none for other operators)
     */
    private static void setOpCode(final BluetoothGattCharacteristic characteristic, final int opCode, final int operator, final Integer... params) {
        final int size = 2 + ((params.length > 0) ? 1 : 0) + params.length * 2; // 1 byte for opCode, 1 for operator, 1 for filter type (if parameters exists) and 2 for each parameter
        characteristic.setValue(new byte[size]);

        // write the operation code
        int offset = 0;
        characteristic.setValue(opCode, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        // write the operator. This is always present but may be equal to OPERATOR_NULL
        characteristic.setValue(operator, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
        offset += 1;

        // if parameters exists, append them. Parameters should be sorted from minimum to maximum. Currently only one or two params are allowed
        if (params.length > 0) {
            // our implementation use only sequence number as a filer type
            characteristic.setValue(FILTER_TYPE_SEQUENCE_NUMBER, BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            for (final Integer i : params) {
                characteristic.setValue(i, BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                offset += 2;
            }
        }
    }

    /**
     * Sends the request to obtain the last (most recent) record from glucose device. The data will be returned to Glucose Measurement characteristic as a notification followed by Record Access
     * Control Point indication with status code ({@link #RESPONSE_SUCCESS} or other in case of error.
     */
    public void getLastRecord(BluetoothGattCharacteristic mRecordAccessControlPointCharacteristic) {
        if (mRecordAccessControlPointCharacteristic == null)
            return;

        clear();
        final BluetoothGattCharacteristic characteristic = mRecordAccessControlPointCharacteristic;
        setOpCode(characteristic, OP_CODE_REPORT_STORED_RECORDS, OPERATOR_LAST_RECORD);
        BluetoothLeService.writeCharacteristic(characteristic);
    }


    /**
     * Sends the request to obtain all records from glucose device. Initially we want to notify him/her about the number of the records so the {@link #OP_CODE_REPORT_NUMBER_OF_RECORDS} is send. The
     * data will be returned to Glucose Measurement characteristic as a notification followed by Record Access Control Point indication with status code ({@link #RESPONSE_SUCCESS} or other in case of
     * error.
     */
    public void getAllRecords(BluetoothGattCharacteristic mRecordAccessControlPointCharacteristic) {
        if (mRecordAccessControlPointCharacteristic == null)
            return;

        clear();
        final BluetoothGattCharacteristic characteristic = mRecordAccessControlPointCharacteristic;
        setOpCode(characteristic, OP_CODE_REPORT_NUMBER_OF_RECORDS, OPERATOR_ALL_RECORDS);
        BluetoothLeService.writeCharacteristic(characteristic);
    }


    /**
     * Clears the records list locally
     */
    public void clear() {
        mMeasurementRecords.clear();
    }

    /**
     * Sends the request to delete all data from the device.
     */
    public void deleteAllRecords(BluetoothGattCharacteristic mRecordAccessControlPointCharacteristic) {
        if (mRecordAccessControlPointCharacteristic == null)
            return;

        clear();
        final BluetoothGattCharacteristic characteristic = mRecordAccessControlPointCharacteristic;
        setOpCode(characteristic, OP_CODE_DELETE_STORED_RECORDS, OPERATOR_ALL_RECORDS);
        BluetoothLeService.writeCharacteristic(characteristic);
    }

    private static String getType(final int type) {
        switch (type) {
            case CASE_GT_CWB:
                return "Capillary Whole blood";
            case CASE_GT_CP:
                return "Capillary Plasma";
            case CASE_GT_VWB:
                return "Venous Whole blood";
            case CASE_GT_VP:
                return "Venous Plasma";
            case CASE_GT_AWB:
                return "Arterial Whole blood";
            case CASE_GT_AP:
                return "Arterial Plasma";
            case CASE_GT_UWB:
                return "Undetermined Whole blood";
            case CASE_GT_UP:
                return "Undetermined Plasma";
            case CASE_GT_ISF:
                return "Interstitial Fluid (ISF)";
            case CASE_GT_CS:
                return "Control Solution";
            default:
                return "Reserved for future use (" + type + ")";
        }
    }

    private static String getLocation(final int location) {
        switch (location) {
            case CASE_GSL_FINGER:
                return "Finger";
            case CASE_GSL_AST:
                return "Alternate Site Test (AST)";
            case CASE_GSL_EARLOBE:
                return "Earlobe";
            case CASE_GSL_CS:
                return "Control solution";
            case CASE_GSL_SAMPLE:
                return "Value not available";
            default:
                return "Reserved for future use (" + location + ")";
        }
    }

    private static String getCarbohydrate(final int id) {
        switch (id) {
            case CASE_BREAKFAST:
                return "Breakfast";
            case CASE_LUNCH:
                return "Lunch";
            case CASE_DINNER:
                return "Dinner";
            case CASE_SNACK:
                return "Snack";
            case CASE_DRINK:
                return "Drink";
            case CASE_SUPPER:
                return "Supper";
            case CASE_BRUNCH:
                return "Brunch";
            default:
                return "Reserved for future use (" + id + ")";
        }
    }

    private static String getMeal(final int id) {
        switch (id) {
            case CASE_PRE:
                return "Preprandial (before meal)";
            case CASE_POST:
                return "Postprandial (after meal)";
            case CASE_FAST:
                return "Fasting";
            case CASE_CASUAL:
                return "Casual (snacks, drinks, etc.)";
            case CASE_BEDTIME:
                return "Bedtime";
            default:
                return "Reserved for future use (" + id + ")";
        }
    }

    private static String getTester(final int id) {
        switch (id) {
            case CASE_SELF:
                return "Self";
            case CASE_HCP:
                return "Health Care Professional";
            case CASE_LABTEST:
                return "Lab test";
            case CASE_CASUAL:
                return "Casual (snacks, drinks, etc.)";
            case CASE_TVNA:
                return "Tester value not available";
            default:
                return "Reserved for future use (" + id + ")";
        }
    }

    private static String getHealth(final int id) {
        switch (id) {
            case CASE_MIN_HEALTH:
                return "Minor health issues";
            case CASE_MAJ_HEALTH:
                return "Major health issues";
            case CASE_MENSES:
                return "During menses";
            case CASE_STRESS:
                return "Under stress";
            case CASE_NO_HEALTH_ISSUES:
                return "No health issues";
            case CASE_HVNA:
                return "Health value not available";
            default:
                return "Reserved for future use (" + id + ")";
        }
    }

    private static String getMedicationId(final int id) {
        switch (id) {
            case CASE_RAI:
                return "Rapid acting insulin";
            case CASE_SAI:
                return "Short acting insulin";
            case CASE_IAI:
                return "Intermediate acting insulin";
            case CASE_LAI:
                return "Long acting insulin";
            case CASE_PMI:
                return "Pre-mixed insulin";
            default:
                return "Reserved for future use (" + id + ")";
        }
    }
}