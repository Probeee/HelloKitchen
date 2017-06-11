package tw.org.iii.hellokitchen.Frag_Recipe;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tw.org.iii.hellokitchen.Activity.ActRealMain;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.RecipeGalleryAdapter;
import tw.org.iii.hellokitchen.Utility.RecipePhotoGalleryAdapter;


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


    /**
     * 用來展示圖片的Gallery
     */
    public GridView photoGallery;

    /**
     * GridView所使用的Adapter
     */
    private RecipeGalleryAdapter adapter;


    private List<Recipes> newRecipesList ;

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
        newRecipesList = new ArrayList<>();
        this.photoGallery = (GridView)v.findViewById(R.id.gridRecipePhoto );

        getAllRecipe();

        this.adapter = new RecipeGalleryAdapter(getActivity(), recipesList, photoGallery,true);
        this.photoGallery.setAdapter( adapter );
        editText_search = (EditText) v.findViewById(R.id.editText_Search_Recipe);

        button = (Button)v.findViewById(R.id.button_Up);
        button.setOnClickListener(button__Click);
        return v;
    }
    private List<Recipes> getAllRecipe()
    {
        if(recipesList == null)
        {
            recipesList = new ArrayList<>();
        }

        else
        {
            recipesList.clear();
        }

        for(int i = 0;i<50;i++)
        {

            if( i % 5 == 0)
            {
                Recipes r0 = new Recipes("" + String.valueOf(i), "第" + i + "份食譜", "第" + i + "份作者", "20170601", true, "1", "10", "http://www.seriouseats.com/images/2015/09/20150914-pressure-cooker-recipes-roundup-08.jpg");
                Recipes r1 = new Recipes("" + String.valueOf(i + 1), "第" + (i + 1) + "份食譜", "第" + (i + 1) + "份作者", "20170602", true, "1", "10", "http://www.seriouseats.com/images/2015/09/20150914-pressure-cooker-recipes-roundup-09.jpg");
                Recipes r2 = new Recipes("" + String.valueOf(i + 2), "第" + (i + 2) + "份食譜", "第" + (i + 2) + "份作者", "20170603", true, "1", "10", "http://www.seriouseats.com/images/2017/02/20170228-pressure-cooker-recipes-roundup-04.jpg");
                Recipes r3 = new Recipes("" + String.valueOf(i + 3), "第" + (i + 3) + "份食譜", "第" + (i + 3) + "份作者", "20170604", true, "1", "10", "http://www.seriouseats.com/images/2017/02/20170228-pressure-cooker-recipes-roundup-06.jpg");
                Recipes r4 = new Recipes("" + String.valueOf(i + 4), "第" + (i + 4) + "份食譜", "第" + (i + 4) + "份作者", "20170604", true, "1", "10", "http://www.seriouseats.com/images/2017/02/20170228-pressure-cooker-recipes-roundup-02.jpg");
                recipesList.add(r0);
                recipesList.add(r1);
                recipesList.add(r2);
                recipesList.add(r3);
                recipesList.add(r4);
            }
        }
        return recipesList;
    }
    private View.OnClickListener button__Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String condition = editText_search.getText().toString();
            recipesList = getAllRecipe();
            newRecipesList.clear();
            if(condition != null)
            {
                //有搜尋條件時候
                for(int count = 0; count<recipesList.size();count++)
                {
                    if(recipesList.get(count).getRecipe_name().contains(condition))
                    {
                        newRecipesList.add(recipesList.get(count));
                    }
                }

                photoGallery.setAdapter(null);
                adapter.notifyDataSetChanged();
                adapter.cancelAllTasks();
                adapter = new RecipeGalleryAdapter(getActivity(), newRecipesList, photoGallery,false);
                photoGallery.setAdapter(adapter);
            }
            else
            {
                //沒有搜尋條件時候
                photoGallery.setAdapter(null);
                adapter.notifyDataSetChanged();
                adapter.cancelAllTasks();
                adapter = new RecipeGalleryAdapter(getActivity(), recipesList, photoGallery,false);
                photoGallery.setAdapter(adapter);

            }
        }

    };

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        this.adapter.cancelAllTasks();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.adapter.cancelAllTasks();
    }

    List<Recipes> recipesList;

    EditText editText_search;
    Button button;
}
