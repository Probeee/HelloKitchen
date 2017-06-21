package tw.org.iii.hellokitchen.Activity;


import android.content.Context;
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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;


import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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


import tw.org.iii.hellokitchen.Entity.Recipes_Material;
import tw.org.iii.hellokitchen.Entity.Recipes_Method;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CustomOkHttp3Downloader;
import tw.org.iii.hellokitchen.Utility.TheDefined;




public class ActRecipeDetail extends AppCompatActivity
{
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private final List<Recipes_Material>  myRMList  = new ArrayList<>();
    private final List<Recipes_Method> myRMdList = new ArrayList<>();

    String recipeId;
    String recipeName;
    String recipeAmount;
    Boolean recipeStatus ;
    String recipeCooktime ;
    String recipePicture ;
    String memberID ;
    String recipeDetail ;
    String recipeUploadDate ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recipe_detail);

        Bundle bundle = getIntent().getExtras();
        recipeId = bundle.getString("recipeId");
        recipeName = bundle.getString("recipeName");
        recipeAmount = bundle.getString("recipeAmount");
        recipeStatus =  bundle.getBoolean("recipeStatus");
        recipeCooktime = bundle.getString("recipeCooktime");
        recipePicture =bundle.getString("recipePicture");
        memberID =bundle.getString("MemberID");
        recipeDetail = bundle.getString("recipeDetail");
        recipeUploadDate = bundle.getString("recipeUploadDate");

        InitialComponent();
    }

    private void InitialComponent()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.detail_toolbar_layout);

        lv_material = (ListView)findViewById(R.id.detail_material_listView);

        lv_method = (ListView)findViewById(R.id.detail_method_listView);

        textViewIntro = (TextView)findViewById(R.id.textViewIntro);
        textViewCookTime = (TextView)findViewById(R.id.textViewCookTime);

        textViewCookTime.setText(recipeCooktime);
        textViewIntro.setText( recipeDetail );

        loadtheDetail();
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

    private void loadtheDetail()
    {
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
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.ivory));
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
                            for (int i = 0; i < jsonRecipeMethod.length(); i++)
                            {
                                JSONObject jsonRM = new JSONObject(jsonRecipeMethod.get(i).toString());
                                Recipes_Method myRMd = new Recipes_Method(jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_detail),
                                        "noData");
                                myRMdList.add(myRMd);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
               /* String strMsg = "";
                for (int i = 0; i < myRMList.size(); i++)
                {
                    strMsg += myRMList.get(i).getMaterial_name()+"\n";
                }
                for (int i = 0; i < myRMdList.size(); i++) {
                    strMsg += myRMdList.get(i).getMethod_detail() + "\n";
                }
                //lblText.setText(strMsg);*/




                CustomAdapter_materials adapter = new CustomAdapter_materials(getBaseContext());
                lv_material.setAdapter(adapter);

                ViewGroup.LayoutParams params = lv_material.getLayoutParams();
                params.height = getItemHeightofListView(lv_material);
                lv_material.setLayoutParams(params);
                lv_material.requestLayout();


                CustomAdapter_methods adapter_method = new CustomAdapter_methods(getBaseContext());
                lv_method.setAdapter(adapter_method);
                ViewGroup.LayoutParams params2=  lv_method.getLayoutParams();

                params2.height =  getItemHeightofListView(lv_method);
                lv_method.setLayoutParams(params2);
                lv_method.requestLayout();

            }
        }.execute();

    }



    public class CustomAdapter_materials extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;

        public CustomAdapter_materials(Context context)
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount()
        {

            return myRMList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            String s = myRMList.get(position).getMaterial_name();
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.layout_list_of_details,null);
                // inflate custom layout called row
                holder = new ViewHolder();
                holder.tv =(TextView) convertView.findViewById(R.id.listview_item);

                // initialize textview
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }


            holder.tv.setText((position+1)+"."+s);

            // set the name to the text;
            return convertView;
        }

    }
    static class ViewHolder
    {
        TextView tv;
    }

    public class CustomAdapter_methods extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;


        public CustomAdapter_methods(Context context)
        {

            this.context = context;
            inflater = LayoutInflater.from(context);

        }


        @Override
        public int getCount()
        {
            return myRMdList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder2 holder2 ;
            String s = myRMdList.get(position).getMethod_detail();
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.layout_list_of_details,null);
                // inflate custom layout called row
                holder2 = new ViewHolder2();
                holder2.tv2 =(TextView) convertView.findViewById(R.id.listview_item);

                // initialize textview
                convertView.setTag(holder2);
            }
            else
            {
                holder2 = (ViewHolder2)convertView.getTag();
            }

            holder2.tv2.setText((position+1)+".\n"+s);

            // set the name to the text;
            return convertView;
        }

    }
    static class ViewHolder2
    {
        TextView tv2;
    }



    public static int getItemHeightofListView(ListView listView)
    {
        ListAdapter adapter = listView.getAdapter();

        int grossElementHeight = 0;
        int listWidth = listView.getMeasuredWidth();
        for (int i = 0; i < adapter.getCount(); i++)
        {
            View childView = adapter.getView(i, null, listView);
            childView .measure(
                    View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            grossElementHeight += childView.getMeasuredHeight();
        }
        grossElementHeight += (listView.getDividerHeight() * (adapter.getCount() - 1));
        return grossElementHeight;
    }


    ListView lv_material;
    ListView lv_method;
    TextView textViewIntro;
    TextView textViewCookTime;

}