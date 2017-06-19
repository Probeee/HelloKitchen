package tw.org.iii.hellokitchen.Frag_Recipe;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Activity.ActRealMain;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.RecipeGalleryAdapterPicasso;
import tw.org.iii.hellokitchen.Utility.TheDefined;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Recipe_Manage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Recipe_Manage extends Fragment {
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


    public Frag_Recipe_Manage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Recipe_Manage.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Recipe_Manage newInstance(String param1, String param2) {
        Frag_Recipe_Manage fragment = new Frag_Recipe_Manage();
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
        View v =  inflater.inflate(R.layout.frag_recipe_manage, container, false);
        if(recipesList == null)
        {
            recipesList = new ArrayList<>();
        }
        else
        {
            recipesList.clear();
        }
        this.photoGallery = (GridView)v.findViewById(R.id.gridRecipeManagePhoto);
        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            servlet_Recipe_Manage_Data();
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
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        recipesList.clear();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        recipesList.clear();
    }

    /*從servlet將食譜抓下來*/
    private void servlet_Recipe_Manage_Data() throws IOException, JSONException
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
                JSONObject jsonObjectRequest = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try
                {

                    String login_user = getActivity().getIntent().getExtras().getString(TheDefined.LOGIN_USER_MAIL);
                    Log.d("login_user",login_user);
                    jsonObjectRequest.put(TheDefined.Android_JSON_Key_Member_id ,login_user);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON,  jsonObjectRequest.toString());
                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeManageServlet")
                        .post(body)
                        .build();
                try
                {
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



                                recipesList.add(myRecipes);
                                Log.d("myRecipes",myRecipes.getRecipe_id());
                                Log.d("myRecipes",myRecipes.getRecipe_name());
                                Log.d("myRecipes",myRecipes.getMember_id());
                                Log.d("myRecipes",""+myRecipes.getRecipe_status());
                                Log.d("myRecipes",myRecipes.getRecipe_amount());
                                Log.d("myRecipes", myRecipes.getRecipe_cooktime());
                                Log.d("myRecipes",myRecipes.getRecipe_picture());
                                Log.d("myRecipes",myRecipes.getRecipe_detail());
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
                adapter = new RecipeGalleryAdapterPicasso(getActivity(),  recipesList, photoGallery);
                photoGallery.setAdapter( adapter );
                message.cancel();
            }
        }.execute();
    }

}
