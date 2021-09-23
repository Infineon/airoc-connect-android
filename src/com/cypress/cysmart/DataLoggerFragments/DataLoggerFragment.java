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

package com.cypress.cysmart.DataLoggerFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.cypress.cysmart.CommonUtils.Constants;
import com.cypress.cysmart.CommonUtils.Logger;
import com.cypress.cysmart.CommonUtils.Utils;
import com.cypress.cysmart.HomePageActivity;
import com.cypress.cysmart.ListAdapters.DataLogsListAdapter;
import com.cypress.cysmart.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragment to showToast the DataLogger
 */
public class DataLoggerFragment extends Fragment implements AbsListView.OnScrollListener {
    /**
     * FilePath of DataLogger
     */
    private static String mFilepath;
    int mTotalLinesToRead = 0;
    ProgressDialog mProgressDialog;
    /**
     * Log Data Temporay storage
     */
    ArrayList<String> mReadLogData;
    /**
     * List Adapter
     */
    DataLogsListAdapter mAdapter;
    /**
     * visibility flag
     */
    private boolean mVisible = false;
    /**
     * DataLogger text
     */
    private ListView mLogList;
    /**
     * Lazyloading variables
     */
    private int mStartLine = 0;
    private int mStopLine = 500;
    private boolean mLazyLoadingEnabled = false;
    /**
     * GUI elements
     */
    private TextView mFileName;
    private Button mScrollDown;

