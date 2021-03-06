package com.example.jakeduncan.fitnesstracker;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

//import android.location.Location;

//import android.location.Location;

public class UserActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {


    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     * A longer interval is used to help eliminate the margin of error with the current latitude/longitude
     * implementation. If this is sorted out, a shorter interval could be used to help be more accurate.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;

    protected Button leaderBoardButton;
    protected Button officeModeOn;
    protected Button officeModeOff;
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView distanceCalcView;
    protected TextView distanceView;
    protected TextView dailyView;

    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    Calendar calendar = Calendar.getInstance();
    private int day = calendar.get(Calendar.DAY_OF_WEEK);
    private float dailyDistance = 0;
    private double curr;
    private double interval = 304.8;
    private double prev;
    private double oldLat;
    private double oldLong;
    public boolean isFirst = true;
    public float[] results = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView userView = (TextView) findViewById(R.id.userView);
        distanceView = (TextView) findViewById(R.id.distanceView);
        distanceCalcView = (TextView) findViewById(R.id.distanceCalcView);
        dailyView = (TextView) findViewById(R.id.dailyView);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("namekey");

        userView.setText(userName);
        String distanceViewText = "Walked: " + getUserDistance(userName) + "m";
        distanceView.setText(distanceViewText);

        leaderBoardButton = (Button) findViewById(R.id.leaderBoardButton);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, LeaderboardShow.class);
                startActivity(intent);
            }
        });

        officeModeOff = (Button) findViewById(R.id.officeModeOff);
        officeModeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationService.turnOnOrOff(false);
            }
        });

        officeModeOn = (Button) findViewById(R.id.officeModeOn);
        officeModeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("officebutton", "onClick: here");
                NotificationService.turnOnOrOff(true);
                Intent notificationIntent = new Intent(UserActivity.this, NotificationService.class);
                startService(notificationIntent);
            }
        });


        mStartUpdatesButton = (Button) findViewById(R.id.startWalkingButton);
        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRequestingLocationUpdates) {
                    mRequestingLocationUpdates = true;
                    setButtonsEnabledState();
                    startLocationUpdates();
                }
            }
        });

        mStopUpdatesButton = (Button) findViewById(R.id.stopWalkingButton);
        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRequestingLocationUpdates) {
                    mRequestingLocationUpdates = false;
                    setButtonsEnabledState();
                    stopLocationUpdates();
                    //these two are used to help with calculating the distance between
                    //lat/long for the walker. See comments on checkDistanceBetween method.
                    results[0] = 0;
                    isFirst = true;
                }
            }
        });


        mLatitudeTextView = (TextView) findViewById(R.id.latitudeView);
        mLongitudeTextView = (TextView) findViewById(R.id.longitudeView);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.timeView);

        // Set labels.
        mLatitudeLabel = "Latitude: ";
        mLongitudeLabel = "Longitude: ";
        mLastUpdateTimeLabel = "Last time updated: ";

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            Log.d(TAG, "startLocationUpdates: exception");
        }
    }

    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    //updates UI
    private void updateUI() {
        mLatitudeTextView.setText(String.format("%s: %f", mLatitudeLabel,
                mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.format("%s: %f", mLongitudeLabel,
                mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                mLastUpdateTime));

        //isFirst is assigned to false after the first run of this method.
        checkDistanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), isFirst);
        checkMilestone(getIntent().getStringExtra("namekey"));

        isFirst = false;
    }


    /**
     * Checks the distance between the latitudes at each update. The isFirst boolean here is needed
     * to set the initial start point for lat and long. This is how I have a starting point for walking.
     * when the stop button is pressed, this is set back to true. The reason for this is that if a person
     * presses start, then stop, then moves to a far off location, and presses start again, we do not want
     * the app to calculate the distance between the first and second mentioned locations.
     */
    public void checkDistanceBetween(double startLatitude, double startLongitude, boolean firstRun) {

        if (firstRun) {
            oldLat = startLatitude;
            oldLong = startLongitude;
        } else {
            //this calculates the distance between old/new lat and long, then adds
            //the value to results[0]. If there's already a value there, it adds them together.
            mCurrentLocation.distanceBetween(oldLat, oldLong, startLatitude, startLongitude, results);
        }
        Log.d(TAG, "results[0]: " + results[0]);

        String distanceCalcText = "Travelled(since last update): " + String.valueOf(results[0] + "m");
        distanceCalcView.setText(distanceCalcText);
        writeDistanceToDatabase(results[0], getIntent().getStringExtra("namekey"));
        updateDailyStats(getIntent().getStringExtra("namekey"), results[0]);
    }

    public void writeDistanceToDatabase(float distance, String user) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase writableDB = databaseHelper.getWritableDatabase();

        float newTotalDistance = getUserDistance(user) + distance;

        try {

            writableDB.execSQL("UPDATE users SET distance=" + newTotalDistance +
                    " WHERE " + UserTable.NAME + " = " + "\"" + user + "\"");

        } finally {
            Log.d(TAG, String.valueOf(getUserDistance(user)));
            String distanceViewText = "Walked: " + getUserDistance(user) + "m";
            distanceView.setText(distanceViewText);


            writableDB.close();
            databaseHelper.close();
        }
    }

    public void checkMilestone(String user) {
        if (isFirst) {
            prev = getUserDistance(user);
        }
        curr = getUserDistance(user);
        if (Math.floor(curr / interval) > Math.floor(prev / interval)) {
            milestonePassed();
        }
        prev = curr; // For next round
    }

    public void milestonePassed() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_cast_grey)
                        .setContentTitle("Milestone")
                        .setContentText("Walked 1000 feet! Good job!");

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int mNotificationId = 001;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }

    //NOTE:: IMPLEMENTATION FLAWED. Day will not change if app is'nt running during the swap from
    //day 1 to day 2.
    public void updateDailyStats(String user, float walked) {
        calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        if (today != day) {
            //set user daily stat to 0
            day = today;

            dailyDistance = 0;
            addStatToDatabase(dailyDistance, user);
            dailyView.setText("daily walked:" + String.valueOf(getUserStat(user)));
        } else {
            dailyDistance = getUserStat(user) + walked;

            addStatToDatabase(dailyDistance, user);
            dailyView.setText("daily walked:" + String.valueOf(getUserStat(user)));
        }
    }

    public void addStatToDatabase(float stat, String user) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase writableDB = databaseHelper.getWritableDatabase();


        try {

            writableDB.execSQL("UPDATE users SET daily=" + stat +
                    " WHERE " + UserTable.NAME + " = " + "\"" + user + "\"");

        } finally {
            Log.d(TAG, "addStatToDatabase:" + getUserStat(user));
            writableDB.close();
            databaseHelper.close();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {

            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                Log.d(TAG, "onConnected:" + mCurrentLocation);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateUI();
            } catch (SecurityException e) {
                Log.d(TAG, "onConnected: exception");
            }


        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, "location updated",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    public float getUserStat(String userName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        float stat = 0;
        try {

            cursor = dataBase.rawQuery("SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.NAME + " = " + "\"" + userName + "\"", null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                stat = cursor.getFloat(cursor.getColumnIndex(UserTable.DAILY));
            }

            return stat;
        } finally {

            if (cursor != null) {
                cursor.close();
                databaseHelper.close();
                dataBase.close();
            }
        }
    }


    public float getUserDistance(String userName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase dataBase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        float distance = 0;
        try {

            cursor = dataBase.rawQuery("SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.NAME + " = " + "\"" + userName + "\"", null);

            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                distance = cursor.getFloat(cursor.getColumnIndex(UserTable.DISTANCE));
            }

            return distance;
        } finally {

            if (cursor != null) {
                cursor.close();
                databaseHelper.close();
                dataBase.close();
            }
        }
    }
}
