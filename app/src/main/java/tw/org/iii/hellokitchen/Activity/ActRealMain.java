package tw.org.iii.hellokitchen.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import tw.org.iii.hellokitchen.Frag_Ingredients.Frag_Foods_Container;
import tw.org.iii.hellokitchen.Frag_Recipe.Frag_Recipe_Container;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.TheDefined;

public class ActRealMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        } else if (id == R.id.nav_repair)
        {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Frag_Foods_Container frag_foods_container;
    Frag_Recipe_Container frag_recipe_container;
    FragmentManager fragMgr ;
    FragmentTransaction fragmentTransaction;

}