    //Activity Request Code
    private static final int REQUEST_CODE = 123;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.datalogger, container, false);
        mLogList = (ListView) rootView.findViewById(R.id.txtlog);
        mFileName = (TextView) rootView.findViewById(R.id.txt_file_name);
        mScrollDown = (Button) rootView.findViewById(R.id.btn_scroll_down);

        /*
        /History option text
        */
        TextView mDataHistory = (TextView) rootView.findViewById(R.id.txthistory);
        //mScrollView = (CustumScrollView) rootView.findViewById(R.id.scroll_view_logger);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mFilepath = bundle.getString(Constants.DATA_LOGGER_FILE_NAME);
            mVisible = bundle.getBoolean(Constants.DATA_LOGGER_FLAG);
            File fileInView = new File(mFilepath);
            mFileName.setText(fileInView.getName());
        }
        // Handling the history text visibility based on the received Arguments
        if (mVisible) {
            mDataHistory.setVisibility(View.GONE);
        } else {
            Toast.makeText(getActivity(), getResources().
                            getString(R.string.data_logger_timestamp) + Utils.GetTimeandDateUpdate()
                    , Toast.LENGTH_SHORT).show();
            mDataHistory.setVisibility(View.VISIBLE);
        }
        mDataHistory.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent dataloggerHistory = new Intent(getActivity(), DataLoggerHistoryList.class);
                startActivityForResult(dataloggerHistory, REQUEST_CODE);
            }
        });
        prepareData();
        setHasOptionsMenu(true);
        mScrollDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogList.post(new Runnable() {
                    public void run() {
                        mLogList.setSelection(mLogList.getCount() - 1);
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundleReceived = data.getExtras();
            mFilepath = bundleReceived.getString(Constants.DATA_LOGGER_FILE_NAME);
            mVisible = bundleReceived.getBoolean(Constants.DATA_LOGGER_FLAG);
            File fileinView = new File(mFilepath);
            mFileName.setText(fileinView.getName());
            prepareData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(false);
        Utils.setUpActionBar((AppCompatActivity) getActivity(), R.string.data_logger);
    }

    @Override
    public void onPause() {
        if (!mReadLogData.isEmpty()) {
            mStartLine = 0;
            mStopLine = 500;
        }
        super.onPause();
    }

    public void prepareData() {
        mTotalLinesToRead = getTotalLines();
        mReadLogData = new ArrayList<String>();
        mAdapter = new DataLogsListAdapter(getActivity(), mReadLogData);
        mLogList.setAdapter(mAdapter);
        mProgressDialog = new ProgressDialog(getActivity());
        //scrollMyListViewToBottom();
        if (mTotalLinesToRead > 5000) {
            // mLogList.setOnScrollListener(this);
            mLazyLoadingEnabled = true;
            loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
            Logger.e("Start Line>> " + mStartLine + "Stop Line>>" + mStopLine);
            loadLogdata.execute();
            mProgressDialog.setTitle(
                    getResources().
                            getString(R.string.app_name));
            mProgressDialog.setMessage(getResources().
                    getString(R.string.alert_message_log_read));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        } else {
            mLazyLoadingEnabled = false;
            loadLogdata loadLogdata = new loadLogdata(0, 0);
            loadLogdata.execute();
        }
    }

    /**
     * Reading the data from the file stored in the FilePath
     *
     * @return {@link String}
     * @throws FileNotFoundException
     */
    private ArrayList<String> logdata() throws FileNotFoundException {
        File file = new File(mFilepath);
        ArrayList<String> dataLines = new ArrayList<String>();
        if (!file.exists()) {
            return dataLines;
        } else {

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    dataLines.add(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataLines;
        }

    }

    /**
     * Reading the data from the file stored in the FilePath for particular set of lines
     *
     * @return {@link String}
     * @throws FileNotFoundException
     */
    private ArrayList<String> logdata(int startLine, int stopLine) throws FileNotFoundException {
        File file = new File(mFilepath);
        ArrayList<String> dataLines = new ArrayList<String>();
        if (!file.exists()) {
            return dataLines;
        } else {
            BufferedReader buffreader = new BufferedReader(new FileReader(file));
            String line;
            int lines = 0;
            try {
                while ((line = buffreader.readLine()) != null) {
                    lines++;
                    if (lines > startLine && lines <= stopLine) {
                        dataLines.add(line);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return dataLines;
        }
    }

    /**
     * Method to count the total lines in the selected file
     *
     * @return totalLines
     */
    public int getTotalLines() {
        int totalLines = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFilepath));
            while ((bufferedReader.readLine()) != null) {
                totalLines++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalLines;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (mLogList.getLastVisiblePosition() >= mLogList.getCount() - 1 - 0) {
                //load more list items:
                if (mLazyLoadingEnabled) {
                    mStartLine = mStopLine;
                    mStopLine = mStopLine + 500;
                    if (mStopLine < mTotalLinesToRead) {
                        loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
                        loadLogdata.execute();
                    } else {
                        loadLogdata loadLogdata = new loadLogdata(mStartLine, mTotalLinesToRead);
                        loadLogdata.execute();
                        mLazyLoadingEnabled = false;
                    }
                }

            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.global, menu);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem sharelogger = menu.findItem(R.id.sharelogger);
        MenuItem log = menu.findItem(R.id.log);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem graph = menu.findItem(R.id.graph);

        search.setVisible(false);
        share.setVisible(false);
        log.setVisible(false);
        graph.setVisible(false);
        sharelogger.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.sharelogger:
                shareDataLoggerFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sharing the data logger txt file
     */
    private void shareDataLoggerFile() {
        HomePageActivity.mContainerView.invalidate();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        // the attachment
        Uri contentUri = FileProvider.getUriForFile(getActivity(), getActivity().getResources().getString(R.string.authority_fileprovider), new File(mFilepath));
        emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Data Logger File");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /**
     * AsyncTask class for loading logger data
     */
    private class loadLogdata extends AsyncTask<Void, Void, ArrayList<String>> {
        int startLine = 0;
        int stopLine = 0;
        ArrayList<String> newData = new ArrayList<String>();

        public loadLogdata(int startLine, int stopLine) {
            this.startLine = startLine;
            this.stopLine = stopLine;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected ArrayList<String> doInBackground(Void... params) {
            try {
                if (startLine == 0 && stopLine == 0) {
                    newData = logdata();
                } else {
                    newData = logdata(startLine, stopLine);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            return newData;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            mReadLogData.addAll(result);
            //load more list items:
            if (mLazyLoadingEnabled) {
                mStartLine = mStopLine;
                mStopLine = mStopLine + 500;
                if (mStopLine < mTotalLinesToRead) {
                    loadLogdata loadLogdata = new loadLogdata(mStartLine, mStopLine);
                    loadLogdata.execute();
                } else {
                    loadLogdata loadLogdata = new loadLogdata(mStartLine, mTotalLinesToRead);
                    loadLogdata.execute();
                    mLazyLoadingEnabled = false;
                    mProgressDialog.dismiss();
                }
            } else {
                mProgressDialog.dismiss();
            }
            Logger.i("Total size--->" + mReadLogData.size());
            mAdapter.addData(mReadLogData);
            mAdapter.notifyDataSetChanged();
        }
    }

}
