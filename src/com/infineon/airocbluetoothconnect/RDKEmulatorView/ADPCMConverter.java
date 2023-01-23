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

package com.infineon.airocbluetoothconnect.RDKEmulatorView;

/**
 * This class will convert the ADPCM code received through BLE to PCM format
 */
class ADPCMConverter {

    /*Decoding Table: ima index table*/
    private static final byte[] indexTable = {
            -1, -1, -1, -1, 2, 4, 6, 8,
            -1, -1, -1, -1, 2, 4, 6, 8
    };

    /*Decoding Table: ima quantizer step size table */
    private static final short[] stepSizeTable = {
            7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
            19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
            50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
            130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
            337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
            876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
            2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
            5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
            15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767

    };

    /*Quantizer step size*/
    static short step;


    /**
     * Method to get the PCM Data
     *
     * @param audioPktBytes
     * @return pcmdata
     */
    public static byte[] getPCMData(byte[] audioPktBytes) {
        int index = 0;
        short decodedSample = 0;
        byte[] inTestBuffer = new byte[80];
        for (int count = 0; count < audioPktBytes.length; count++) {

            byte audioPktByte = audioPktBytes[count];
           /*Decompress speech sample from each byte and copy them into inTestBuffer*/
                /*Decode the 4-bit adpcm code of the lower nibble*/
            decodedSample = ADPCMDecoder(audioPktByte & RDKAttributes.LOWER_NIBBLE_MASK);

                /*Copy the 16-bit decoded sample into inTestBuffer*/
            inTestBuffer[index++] = (byte) (decodedSample & RDKAttributes.LOWER_BYTE_MASK);
            inTestBuffer[index++] = (byte) ((decodedSample >> RDKAttributes.BIT_8_POSITION)
                    & RDKAttributes.LOWER_BYTE_MASK);

                /*Decode the 4-bit adpcm code in the higher nibble*/
            decodedSample = ADPCMDecoder((audioPktByte >> RDKAttributes.BIT_4_POSITION) &
                    RDKAttributes.LOWER_NIBBLE_MASK);

                /*Copy the 16- bit decoded sample into inTestBuffer*/
            inTestBuffer[index++] = (byte) (decodedSample & RDKAttributes.LOWER_BYTE_MASK);
            inTestBuffer[index++] = (byte) ((decodedSample >> RDKAttributes.BIT_8_POSITION)
                    & RDKAttributes.LOWER_BYTE_MASK);

        }
        return inTestBuffer;
    }

    /**
     * ADPCM Decoder function
     *
     * @param adpcmCode
     * @return
     */
    private static short ADPCMDecoder(int adpcmCode) {
            /*Structure for previous predicted sample and quantizer step size table index*/
        ADPCMStateModel state = new ADPCMStateModel();
         /* Index into step size table */
        byte Index;

    /*Quantizer step size*/
        short step;

    /*Predicted Sample */
        int predSample;

    /*Predicted difference */
        short difference;

    /*Previous predicted sample.*/
        predSample = state.prevSample;

    /*Previous quantizer step size index.*/
        Index = state.prevIndex;

    /*Step size*/
        step = stepSizeTable[Index];

    /*Get the difference using adpcm code and the quantizer step size*/
        difference = (short) (step >> RDKAttributes.BIT_3_POSITION);

        if ((adpcmCode & RDKAttributes.BIT_2_MASK) != 0) {
            difference += step;
        }

        if ((adpcmCode & RDKAttributes.BIT_1_MASK) != 0) {
            difference += (step >> RDKAttributes.BIT_1_POSITION);
        }

        if ((adpcmCode & RDKAttributes.BIT_0_MASK) != 0) {
            difference += (step >> RDKAttributes.BIT_2_POSITION);
        }

    /* Add the difference to the predicted sample*/
        if ((adpcmCode & RDKAttributes.BIT_3_MASK) != 0) {
            predSample -= difference;
        } else {
            predSample += difference;
        }

    /*Check overflow of the predicted sample*/
        if (predSample > RDKAttributes.AUDIO_SAMPLE_MAX) {
            predSample = RDKAttributes.AUDIO_SAMPLE_MAX;
        } else if (predSample < RDKAttributes.AUDIO_SAMPLE_MIN) {
            predSample = RDKAttributes.AUDIO_SAMPLE_MIN;
        }

    /* Get the new quantizer step size by adding the prev Index and a table lookup using the ADPCM code*/
        Index += indexTable[(adpcmCode & (RDKAttributes.BIT_0_MASK | RDKAttributes.BIT_1_MASK |
                RDKAttributes.BIT_2_MASK | RDKAttributes.BIT_3_MASK))];

    /* Check overflow of the quantizer step size index*/
        if (Index < 0) {
            Index = 0;
        }
        if (Index > RDKAttributes.ADPCM_INDEX_MAX) {
            Index = RDKAttributes.ADPCM_INDEX_MAX;
        }

    /*Save the current predicted sample and index.*/
        state.prevSample = (short) predSample;
        state.prevIndex = Index;
        return ((short) predSample);
    }
}
