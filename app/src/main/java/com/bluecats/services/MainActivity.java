package com.bluecats.services;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BCTriggeredEvent;
import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.services.interfaces.BlueCatsSDKInterfaceService;
import com.bluecats.services.interfaces.IBlueCatsSDKInterfaceServiceCallback;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * The type Main activity.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private TextView mTxtMessage;
    private TextView mTxtMessage1;
    //private
    private Button mButton;
    private Button mButton2;
    private Button  sendEmail;          // button to send email.
    private Map<BCBeacon, AtomicBoolean> beaconMap = new HashMap<BCBeacon, AtomicBoolean>(); // Map to hold beacon details
                                                            // having beacon and atomic boolean and key, value pair
    private String beaconName = "";


    /**
     * The Notification manager.
     */
    NotificationManager notificationManager;

    /**
     * The Is notific active.
     */
    boolean isNotificActive = false;

    /**
     * The Notif id.
     */
    int notifID = 33;

    /**
     * The M bc beacon manager.
     */
    BCBeaconManager mBCBeaconManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sendEmail = (Button) findViewById(R.id.sendEmail);


        mTxtMessage = (TextView)findViewById(R.id.txt_message);
        mTxtMessage1 = (TextView)findViewById(R.id.txt_message1);
        mButton = (Button)findViewById(R.id.btn);
        mButton2 = (Button)findViewById(R.id.btn2);
        mButton.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        final EditText beaconNameET = (EditText) findViewById(R.id.BeaconNameET);           // Edit text to enter beacon name
        Button submitButton = (Button)findViewById(R.id.SubmitButton);                      // Button to submit beacon name

        // Anonymous class to perform action when submitButton is clicked.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconName = beaconNameET.getText().toString();     // getting the string from user and casting it to String

                beaconNameET.setText("");       // resetting the edit text back to empty string ""

            }
        });

        MainApplication.runSDK(this.getApplicationContext());
        mBCBeaconManager = new BCBeaconManager();
        mBCBeaconManager.registerCallback(mCallback);

        sendEmail.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                sendEmail();
            }
        });

    }


    /**
     * Send email.
     */
