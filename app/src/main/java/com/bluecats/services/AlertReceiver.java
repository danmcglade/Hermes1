package com.bluecats.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by dgurun00c on 11/27/2016.
 */

public class AlertReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Times Up ", "5 Seconds Has Passed", "Alert");
    }

    private void createNotification(Context context, String msg, String msgText, String msgAlert) {

        PendingIntent notificIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.drawable.ic_launcher1);
        mBuilder.setContentTitle(msg);
        mBuilder.setContentText(msgText);
        mBuilder.setTicker(msgAlert);



        mBuilder.setContentIntent(notificIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        mBuilder.setAutoCancel(true);

        NotificationManager nNotificationManager =
                (NotificationManager ) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nNotificationManager.notify(1, mBuilder.build());
    }
}
