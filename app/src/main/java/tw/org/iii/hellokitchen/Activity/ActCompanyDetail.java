package tw.org.iii.hellokitchen.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;


import tw.org.iii.hellokitchen.Frag_Company.Frag_CompanyDetail_Gallery;
import tw.org.iii.hellokitchen.Frag_Company.Frag_CompanyDetail_Info;

import tw.org.iii.hellokitchen.Frag_Company.Frag_CompanyDetail_Map;
import tw.org.iii.hellokitchen.R;

public class ActCompanyDetail extends AppCompatActivity  {

    String companyId ;
    String companyName ;
    String companyLogo ;
    String companyCover;
    String companyIntro ;
    String companyAddress ;
    String companyTel ;
    String companyEmail ;
    String companyOwner ;
    Boolean companyStatus ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_company_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GetInfo();
        InitialComponent();

    }

    private void GetInfo()
    {
        Bundle bundle = getIntent().getExtras();
        companyId = bundle.getString("companyId");
        companyName = bundle.getString("companyName");
        companyLogo = bundle.getString("company_logo");
        companyCover =  bundle.getString("company_cover");
        companyIntro = bundle.getString("company_intro");
        companyAddress =bundle.getString("company_address");
        companyTel =bundle.getString("company_tel");
        companyEmail =bundle.getString("company_email");
        companyOwner =bundle.getString("company_owner");
        companyStatus = bundle.getBoolean("company_status");
    }


    private void InitialComponent()
    {
        frag_companyDetail_info = new Frag_CompanyDetail_Info();
        frag_companyDetail_gallery = new Frag_CompanyDetail_Gallery();
        frag_companyDetail_map = new Frag_CompanyDetail_Map();

        bundleToCompanyDetailPageOne();
        bundleToCompanyDetailPageTwo();
        bundleToCompanyDetailPageThree();

        fragMgr = getFragmentManager();
        fragmentTransaction = fragMgr.beginTransaction();
        fragmentTransaction.add(R.id.frag_company_detail_container,frag_companyDetail_gallery).commit();
        //設置TabLayout
        TabLayout mTabs = (TabLayout) findViewById(R.id.tab_companyInfo);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int position = tab.getPosition();
                fragMgr = getFragmentManager();
                fragmentTransaction = fragMgr.beginTransaction();
                if(position==0)
                {
                    //進入廠商第一頁面
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_company_detail_container,frag_companyDetail_gallery);
                    fragmentTransaction.commit();
                }
                if(position==1)
                {
                    //進入廠商第二頁面
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_company_detail_container,frag_companyDetail_info);
                    fragmentTransaction.commit();
                }
                if(position==2)
                {
                    //進入廠商第二頁面
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_company_detail_container,frag_companyDetail_map);
                    fragmentTransaction.commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void bundleToCompanyDetailPageOne()
    {
        Bundle bundle = new Bundle();
        bundle.putString("companyId",companyId);
        bundle.putString("companyName", companyName);
        bundle.putString("company_logo",companyLogo);
        bundle.putString("company_cover",companyCover);
        frag_companyDetail_gallery.setArguments(bundle);
    }

    private void bundleToCompanyDetailPageTwo()
    {
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        bundle.putString("company_intro",companyIntro);
        bundle.putString("company_owner",companyOwner);
        bundle.putString("company_tel", companyTel);
        bundle.putString("company_email",companyEmail);
        bundle.putString("company_address",companyAddress);
        frag_companyDetail_info.setArguments(bundle);
    }

    private void bundleToCompanyDetailPageThree()
    {
        Bundle bundle = new Bundle();
        bundle.putString("company_address",companyAddress);
        frag_companyDetail_map.setArguments(bundle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //左上角的返回鍵
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    FragmentManager fragMgr;
    FragmentTransaction fragmentTransaction ;
    Frag_CompanyDetail_Info frag_companyDetail_info;
    Frag_CompanyDetail_Gallery frag_companyDetail_gallery;
    Frag_CompanyDetail_Map frag_companyDetail_map;

}
