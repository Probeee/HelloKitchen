package tw.org.iii.hellokitchen.Frag_Recipe;



import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.RecipeGalleryAdapterPicasso;
import tw.org.iii.hellokitchen.Utility.TheDefined;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Recipe_Container#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Recipe_Container extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;




    public Frag_Recipe_Container() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Recipe_Container.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Recipe_Container newInstance(String param1, String param2) {
        Frag_Recipe_Container fragment = new Frag_Recipe_Container();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag__recipe__container, container, false);

        final Frag_Recipe_Gallery frag_recipe_gallery = new Frag_Recipe_Gallery();
        final Frag_Recipe_Manage frag_recipe_manage = new Frag_Recipe_Manage();
        fragMgr = getFragmentManager();
        fragmentTransaction = fragMgr.beginTransaction();
        fragmentTransaction.add(R.id.frag_recipe_container,frag_recipe_gallery).commit();

        //設置TabLayout
        TabLayout mTabs = (TabLayout) v.findViewById(R.id.tab_recipe);
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

                    //進入食譜搜尋頁面
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_recipe_container,frag_recipe_gallery);
                    fragmentTransaction.commit();
                }
                if(position==1)
                {

                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_recipe_container,frag_recipe_manage);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }

        });
        return v;
    }


    FragmentManager fragMgr;
    FragmentTransaction fragmentTransaction ;
}
