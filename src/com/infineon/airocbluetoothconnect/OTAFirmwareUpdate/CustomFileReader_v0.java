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

package com.infineon.airocbluetoothconnect.OTAFirmwareUpdate;

import com.infineon.airocbluetoothconnect.CommonUtils.Logger;
import com.infineon.airocbluetoothconnect.CommonUtils.Utils;
import com.infineon.airocbluetoothconnect.DataModelClasses.OTAFlashRowModel_v0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class created to read the .cycad files.The read file is stored temporarily
 */
class CustomFileReader_v0 {
    private String mSiliconID;
    private final String mHeader;
    private final File mFile;
    private int mReadingLine = 0;

    //File read status updater
    private FileReadStatusUpdater mFileReadStatusUpdater;

    //Constructor
    public CustomFileReader_v0(String filepath) {
        mFile = new File(filepath);
        mHeader = getHeader(mFile);
        Logger.e("PATH>>>"+filepath);
    }

    public void setFileReadStatusUpdater(FileReadStatusUpdater fileReadStatusUpdater) {
        this.mFileReadStatusUpdater = fileReadStatusUpdater;
    }

    /**
     * Analysing the header file and extracting the silicon ID,Check Sum Type and Silicon rev
     */
    public String[] analyseFileHeader() {
        String[] headerData = new String[3];
        String MSBString = Utils.getMSB(mHeader);
        mSiliconID = getSiliconID(MSBString);
        String siliconRev = getSiliconRev(MSBString);
        String checkSumType = getCheckSumType(MSBString);
        headerData[0] = mSiliconID;
        headerData[1] = siliconRev;
        headerData[2] = checkSumType;
        return headerData;
    }

    /**
     * Method to parse the file a read each line and put the line to a data model
     *
     * @return
     */
    public ArrayList<OTAFlashRowModel_v0> readDataLines() {
        ArrayList<OTAFlashRowModel_v0> flashDataLines = new ArrayList<>();
        String dataLine = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFile));
            while ((dataLine = bufferedReader.readLine()) != null) {
                mReadingLine++;
                mFileReadStatusUpdater.onFileReadProgressUpdate(mReadingLine);
                byte[] data;

                OTAFlashRowModel_v0 model = new OTAFlashRowModel_v0();
                if (mReadingLine != 1) {
                    StringBuilder sb = new StringBuilder(dataLine);
                    sb.deleteCharAt(0);
                    model.mArrayId = Integer.parseInt(sb.substring(0, 2), 16);
                    model.mRowNo = Utils.getMSB(sb.substring(2, 6));
                    model.mDataLength = Integer.parseInt(sb.substring(6, 10), 16);
                    model.mRowCheckSum = Integer.parseInt(sb.substring(dataLine.length() - 3, dataLine.length() - 1), 16);
                    String dataString = sb.substring(10, dataLine.length() - 2);
                    data = new byte[model.mDataLength];
                    for (int i = 0, j = 0; i < model.mDataLength; i++, j += 2) {
                        data[i] = (byte) Integer.parseInt(dataString.substring(j, j + 2), 16);
                    }
                    model.mData = data;
                    flashDataLines.add(model);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flashDataLines;
    }

    /**
     * Count the number of lines in the selected file
     *
     * @return totalLines
     */
    public int getTotalLines() {
        int totalLines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mFile));
            try {
                String dataLine = null;
                while ((dataLine = reader.readLine()) != null) {
                    totalLines++;
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalLines;
    }

    /**
     * Read the first line from the file
     *
     * @param file
     * @return
     */
    protected String getHeader(File file) {
        String header = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                header = reader.readLine();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    private String getSiliconID(String header) {
        String siliconID = header.substring(4, 12);
        return siliconID;
    }

    private String getSiliconRev(String header) {
        String siliconRev = header.substring(2, 4);
        return siliconRev;
    }

    private String getCheckSumType(String header) {
        String checkSumType = header.substring(0, 2);
        return checkSumType;
    }
}
