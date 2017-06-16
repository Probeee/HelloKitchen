package tw.org.iii.hellokitchen.Activity;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import tw.org.iii.hellokitchen.Frag_Company.Frag_Company_Container;
import tw.org.iii.hellokitchen.Frag_Ingredients.Frag_Foods_Container;
import tw.org.iii.hellokitchen.Frag_Recipe.Frag_Recipe_Container;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.AlarmBroadCastReceiver;
import tw.org.iii.hellokitchen.Utility.TheDefined;

import static android.content.DialogInterface.BUTTON_NEGATIVE;

public class ActRealMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private SharedPreferences table_time;
    private AlarmManager am;
    private java.util.Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__foods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化三個主頁面
        frag_foods_container = new Frag_Foods_Container();
        frag_recipe_container = new Frag_Recipe_Container();
        frag_company_container = new Frag_Company_Container();
        //一開始先顯示食材頁面
        fragMgr = getFragmentManager();
        fragmentTransaction = fragMgr.beginTransaction();
        fragmentTransaction.add(R.id.fragment_main_of_three_container,frag_foods_container).commit();

        //左方導覽列初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        View hview = navigationView.getHeaderView(0);
        try
        {
            Bundle bundle = getIntent().getExtras();
            String user_name = bundle.getString(TheDefined.LOGIN_USER_NAME);
            String user_mail = bundle.getString(TheDefined.LOGIN_USER_MAIL);

            TextView nav_user_name = (TextView)hview.findViewById(R.id.textView_nav_header_name);
            TextView nav_user_mail = (TextView)hview.findViewById(R.id.textView_nav_header_email);

            nav_user_name.setText(user_name);
            nav_user_mail.setText(user_mail);
        }
        catch (Exception e)
        {

        }
        //FAB初始化
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(ActRealMain.this, ActRecipeUpload.class));
            }
        });

        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = java.util.Calendar.getInstance();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act__foods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_time)
        {
            //產生TimePickerDialog 物件 初始顯示時間為現在
            TimePickerDialog message = new TimePickerDialog(this, myTimeSetListener, java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY),
                    java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE),true);
            message.setButton(BUTTON_NEGATIVE, "關閉提醒", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    //關閉鬧鐘功能
                    Intent intent = new Intent(ActRealMain.this, AlarmBroadCastReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            ActRealMain.this, 0, intent, 0);
                    //取消該Intent
                    am.cancel(pendingIntent);
                    Toast.makeText(ActRealMain.this,"鬧鐘已取消！",Toast.LENGTH_SHORT).show();
                }
            });

            message.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            //設定呼叫時間
            String hours ;
            String minutes ;
            String time ;
            // TODO Auto-generated method stub
            if(hourOfDay<10)
            {
                hours = "0" + String.valueOf(hourOfDay);
            }
            else
            {
                hours = String.valueOf(hourOfDay);
            }
            if(minute<10)
            {
                minutes = "0"+String.valueOf(minute);
            }
            else
            {
                minutes =  String.valueOf(minute);
            }
            time = hours + ":" +  minutes ;
            Toast.makeText(ActRealMain.this,time,Toast.LENGTH_SHORT).show();
            scheduleNotification(hourOfDay,minute);


        }
    };

    private void scheduleNotification(int hour,int min)
    {
        //設定時間
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
        calendar.set(java.util.Calendar.MINUTE, min);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        //加入notification
        Intent notificationIntent = new Intent(this, AlarmBroadCastReceiver.class);

        Notification notification = getNotification();
        notificationIntent.putExtra("notification_id", 1);
        notificationIntent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), (24 * 60 * 60 * 1000),pendingIntent);//am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 1000, (24 * 60 * 60 * 1000),pendingIntent);
    }

    private Notification getNotification()
    {
        Notification.Builder builder = new Notification.Builder(ActRealMain.this);
        builder.setContentTitle("過期通知");
        builder.setContentText("您有過期食材");
        builder.setSmallIcon(R.drawable.account_icon);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return builder.build();
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ingredients)
        {
            //選食材
            fragMgr = getFragmentManager();
            fragmentTransaction = fragMgr.beginTransaction();
           // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_main_of_three_container,frag_foods_container).commit();
        }
        else if (id == R.id.nav_recipe)
        {
            // 選食譜
            fragMgr = getFragmentManager();
            fragmentTransaction = fragMgr.beginTransaction();
           // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_main_of_three_container,frag_recipe_container).commit();
        }
        else if (id == R.id.nav_repair)
        {
            fragMgr = getFragmentManager();
            fragmentTransaction = fragMgr.beginTransaction();
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_main_of_three_container,frag_company_container).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Frag_Foods_Container frag_foods_container;
    Frag_Recipe_Container frag_recipe_container;
    Frag_Company_Container frag_company_container;
    FragmentManager fragMgr ;
    FragmentTransaction fragmentTransaction;


}