// Method to send Email  @https://www.tutorialspoint.com/android/android_sending_email.htm
    protected void sendEmail() {

        Log.i("Send email", "");
        String[] TO = {"xyz@gmail.com"};
        String[] CC = {"abc@gmail.com"};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,TO);
        emailIntent.putExtra(Intent.EXTRA_CC,CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Beacon found");
        emailIntent.putExtra(Intent.EXTRA_TEXT,"Beacon with patient found");

        try {
            startActivity(Intent.createChooser(emailIntent,"Send mail ...."));
            finish();
            Log.i("Finished sending email ", " ");
        }

        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * The M callback.
     */
    BCBeaconManagerCallback mCallback = new BCBeaconManagerCallback() {
        @Override
        public void didEnterSite(BCSite site) {
            super.didEnterSite(site);
            Log.d(TAG, "didEnterSite "+site.getName());
        }

        @Override
        public void didExitSite(BCSite site) {
            super.didExitSite(site);
            Log.d(TAG, "didExitSite "+site.getName());
        }

        @Override
        public void didDetermineState(BCSite.BCSiteState state, BCSite forSite) {
            super.didDetermineState(state, forSite);
            Log.d(TAG, "didDetermineState "+forSite.getName());
        }


        // Method used to track specific beacon.
        @Override
        public void didEnterBeacons(List<BCBeacon> beacons) {
            super.didEnterBeacons(beacons);

            Log.d(TAG, "didEnterBeacons "+getBeaconNames(beacons));


            // Enhanced for loop to go through "beacons" array.
            for(BCBeacon beacon : beacons) {
                // Checking for specific beacon
                if (beacon.getName().equalsIgnoreCase(beaconName)) {

                    CheckBox rangeCB = (CheckBox) findViewById(R.id.rangeCB); // Set the range check Box

                    rangeCB.setText("In range of " + beaconName);   // Display the text message when specific beacon is in range.

                    rangeCB.setChecked(true);       // Setting the check mark to true


                    if (beaconMap.containsKey(beacon))

                    {
                        beaconMap.get(beacon).set(true);
                    }

                    else
                    {
                        beaconMap.put(beacon,new AtomicBoolean(true));
                    }

//                    new Thread(){
//                        @Override
//                        public void run(){
//                            try {
//                                Thread.sleep(10000); // 1 Second (i think)
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            sendEmail();
//                        }
//                    }.start();  // might be .start();
                    //new SmsAsyncTask().execute("In range of beacon 5000050e");
                    //sendEmail();
                }
            }

//            for(BCBeacon beacon : beacons) {
//                if (beacon.getName().equalsIgnoreCase("Hermes_Deepak_1")) {
//
//                    //new SmsAsyncTask().execute("In range of beacon 5000050e");
//                    sendEmail();
//                }
//            }



        }


        // Method to track exiting beacon
        @Override
        public void didExitBeacons(List<BCBeacon> beacons) {
            super.didExitBeacons(beacons);
            Log.d(TAG, "didExitBeacons "+getBeaconNames(beacons));

            // Enhanced for loop to go through "beacons" array.
            for (BCBeacon beacon : beacons)
            {
                // Checking for condition where beacon is out of range and beaconMap has the key.
                if (beacon.getName().equalsIgnoreCase(beaconName) && beaconMap.containsKey(beacon))
                {

                    CheckBox rangeCB = (CheckBox) findViewById(R.id.rangeCB); // Set the check box for exiting becon

                    rangeCB.setText("Out of range of " + beaconName); // Displaying the text message when beacon is out of range.

                    rangeCB.setChecked(false);          // Removing the check mark
                    beaconMap.get(beacon).set(false);   // setting the value of beaconMap to false when beacon is out of range
                    TextView timerTV = (TextView) findViewById(R.id.timerTV); // Setting the time out countdown
                    timerTV.setVisibility(View.VISIBLE);    // setting the time out countdown to visible
                    new ExitTimerAsyncTask(beacon,timerTV).execute(); // executing the ExitTimerAsyncTask in back ground when time out counter is over to send email.
                }
            }
        }

        @Override
        public void didDetermineState(BCBeacon.BCBeaconState state, BCBeacon forBeacon) {
            super.didDetermineState(state, forBeacon);
            Log.d(TAG, "didDetermineState "+forBeacon.getSerialNumber());
        }

        @Override
        public void didRangeBeacons(List<BCBeacon> beacons) {
            super.didRangeBeacons(beacons);
            Log.d(TAG, "didRangeBeacons "+getBeaconNames(beacons));
        }

        @Override
        public void didRangeBlueCatsBeacons(List<BCBeacon> beacons) {
            super.didRangeBlueCatsBeacons(beacons);
            Log.d(TAG, "didRangeBlueCatsBeacons "+getBeaconNames(beacons));


        }

        @Override
        public void didRangeNewbornBeacons(List<BCBeacon> newBornBeacons) {
            super.didRangeNewbornBeacons(newBornBeacons);
            Log.d(TAG, "didRangeNewbornBeacons "+getBeaconNames(newBornBeacons));
        }



        @Override
        public void didRangeEddystoneBeacons(List<BCBeacon> eddystoneBeacons) {
            super.didRangeEddystoneBeacons(eddystoneBeacons);
            Log.d(TAG, "didRangeEddystoneBeacons "+getBeaconNames(eddystoneBeacons));
        }

        @Override
        public void didDiscoverEddystoneURL(URL eddystoneUrl) {
            super.didDiscoverEddystoneURL(eddystoneUrl);
            Log.d(TAG, "didDiscoverEddystoneURL "+eddystoneUrl.toString());
        }
    };

    private String getBeaconNames(List<BCBeacon> beacons) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (BCBeacon beacon: beacons) {
            sb.append(beacon.getSerialNumber());
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BlueCatsSDKInterfaceService.registerBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName(), mBlueCatsSDKInterfaceServiceCallback);

        BlueCatsSDKInterfaceService.didEnterForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();

        BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());

        BlueCatsSDKInterfaceService.didEnterBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mBCBeaconManager.unregisterCallback(mCallback);
        BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());

        BlueCatsSDKInterfaceService.didEnterBackground();
    }

    private IBlueCatsSDKInterfaceServiceCallback mBlueCatsSDKInterfaceServiceCallback = new IBlueCatsSDKInterfaceServiceCallback() {
        @Override
        public void onDidEnterSite(BCSite site) {



        }

        @Override
        public void onDidExitSite(BCSite site) {



        }

        @Override
        public void onDidUpdateNearbySites(List<BCSite> sites) {

        }

        @Override
        public void onDidRangeBeaconsForSiteID(BCSite site, List<BCBeacon> beacons) {

        }

        @Override
        public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {

        }

        @Override
        public void onTriggeredEvent(BCTriggeredEvent triggeredEvent) {
            final BCBeacon beacon = triggeredEvent.getFilteredMicroLocation().getBeacons().get(0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtMessage.setText("Closest to beacon " + beacon.getSerialNumber());

                }
            });
        }
    };




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn) {

        } else if (v.getId() == R.id.btn2) {

        }
    }

    /**
     * Sets alarm.
     *
     * @param v the v
     */
    public void setAlarm (View v) {

        Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;

        Intent alertIntent = new Intent(this, AlertReceiver.class);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,alertTime,PendingIntent.getBroadcast(this,1, alertIntent,PendingIntent.FLAG_UPDATE_CURRENT));
    }


    /**
     * The type Exit timer async task.
     */
// inner class to have the sendEmail method called in background when beacon exit out of range
    class ExitTimerAsyncTask extends AsyncTask <Void, String, Void>


    {
        /**
         * The Beacon.
         */
        BCBeacon beacon;
        /**
         * The Timer tv.
         */
        TextView timerTV;
        /**
         * The Wait time.
         */
        int waitTime = 5;

        /**
         * Instantiates a new Exit timer async task.
         *
         * @param beacon  the beacon
         * @param timerTV the timer tv
         */
/*
        @param beacon
        @param  timerTV
        ExitTimerAsycTask constructor
         */
        public ExitTimerAsyncTask (BCBeacon beacon, TextView timerTV)
        {
            this.beacon = beacon;
            this.timerTV = timerTV;

        }

        @Override

        /*
        doInBackground method for sleep time and setting the count down.
         */
        public Void doInBackground(Void ... params)
        {

            try {
                while (waitTime>0)
                {
                    Thread.sleep(1000);
                    waitTime--;
                    publishProgress(Integer.toString(waitTime));
                }
                 // 1 Second (i think)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;

        }

        /*
        onProgressUpdate : To display the time out countdown.
         */
        @Override
        public void onProgressUpdate(String ... params)
        {
            timerTV.setText(params[0]);
        }


        /*
        onPostExecute - Method to trigger sendEmail method
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!beaconMap.get(beacon).get() )

            {
                sendEmail();
            }

            timerTV.setVisibility(View.INVISIBLE);

        }
    }


}

