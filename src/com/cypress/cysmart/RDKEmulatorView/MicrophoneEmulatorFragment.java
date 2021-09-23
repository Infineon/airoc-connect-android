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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cypress.cysmart.BLEConnectionServices.BluetoothLeService;
import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

/**
 * Fragment class to showToast the emulator view of the Remote control RDK which has Human Interface
 * Device sservice
 */
public class MicrophoneEmulatorFragment extends Fragment {

    //temporary file names
    public static String mfilePCM = Environment.getExternalStorageDirectory()
            + File.separator + "CySmart" + File.separator + "RecordedAudio.pcm";

    String mfileWAV= Environment.getExternalStorageDirectory()
            + File.separator + "CySmart" + File.separator + "RecordedAudio.wav";


    File mFilePCM;

    //Flags
    private static boolean mIsrecording = false;
    private Timer mTimer;
    private int RECORD_TIME_OUT=60000;

    //Constants
    private int PACKETSIZE = 16384;
    //UI elements
    private TextView mHexValue;

    /**
     * Thread communication
     */
    private Handler mTaskandler;
    private Handler mGUIhandler;
    String START_RECORD = "RECORD";
    String CANCEL_RECORD = "CANCEL";
    /**
     * BroadcastReceiver for receiving the GATT server status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            // GATT Data available
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                /**
                 * Byte information send through BLE received here
                 */
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    byte[] array = intent
                            .getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                    String hexValue = getHexValue(array);
                    /**
                     * Report reference descriptor received
                     */
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID)) {
                        String reportReference = intent.getStringExtra
                                (Constants.EXTRA_DESCRIPTOR_REPORT_REFERENCE_ID);
                        /**
                         * Audio report reference control received
                         */
                        if (reportReference.equalsIgnoreCase(ReportAttributes.
                                AUDIO_REPORT_REFERENCE_CONTROL_STRING)) {

                            /**
                             * Extracting the first byte to verify
                             * sync is required
                             */
                            String firstByte = hexValue.substring(0, 2);
                            if (firstByte.equalsIgnoreCase(ReportAttributes.MICROPHONE_SYNC)) {
                                /**
                                 * Sync required
                                 * Updating the ADPCMStateModel values
                                 */
                                if (hexValue.length() == 8) {
                                    ADPCMStateModel.prevIndex = array[1];
                                    ADPCMStateModel.prevSample = ((int) array[2] << 8);
                                    ADPCMStateModel.prevSample |= array[3];
                                }
                            }else if(hexValue.equalsIgnoreCase("FF00")){
                                Logger.e("Last Packet Received");
                                if(mIsrecording){
                                    mIsrecording=false;
                                    if(mTimer!=null){
                                        mTimer.cancel();
                                    }
                                    mGoogleVoiceRecord.setText(START_RECORD);
                                    createWavFile(mFilePCM, mfileWAV);
                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            displayHexData("The Complete PCM data of the recording " +
                                                    "is saved at\n"+mfilePCM);
                                        }
                                    },1000);
                                }

                            }


                        }
                        /**
                         * Audio report reference data received
                         */
                        if (reportReference.equalsIgnoreCase(ReportAttributes.
                                AUDIO_REPORT_REFERENCE_DATA_STRING)) {
                            if(mIsrecording){
                                Message audioData=Message.obtain();
                                audioData.obj=array;
                                mTaskandler.sendMessage(audioData);
                            }
                        }
                    }

                }
            }
        }

    };
    // create temp file that will hold byte array
    // File tempMp3;
    private TextView mConvertedTextValue;
    private Button mGoogleVoiceRecord;
    private Button mGoogleVoicePlayBack;
    private Button mGoogleVoiceConvert;
    private TextView mFilePath;
    private MediaPlayer mMediaPlayer;

    private static byte[] intToByteArray(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    // convert a short to a byte array
    public static byte[] shortToByteArray(short data) {
        /*
         * NB have also tried:
         * return new byte[]{(byte)(data & 0xff),(byte)((data >> 8) & 0xff)};
         *
         */

        return new byte[]{(byte) (data & 0xff), (byte) ((data >>> 8) & 0xff)};
    }

    //Constructor
    public MicrophoneEmulatorFragment create(BluetoothGattService bluetoothGattService) {
        MicrophoneEmulatorFragment remoteControlEmulatorService = new MicrophoneEmulatorFragment();
        return remoteControlEmulatorService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rdk_microphone, container,
                false);
        mHexValue = (TextView) rootView.findViewById(R.id.hex_value);
        mConvertedTextValue = (TextView) rootView.findViewById(R.id.converted_text_value);
        mGoogleVoiceConvert = (Button) rootView.findViewById(R.id.voiceconversion);
        mFilePath = (TextView) rootView.findViewById(R.id.pcm_filepath);
        Button mAPIChange = (Button) rootView.findViewById(R.id.apichange);
        mGoogleVoiceRecord = (Button) rootView.findViewById(R.id.voicerecord);
        mGoogleVoicePlayBack = (Button) rootView.findViewById(R.id.voiceplayback);
        /**
         * Deleting the old recorded file and creating new one
         */
        mFilePCM = new File(mfilePCM);
        if (mFilePCM.exists()) {
            mFilePCM.delete();
        }
        try {
            mFilePCM.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFilePath.setText("The play back file is saved at\n"+mfileWAV);
        /**
         * Convert button click listner
         */
        mGoogleVoiceConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleVoiceRecord.setText(START_RECORD);
                if (mFilePCM.length() != 0) {
                    String mStoredAPIKey = Utils.getStringSharedPreference(getActivity(),
                            Constants.PREF_GOOGLE_API_KEY);
                    if (mIsrecording) {
                       mIsrecording = false;
                        File file = new File(mfilePCM);
                        createWavFile(file, mfileWAV);
                    }
                    if (mStoredAPIKey.equalsIgnoreCase("")) {
                        showCustumAlert("", false);
                    } else {
                        if (Utils.checkNetwork(getActivity())) {
                            googleVoiceConversion googleVoiceConversion = new googleVoiceConversion();
                            googleVoiceConversion.execute();
                        } else {
                            Toast.makeText(getActivity(), R.string.alert_message_no_internet,
                                    Toast.LENGTH_SHORT).
                                    show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.toast_file_zero, Toast.LENGTH_SHORT).
                            show();
                }


            }
        });
        /**
         * Record button click listner
         */
        mGoogleVoiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView recordStatus = (TextView) view;

                if (recordStatus.getText().toString().equalsIgnoreCase(START_RECORD)) {
                    mFilePCM = new File(mfilePCM);
                    if (mFilePCM.exists()) {
                        mFilePCM.delete();
                    }
                    try {
                        mFilePCM.createNewFile();
                        mIsrecording = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recordStatus.setText(CANCEL_RECORD);
                    mGoogleVoicePlayBack.setVisibility(View.GONE);
                    mGoogleVoiceConvert.setVisibility(View.GONE);
                    mFilePath.setVisibility(View.GONE);
                    clearGUI();
                    startRecordTimer();
                } else if (recordStatus.getText().toString().equalsIgnoreCase(CANCEL_RECORD)) {
                    recordStatus.setText(START_RECORD);
                    mIsrecording = false;
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clearGUI();
                        }
                    }, 500);

                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    if (mFilePCM.exists()) {
                        mFilePCM.delete();
                    }
                }
            }
        });
        /**
         * Playback button listner
         */
        mGoogleVoicePlayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFilePCM.length()>0){
                    mMediaPlayer = new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(mfileWAV);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getActivity(), R.string.toast_file_zero, Toast.LENGTH_SHORT).
                            show();
                }

            }
        });
        mAPIChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mStoredAPIKey = Utils.getStringSharedPreference(getActivity(),
                        Constants.PREF_GOOGLE_API_KEY);
                showCustumAlert(mStoredAPIKey, true);
            }
        });
        PCMConversionTask pcmConversionTask = new PCMConversionTask();
        pcmConversionTask.start();

        mGUIhandler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                byte[] PCMData=(byte[])msg.obj;
                displayHexData(getHexValue(PCMData));
            }
        };
        return rootView;
    }

    private void startRecordTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mIsrecording){
                    mIsrecording=false;
                    mGoogleVoiceRecord.post(new Runnable() {
                        @Override
                        public void run() {
                            mGoogleVoiceRecord.setText(START_RECORD);
                            createWavFile(mFilePCM, mfileWAV);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    displayHexData("The Complete PCM data of the recording " +
                                            "is saved at\n" + mfilePCM);
                                }
                            }, 1000);
                        }
                    });
                }

            }
        }, RECORD_TIME_OUT);
    }

    /**
     * Method to display a custom alert.
     * Option for entering the google key in the method for voice to
     * text conversion
     */
    private void showCustumAlert(String storedKey,boolean changeNeeded) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.api_key_dialog_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set api_key_dialog_alert.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        //User input Edittext
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.custom_alert_user_input);
        if(changeNeeded) {
            userInput.setText(storedKey);
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to sharedpreferecne
                                Utils.setStringSharedPreference(getActivity(),
                                        Constants.PREF_GOOGLE_API_KEY,userInput.getText().
                                                toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // showToast it
        alertDialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothLeService.registerBroadcastReceiver(getActivity(), mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.rdk_emulator_view);
    }

    @Override
    public void onDestroy() {
        BluetoothLeService.unregisterBroadcastReceiver(getActivity(), mGattUpdateReceiver);
        super.onDestroy();
    }

    /**
     * Method to update the GUI with received HEX value
     *
     * @param value
     */
    private void displayHexData(String value) {
        mHexValue.setText(value);
    }

    /**
     * Converting the byte array to Hex vale
     *
     * @param array
     * @return
     */
    private String getHexValue(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            sb.append(Utils.formatForRootLocale("%02x", byteChar));
        }
        return "" + sb;
    }
    void createPCMFile(byte[] data) {
        String filename = mfilePCM;
        FileOutputStream output;
        try {
            output = new FileOutputStream(filename, true);
            output.write(data);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void createWavFile(File fileToConvert,String wavFilePath){
        try {
            long SUB_CHUNK_SIZE = 16;
            int BITS_PER_SAMPLE= 16;
            int FORMAT = 1;
            long CHANNELS = 1;
            long SAMPLE_RATE = 16000;
            long BYTE_RATE = SAMPLE_RATE * CHANNELS * BITS_PER_SAMPLE/8;
            int myBlockAlign = (int) (CHANNELS * BITS_PER_SAMPLE/8);

            byte[] clipData = getBytesFromFile(fileToConvert);

            long myDataSize = clipData.length;
            long myChunk2Size =  myDataSize * CHANNELS * BITS_PER_SAMPLE/8;
            long myChunkSize = 36 + myChunk2Size;

            OutputStream os;
            os = new FileOutputStream(new File(wavFilePath));
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream outFile = new DataOutputStream(bos);

            outFile.writeBytes("RIFF");
            outFile.write(intToByteArray((int) myChunkSize), 0, 4);
            outFile.writeBytes("WAVE");
            outFile.writeBytes("fmt ");
            outFile.write(intToByteArray((int) SUB_CHUNK_SIZE), 0, 4);
            outFile.write(shortToByteArray((short) FORMAT), 0, 2);
            outFile.write(shortToByteArray((short) CHANNELS), 0, 2);
            outFile.write(intToByteArray((int) SAMPLE_RATE), 0, 4);
            outFile.write(intToByteArray((int) BYTE_RATE), 0, 4);
            outFile.write(shortToByteArray((short) myBlockAlign), 0, 2);
            outFile.write(shortToByteArray((short)BITS_PER_SAMPLE), 0, 2);
            outFile.writeBytes("data");
            outFile.write(intToByteArray((int) myDataSize), 0, 4);
            outFile.write(clipData);

            outFile.flush();
            outFile.close();
            mGoogleVoicePlayBack.setVisibility(View.VISIBLE);
            mGoogleVoiceConvert.setVisibility(View.VISIBLE);
            mFilePath.setVisibility(View.VISIBLE);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytesFromFile(File fileToConvert) {
        int size = (int) fileToConvert.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileToConvert));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
             e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.e("Read file Length"+bytes.length);
        return bytes;
    }
    private void clearGUI(){
        mHexValue.setText("");
        mConvertedTextValue.setText("");
    }
    private class googleVoiceConversion extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Converting audio Please wait...");
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... strings) {
            String convertedResult = "";
            String possibleOutcomes = "No possible text found";
            try {
                String apiKey = Utils.getStringSharedPreference(getActivity(),
                        Constants.PREF_GOOGLE_API_KEY);
                URL url = new URL("https://www.google.com/speech-api/v2/recognize?" +
                        "output=json&lang=en-us&key=" + apiKey);
                Logger.e("Api key---"+apiKey);
                Logger.e("URL---"+url);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                // add reuqest header
                con.setRequestMethod("POST");
                //for FLAC
                //  con.setRequestProperty("Content-Type", "audio/x-flac; rate=44100");
                //for PCM wav
                con.setRequestProperty("Content-Type", "audio/l16; rate=16000");
                con.setDoOutput(true);
                InputStream audioInputStream = null;
                try {
                    audioInputStream = new FileInputStream(mfileWAV);
                    byte[] audioData = getByteDataFromInputStream(audioInputStream);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.write(audioData);
                    wr.flush();
                    wr.close();
                } catch (Exception exception) {
                } finally {
                    if (audioInputStream != null) {
                        audioInputStream.close();
                    }
                }

                int responseCode = con.getResponseCode();
                InputStream inputStream = con.getInputStream();
                Reader in = new InputStreamReader(inputStream);
                BufferedReader bufferedreader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();
                String stringReadLine = null;
                while ((stringReadLine = bufferedreader.readLine()) != null) {
                    stringBuilder.append(stringReadLine + "\n");
                }
                System.out.println(" response : " + stringBuilder);
                String result = stringBuilder.toString().replace("{\"result\":[]}\n", "");
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("result");
                Logger.i("JSON array--->result-->" + jsonArray);
                for (int count = 0; count < jsonArray.length(); count++) {
                    JSONObject jsonAlternative = jsonArray.getJSONObject(count);
                    JSONArray jsonAlternativesArray = jsonAlternative.getJSONArray("alternative");
                    Logger.i("JSON array--->alternative-->" + jsonArray);
                    for (int pos = 0; pos < jsonAlternativesArray.length(); pos++) {
                        JSONObject jsonTranscript = jsonAlternativesArray.getJSONObject(pos);
                        String transcript = "";
                        if (jsonTranscript.has("transcript")) {
                            transcript = jsonTranscript.getString("transcript");
                            if (jsonTranscript.has("confidence")) {
                                convertedResult = transcript;
                            } else {
                                possibleOutcomes = transcript + "\n";
                            }
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (convertedResult.equalsIgnoreCase("")) {
                return possibleOutcomes;
            } else {
                return convertedResult;
            }


        }

        private byte[] getByteDataFromInputStream(InputStream inputStream) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[PACKETSIZE];

            try {
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toByteArray();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            mConvertedTextValue.setText(result);
        }
    }
    private class PCMConversionTask extends Thread{
        byte[] mPCMData;

        @Override
        public void run() {
            Looper.prepare();
            mTaskandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(mIsrecording){
                        byte[] rawData=(byte[])msg.obj;
                        mPCMData=ADPCMConverter.getPCMData(rawData);
                        Message pcmData=Message.obtain();
                        pcmData.obj=mPCMData;
                        mGUIhandler.sendMessage(pcmData);
                       if (mPCMData != null){
                                createPCMFile(mPCMData);
                        }
                    }
                }
            };

            // Run the message queue in this thread
            Looper.loop();
        }
    }

}


