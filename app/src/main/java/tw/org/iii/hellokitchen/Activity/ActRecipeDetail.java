package tw.org.iii.hellokitchen.Activity;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.Entity.Recipes_Material;
import tw.org.iii.hellokitchen.Entity.Recipes_Method;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CustomOkHttp3Downloader;
import tw.org.iii.hellokitchen.Utility.TheDefined;

public class ActRecipeDetail extends AppCompatActivity
{
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recipe_detail);
        InitialComponent();
    }

    private void InitialComponent()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.detail_toolbar_layout);
        Bundle bundle = getIntent().getExtras();

        String recipeId = bundle.getString("recipeId");
        String recipeName = bundle.getString("recipeName");
        String recipeAmount = bundle.getString("recipeAmount");
        Boolean recipeStatus =  bundle.getBoolean("recipeStatus");
        String recipeCooktime = bundle.getString("recipeCooktime");
        String recipePicture =bundle.getString("recipePicture");
        String memberID =bundle.getString("MemberID");
        String recipeDetail = bundle.getString("recipeDetail");
        String recipeUploadDate = bundle.getString("recipeUploadDate");


        try
        {
            //用Picasso載入標題圖片
            Picasso picasso = new Picasso.Builder(this)
                    .downloader(new CustomOkHttp3Downloader(this))
                    .build();
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored)
        {

        }

        collapsingToolbarLayout.setTitle(recipeName);
        Picasso.with(this).load(recipePicture).config(Bitmap.Config.ALPHA_8).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
                collapsingToolbarLayout.getBackground().setAlpha(75);//設定非透明度75

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
        tv_detail= (TextView)findViewById(R.id.detail_recipe_detailTextView);
        servlet_RecipeDetail_Data(recipeId);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.detail_fab);
        fab.setVisibility(View.INVISIBLE);
         /*
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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

    /*暫時*/
    public void servlet_RecipeDetail_Data(final String recipeId)
    {
        final List<Recipes_Material> myRMList = new ArrayList<Recipes_Material>();
        final List<Recipes_Method> myRMdList = new ArrayList<Recipes_Method>();

        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {

                String responseString = "";

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try {
                    jsonObject.put(TheDefined.Android_JSON_Key_Recipe_id, recipeId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeDetailServlet")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        responseString = response.body().string();

                        try {
                            jsonObject = new JSONObject(responseString);

                            JSONArray jsonRecipeMaterial = new JSONArray(jsonObject.get(TheDefined.Android_JSONArray_Key_Recipe_Material).toString());
                            for (int i = 0; i < jsonRecipeMaterial.length(); i++) {
                                JSONObject jsonRM = new JSONObject(jsonRecipeMaterial.get(i).toString());
                                Recipes_Material myRM = new Recipes_Material(jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Material_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Material_name),
                                        "noData",
                                        "noData");
                                myRMList.add(myRM);
                            }
                            JSONArray jsonRecipeMethod = new JSONArray(jsonObject.get(TheDefined.Android_JSONArray_Key_Recipe_Method).toString());
                            for (int i = 0; i < jsonRecipeMethod.length(); i++) {
                                JSONObject jsonRM = new JSONObject(jsonRecipeMethod.get(i).toString());
                                Recipes_Method myRMd = new Recipes_Method(jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_detail),
                                        "noData");
                                myRMdList.add(myRMd);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                String strMsg = "";
                for (int i = 0; i < myRMList.size(); i++) {
                    strMsg += myRMList.get(i).getMaterial_name() + "\n";
                }
                strMsg +=  "\n";
                for (int i = 0; i < myRMdList.size(); i++) {
                    strMsg += myRMdList.get(i).getMethod_detail() + "\n";
                }

                //lblText.setText(strMsg);
                tv_detail.setText(strMsg);

            }
        }.execute();
    }


    TextView tv_detail ;

}
