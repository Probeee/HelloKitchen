package tw.org.iii.hellokitchen.Utility;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import tw.org.iii.hellokitchen.Activity.ActMain;
import tw.org.iii.hellokitchen.Activity.ActRealMain;
import tw.org.iii.hellokitchen.Entity.Ingredients;
import tw.org.iii.hellokitchen.R;


/**
 * Created by iii on 2017/6/8.
 */


public class AlarmBroadCastReceiver extends BroadcastReceiver
{
    PendingIntent pendingIntent;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
       // Notification notification = intent.getParcelableExtra("notification");
        int id = intent.getIntExtra("notification_id",1);

        int count = FindCount(context);
        this.context = context;
        Log.d("Count",String.valueOf(count));
        if(count>0)
        {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ActMain.class), 0);
            Notification notification = getNotification();
            notificationManager.notify(id, notification);

        }
        else
          notificationManager.cancel(id);
    }

    private Notification getNotification()
    {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("過期通知");
        builder.setContentText("您有食材已過期，記得清理喔!");
        builder.setSmallIcon(R.drawable.bell);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }


    public int FindCount(Context c)
    {
        int count = 0;
        MyDBHelper myDBHelper = MyDBHelper.getInstance(c);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.query("tingredients", null, null, null, null, null, null);

        while (cursor.moveToNext())
        {
            String _id = cursor.getString(0);
            String name = cursor.getString(1);
            String startDate = cursor.getString(2);
            String endDate = cursor.getString(3);
            String amount = cursor.getString(4);
            String member = cursor.getString(5);
            Ingredients i  = new Ingredients(_id, name, startDate, endDate,amount,member);

            if(i.getTime()<0)
            {
                count++;
            }
        }
        return count;
    }

}
