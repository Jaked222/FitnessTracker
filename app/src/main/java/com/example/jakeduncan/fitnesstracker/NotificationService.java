package com.example.jakeduncan.fitnesstracker;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by jakeduncan on 10/26/16.
 */

public class NotificationService extends IntentService {


    public NotificationService() {
        super("Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("asd", "onHandleIntent: getting in");


        makeNotification();

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        alarm.cancel(pintent);
        //wait an hour before calling this service again, repeating the notification.
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
                + 60000 * 60, pintent);

    }

    public void makeNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_cast_grey)
                        .setContentTitle("Office Mode")
                        .setContentText("Get up and walk around!");

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int mNotificationId = 001;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }
}