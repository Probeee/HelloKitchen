package tw.org.iii.hellokitchen.Utility;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Activity.ActRecipeDetail;
import tw.org.iii.hellokitchen.Activity.ActRecipeModify;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.Frag_Ingredients.Frag_Foods_Deadline;
import tw.org.iii.hellokitchen.R;

/**
 * Created by Kevin on 2017/6/12.
 */


public class RecipeGalleryAdapterPicasso extends ArrayAdapter<Recipes> implements AbsListView.OnScrollListener
{

    private Context context;
    private LayoutInflater inflater;
    /**圖片位址或路徑列表  使用整個Recipe物件**/
    private List<Recipes> recipeObjects;
    /**Gallery View**/
    private GridView gridViewPhoto;
    private int tabNum ;
    private RecipeGalleryAdapterPicasso adapter;

    public RecipeGalleryAdapterPicasso(Context context, List<Recipes> objects, GridView photoGridView,int tabNum)
    {
        super(context, 0, objects );
        this.context = context;
        this.recipeObjects = objects;
        this.gridViewPhoto = photoGridView;
        this.tabNum = tabNum;
        inflater = LayoutInflater.from(context);


        try
        {
            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new CustomOkHttp3Downloader(context))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }
        catch (IllegalStateException ignored)
        {
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }

    }

    /**從List裡面的物件產生畫面**/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent )
    {
        //根據view上的position當作index,來抓取該物件的資料
        final String recipe_id = this.getItem(position).getRecipe_id();
        final String recipe_name = this.getItem(position).getRecipe_name();
        final String recipe_producer_id = this.getItem(position).getMember_id();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.recipe_photo_view,parent,false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.photo_Recipe);
        TextView textView_RecipeName = (TextView)convertView.findViewById(R.id.textView_RecipeName);
        TextView textView_ProducerId = (TextView) convertView.findViewById(R.id.textView_RecipeProducer);

        // 給圖片設置一個tag
        textView_RecipeName.setTag(recipe_id);
        textView_ProducerId.setTag(recipe_id);
        imageView.setTag(recipe_id);


        //將網址跟imageView物件傳至setImageView這個Class做事情
        Picasso
                .with(context)
                .load(recipeObjects.get(position).getRecipe_picture())
                .config(Bitmap.Config.ALPHA_8)
                .resize(gridViewPhoto.getColumnWidth(), gridViewPhoto.getColumnWidth() * 75 / 100)
                .tag(recipe_id)
                .placeholder(R.drawable.photo)   // optional
                .error(R.drawable.icon_pictureloading_error)      // optional
                .into(imageView);
        Log.d("RecipePath",recipeObjects.get(position).getRecipe_picture()+"");
        //設定每個區塊上的資訊
        textView_RecipeName.setText(recipe_name);
        textView_ProducerId.setText(recipe_producer_id);


        //三個View元件共用一個事件
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClick(position);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                itemLongClick(position);
                return true;
            }
        });



        textView_RecipeName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClick(position);
            }
        });

        textView_RecipeName.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                itemLongClick(position);
                return true;
            }
        });

        textView_ProducerId.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClick(position);
            }
        });

        textView_ProducerId.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                itemLongClick(position);
                return true;
            }
        });

        return convertView;
    }

    private void itemLongClick(final int position)
    {
        if(tabNum ==0)
        {
            return ;
        }

        if(tabNum == 1)
        //tab為1時候的情況
        {
            Log.d("LongClick","Long");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("確定刪除此菜單?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String deleteRecipeId = recipeObjects.get(position).getRecipe_id();
                            String deleteMemberId = recipeObjects.get(position).getMember_id();
                            //刪除動作至servlet
                            //Toast.makeText(getContext(),deleteRecipeId,Toast.LENGTH_SHORT).show();
                            recipesDeleteUpdate(deleteRecipeId,deleteMemberId);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // 取消
                        }
                    });
            AlertDialog about_dialog = builder.create();
            about_dialog.show();
        }
    }

    private void itemClick(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("recipeId", recipeObjects.get(position).getRecipe_id());
        bundle.putString("recipeName", recipeObjects.get(position).getRecipe_name());
        bundle.putString("recipeAmount", recipeObjects.get(position).getRecipe_amount());
        bundle.putBoolean("recipeStatus", recipeObjects.get(position).getRecipe_status());
        bundle.putString("recipeCooktime", recipeObjects.get(position).getRecipe_cooktime());
        bundle.putString("recipePicture", recipeObjects.get(position).getRecipe_picture());
        bundle.putString("MemberID", recipeObjects.get(position).getMember_id());
        bundle.putString("recipeDetail", recipeObjects.get(position).getRecipe_detail());
        bundle.putString("recipeUploadDate", recipeObjects.get(position).getUpload_date());

        if(tabNum ==0)
        {
            //tab為0時候的情況
            //給GridView上的每個區塊做觸發
            Intent intent = new Intent();
            intent.setClass(getContext(), ActRecipeDetail.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
        }

        if(tabNum ==1)
        //tab為1時候的情況
        {
            //  Toast.makeText(getContext(),"123",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getContext(), ActRecipeModify.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        //當卷軸停止捲動才做
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

    }

    /*上傳資料方法*/
    private void recipesDeleteUpdate(final String recipeId,final String memberId)
    {

        final ProgressDialog message = new ProgressDialog(getContext());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("更新中");
        message.setCancelable(false);
        message.show();

        new AsyncTask<Object, Object, List<Recipes>>()
        {
            @Override
            protected List<Recipes> doInBackground(Object... params)
            {
                OkHttpClient client = new OkHttpClient();

                FormBody body = new FormBody.Builder()
                        .add(TheDefined.Android_JSON_Key_Recipe_id, recipeId)
                        .add(TheDefined.Android_JSON_Key_Member_id, memberId)
                        .build();

                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeDeleteServlet")
                        .post(body)
                        .build();
                try
                {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                    {
                        String responseString = response.body().string();
                        try
                        {
                            JSONArray responseJSON = new JSONArray(responseString);
                            recipeObjects.clear();
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

                                Log.d("myRecipes", myRecipes.toString());
                                recipeObjects.add(myRecipes);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    TheDefined.showToastByRunnable(context, "伺服器無法取得回應", Toast.LENGTH_SHORT);
                    message.cancel();
                    e.printStackTrace();
                }

                return recipeObjects;
            }

            @Override
            protected void onPostExecute(List<Recipes> objects)
            {
                super.onPostExecute(objects);
                adapter = (RecipeGalleryAdapterPicasso) gridViewPhoto.getAdapter();
                gridViewPhoto.setAdapter(null);
                adapter.notifyDataSetChanged();
                adapter =  new RecipeGalleryAdapterPicasso(context,recipeObjects,gridViewPhoto,1);
                gridViewPhoto.setAdapter(adapter);
                message.cancel();
            }
        }.execute();
    }

}

