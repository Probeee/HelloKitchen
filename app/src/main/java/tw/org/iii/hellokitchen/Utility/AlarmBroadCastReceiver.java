package tw.org.iii.hellokitchen.Utility;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import tw.org.iii.hellokitchen.Activity.ActRealMain;
import tw.org.iii.hellokitchen.R;

import static android.R.attr.id;

/**
 * Created by iii on 2017/6/8.
 */


public class AlarmBroadCastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra("notification");
        int id = intent.getIntExtra("notification_id",1);
        notificationManager.notify(id, notification);
    }
}
