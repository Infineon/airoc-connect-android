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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

import com.infineon.airocbluetoothconnect.CommonUtils.ConvertUtils;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v1;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v1.AppInfo;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v1.Data;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v1.EIV;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v1.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse .cyacd2 file according to 002-13924 *B
 * APPINFO is in Big Endian format
 * The rest of multi-byte values is in Little Endian format
 */
class CustomFileReader_v1 {

    /**
     * Thrown in case of invalid .cyacd2 file being parsed
     */
    public static class InvalidFileFormatException extends Exception {

        public InvalidFileFormatException(Throwable t) {
            super(t);
        }
    }

    public static final String KEY_HEADER = "HEADER";
    public static final String KEY_APPINFO = "APPINFO";
    public static final String KEY_DATA = "DATA";

    private final File mFile;

    //File read status updater
    private FileReadStatusUpdater mFileReadStatusUpdater;

    public CustomFileReader_v1(String filepath) {
        mFile = new File(filepath);
    }

    public void setFileReadStatusUpdater(FileReadStatusUpdater fileReadStatusUpdater) {
        this.mFileReadStatusUpdater = fileReadStatusUpdater;
    }

    /**
     * Parses the .cyacd2 file
     *
     * @return parsed data
     * @throws InvalidFileFormatException
     */
    public Map<String, List<OTAFlashRowModel_v1>> readLines() throws InvalidFileFormatException {
        Map<String, List<OTAFlashRowModel_v1>> rows = new HashMap<>();
        rows.put(KEY_DATA, new ArrayList<OTAFlashRowModel_v1>());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mFile));
            try {
                int lineCount = 0;
                if (mFileReadStatusUpdater != null) {
                    mFileReadStatusUpdater.onFileReadProgressUpdate(lineCount + 1);
                }
                for (String line = null; (line = reader.readLine()) != null; lineCount++) {
                    if (lineCount == 0) {
                        Header header = parseHeader(line);
                        rows.put(KEY_HEADER, Arrays.<OTAFlashRowModel_v1>asList(header));
                    } else {//data rows
                        if (line.startsWith(AppInfo.DISCRIMINATOR)) {
                            AppInfo appInfo = parseAppInfo(line);
                            rows.put(KEY_APPINFO, Arrays.<OTAFlashRowModel_v1>asList(appInfo));
                        } else if (line.startsWith(EIV.DISCRIMINATOR)) {
                            EIV eiv = parseEiv(line);
                            rows.get(KEY_DATA).add(eiv);
                        } else if (line.startsWith(Data.DISCRIMINATOR)) {
                            Data data = parseData(line);
                            rows.get(KEY_DATA).add(data);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            throw new InvalidFileFormatException(e);
        }

        if (!rows.containsKey(KEY_APPINFO)) {
            long appStart = 0xffffffffl, appSize = 0;
            for (OTAFlashRowModel_v1 row : rows.get(KEY_DATA)) {
                if (row instanceof Data) {
                    Data data = (Data) row;
                    long addr = ConvertUtils.byteArrayToLongLittleEndian(data.mAddress);
                    if (addr < appStart) {
                        appStart = addr;
                    }
                    appSize += data.mData.length;
                }
            }
            byte[] baAppStart = ConvertUtils.intToByteArray((int) appStart);
            byte[] baAppSize = ConvertUtils.intToByteArray((int) appSize);
            AppInfo appInfo = new AppInfo(baAppStart, baAppSize);
            rows.put(KEY_APPINFO, Arrays.<OTAFlashRowModel_v1>asList(appInfo));
        }
        return rows;
    }

    /**
     * @param line
     * @return FileVersion(1) + SiliconId(4) + SiliconRev(1) + CheckSumType(1) + AppId(1) + ProductId(4) parsed from the line
     * @throws IllegalArgumentException if the line is invalid
     */
    private static Header parseHeader(String line) {
        final int fileVersionLength = 2;//2 hex digits (1 byte)
        final int siliconIdLength = 8;//4 bytes
        final int siliconRevLength = 2;//1 byte
        final int checkSumTypeLength = 2;//1 byte
        final int appIdLength = 2;//1 byte
        final int productIdLength = 8;//4 byte
        if (line.length() == fileVersionLength + siliconIdLength + siliconRevLength + checkSumTypeLength + appIdLength + productIdLength) {
            int offset = 0;
            byte fileVersion = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, fileVersionLength)[0];

            offset += fileVersionLength;
            byte[] siliconId = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, siliconIdLength);

            offset += siliconIdLength;
            byte siliconRev = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, siliconRevLength)[0];

            offset += siliconRevLength;
            byte checkSumType = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, checkSumTypeLength)[0];

            offset += checkSumTypeLength;
            byte appId = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, appIdLength)[0];
            offset += appIdLength;

            byte[] productId = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, productIdLength);
            return new Header(fileVersion, siliconId, siliconRev, checkSumType, appId, productId);
        }
        throw new IllegalArgumentException("Invalid Header line");
    }

    /**
     * @param line
     * @return AppStart(4) + AppSize(4) parsed from the line
     * @throws IllegalArgumentException if the line is invalid
     */
    private static AppInfo parseAppInfo(String line) {
        int separatorIndex = line.indexOf(AppInfo.SEPARATOR);
        int offset = AppInfo.DISCRIMINATOR.length();
        if (separatorIndex >= offset) {//This check covers valid string "@APPINFO:0x,0x" which gets parsed to [0,0,0,0] for address and [0,0,0,0] for size
            final int appStartLength = separatorIndex - offset;
            byte[] appStart = ConvertUtils.hexStringToByteArrayBigEndian(line, offset, appStartLength, 8);

            offset += appStartLength + AppInfo.SEPARATOR.length();
            final int appSizeLength = line.length() - offset;//Remaining bytes for appSize
            byte[] appSize = ConvertUtils.hexStringToByteArrayBigEndian(line, offset, appSizeLength, 8);
            return new AppInfo(appStart, appSize);
        }
        throw new IllegalArgumentException("Invalid AppInfo line");
    }

    /**
     * @param line
     * @return EIV(0, 8, or 16) parsed from the line
     * @throws IllegalArgumentException if the line is invalid
     */
    private static EIV parseEiv(String line) {
        int offset = EIV.DISCRIMINATOR.length();
        int length = line.length() - offset;//Remaining bytes for EIV
        byte[] eiv = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, length);
        return new EIV(eiv);
    }

    /**
     * @param line
     * @return Address(4) + Data(N) parsed from the line
     * @throws IllegalArgumentException if the line is invalid
     */
    private static Data parseData(String line) {
        int offset = Data.DISCRIMINATOR.length();
        final int addressLength = 8;//4 bytes
        if (offset + addressLength <= line.length()) {//Address is required, data is optional
            byte[] address = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, addressLength);

            offset += addressLength;
            final int dataLength = line.length() - offset;//Remaining bytes for data
            byte[] data = ConvertUtils.hexStringToByteArrayLittleEndian(line, offset, dataLength);

            return new Data(address, data);
        }
        throw new IllegalArgumentException("Invalid Data line");
    }
}
