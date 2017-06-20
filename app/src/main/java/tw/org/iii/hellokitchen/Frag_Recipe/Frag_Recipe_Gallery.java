package tw.org.iii.hellokitchen.Frag_Recipe;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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
 * Use the {@link Frag_Recipe_Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Recipe_Gallery extends Fragment {
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

    private RecipeGalleryAdapterPicasso adapter;

    private List<Recipes> recipesList;
    private List<Recipes> newRecipesList ;

    private EditText editText_search;
    private Button button;
    private int tabNum;

    public Frag_Recipe_Gallery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Recipe_Gallery.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Recipe_Gallery newInstance(String param1, String param2) {
        Frag_Recipe_Gallery fragment = new Frag_Recipe_Gallery();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_recipe_gallery, container, false);
        if(recipesList == null)
        {
            recipesList = new ArrayList<>();
        }
        else
        {
            recipesList.clear();
        }

        if(newRecipesList == null)
        {
            newRecipesList = new ArrayList<>();
        }
        else
        {
            newRecipesList.clear();
        }
       /* if(recipesList_status == null)
        {
            recipesList_status = new ArrayList<>();
        }
        else
        {
            recipesList_status.clear();
        }*/

        this.photoGallery = (GridView)v.findViewById(R.id.gridRecipePhoto );
        //getAllRecipe();
        editText_search = (EditText) v.findViewById(R.id.editText_Search_Recipe);
        button = (Button)v.findViewById(R.id.button_Up);
        button.setOnClickListener(button__Click);

        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            servlet_Recipe_Data();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private View.OnClickListener button__Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String condition = editText_search.getText().toString();
            //recipesList = getAllRecipe();
            newRecipesList.clear();
            if(condition != null)
            {
                //有搜尋條件時候
                for(int count = 0; count< recipesList.size();count++)
                {
                    if( recipesList.get(count).getRecipe_name().contains(condition))
                    {
                        newRecipesList.add( recipesList.get(count));
                    }
                }

                photoGallery.setAdapter(null);
                adapter.notifyDataSetChanged();
                adapter = new RecipeGalleryAdapterPicasso(getActivity(), newRecipesList, photoGallery,0);
                photoGallery.setAdapter(adapter);
            }
            else
            {
                //沒有搜尋條件時候
                photoGallery.setAdapter(null);
                adapter.notifyDataSetChanged();
                //recipesList_status.clear();
                recipesList.clear();
                try
                {
                    servlet_Recipe_Data();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                //adapter = new RecipeGalleryAdapterPicasso(getActivity(), recipesList_status, photoGallery);
                // photoGallery.setAdapter(adapter);

            }
        }

    };
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recipesList.clear();
        //recipesList_status.clear();
        newRecipesList.clear();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        recipesList.clear();
        // recipesList_status.clear();
        newRecipesList.clear();
    }

    /*從servlet將食譜抓下來*/
    private void servlet_Recipe_Data() throws IOException, JSONException
    {
        final ProgressDialog message = new ProgressDialog(getActivity());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("正在讀取資料");
        message.setCancelable(false);
        message.show();

        new AsyncTask<Object, Object, List<Recipes>>()
        {
            @Override
            protected List<Recipes> doInBackground(Object... params)
            {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeServlet")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        try {
                            JSONArray responseJSON = new JSONArray(responseString);
                            for (int i = 0; i < responseJSON.length(); i++)
                            {
                                JSONObject jsonObject = new JSONObject(responseJSON.get(i).toString());
                                Recipes myRecipes = new Recipes(jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_name),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Member_id),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Upload_date),
                                        Boolean.valueOf(jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_status)),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_amount),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_cooktime),
                                        TheDefined.Web_Server_URL + "/" + jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_picture),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Recipe_detail));

                                Log.d("myRecipes",myRecipes.toString());
                                recipesList.add(myRecipes);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                    message.cancel();
                    e.printStackTrace();
                }

                return recipesList;
            }

            @Override
            protected void onPostExecute(List<Recipes> objects)
            {
                super.onPostExecute(objects);
                adapter = new RecipeGalleryAdapterPicasso(getActivity(),  recipesList, photoGallery,0);
                photoGallery.setAdapter( adapter );
                message.cancel();
            }
        }.execute();
    }

}
