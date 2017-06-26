package tw.org.iii.hellokitchen.Frag_Recipe;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Ingredients;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.MyDBHelper;
import tw.org.iii.hellokitchen.Utility.RecipeGalleryAdapterPicasso;
import tw.org.iii.hellokitchen.Utility.TheDefined;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Recipe_FindByIngredients#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Recipe_FindByIngredients extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public Frag_Recipe_FindByIngredients() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Recipe_FindByIngredients.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Recipe_FindByIngredients newInstance(String param1, String param2) {
        Frag_Recipe_FindByIngredients fragment = new Frag_Recipe_FindByIngredients();
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
        View v = inflater.inflate(R.layout.frag__recipe__find_by_ingredients, container, false);
        InitialComponent(v);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Query();
        try
        {
            UploadToServlet();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void InitialComponent(View v)
    {
        recipesList = new ArrayList<>();
        recipesNameList = new ArrayList<>();
        indegredientList = new ArrayList<>();
        myDBHelper = MyDBHelper.getInstance(getActivity());
        db = myDBHelper.getWritableDatabase();
        photoGallery = (GridView)v.findViewById(R.id.gridRecipeIngredientFindPhoto);
    }
    public void Query()
    {
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
            if(i.time>=0)
            {   //沒過期才加入
                indegredientList.add(i);
                recipesNameList.add(i.getName());
            }
        }
        Collections.sort(indegredientList, new Comparator<Ingredients>()
        {
            @Override
            public int compare(Ingredients i2, Ingredients i1)
            {
                return  i2.getTime().compareTo(i1.getTime());
            }
        });
    }

    private void UploadToServlet() throws IOException, JSONException
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
                JSONArray jsonArrayRequest = new JSONArray();  //用來當內層被丟進陣列內的JSON物件
                for(int i = 0;i<recipesNameList.size();i++)
                {
                    //上傳字串
                    jsonArrayRequest.put(recipesNameList.get(i));
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonArrayRequest.toString());
                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeSearchServlet")
                        .post(body)
                        .build();
                try
                {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                    {
                        String responseString = response.body().string();
                        if (!responseString.equals(TheDefined.Android_JSON_Value_Fail)) {
                            try
                            {
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
                                    Log.d("myRecipes", myRecipes.getRecipe_id());
                                    Log.d("myRecipes", myRecipes.getRecipe_name());
                                    Log.d("myRecipes", myRecipes.getMember_id());
                                    Log.d("myRecipes", "" + myRecipes.getRecipe_status());
                                    Log.d("myRecipes", myRecipes.getRecipe_amount());
                                    Log.d("myRecipes", myRecipes.getRecipe_cooktime());
                                    Log.d("myRecipes", myRecipes.getRecipe_picture());
                                    Log.d("myRecipes", myRecipes.getRecipe_detail());
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        } else {
                            TheDefined.showToastByRunnable(getActivity(), "查無相關食譜", Toast.LENGTH_LONG);
                            message.cancel();
                        }

                    }
                } catch (Exception e)
                {
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
                adapter = new RecipeGalleryAdapterPicasso(getActivity(), recipesList, photoGallery,0);
                photoGallery.setAdapter(adapter);
                message.cancel();
            }
        }.execute();

    }



    public GridView photoGallery;
    private RecipeGalleryAdapterPicasso adapter;
    private List<Recipes> recipesList;
    private List<Ingredients> indegredientList;
    private List<String> recipesNameList;
    MyDBHelper myDBHelper;
    SQLiteDatabase db;
}
