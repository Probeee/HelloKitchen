package tw.org.iii.hellokitchen.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;

/**
 * Created by Kevin on 2017/6/12.
 */


public class RecipeGalleryAdapterPicasso extends ArrayAdapter<Recipes>
{

    private Context context;
    private LayoutInflater inflater;
    /**圖片位址或路徑列表  使用整個Recipe物件**/
    private List<Recipes> recipeObjects;
    /**Gallery View**/
    private GridView gridViewPhoto;

    public RecipeGalleryAdapterPicasso(Context context, List<Recipes> objects, GridView photoGridView)
    {
        super(context, 0, objects );
        this.context = context;
        this.recipeObjects = objects;
        this.gridViewPhoto = photoGridView;
        inflater = LayoutInflater.from(context);
        okhttp3.OkHttpClient okHttp3Client = new okhttp3.OkHttpClient();
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttp3Client);
        try
        {
            Picasso picasso = new Picasso.Builder(context)
                .downloader(new CustomOkHttp3Downloader(context))
                .build();
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored)
        {
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }

    }

    /**從List裡面的物件產生畫面**/
    @Override
    public View getView(int position, View convertView, ViewGroup parent )
    {
        //根據view上的position當作index,來抓取該物件的資料
        final String recipe_id = this.getItem(position).getRecipe_id();
        final String recipe_name = this.getItem(position).getRecipe_name();
        final String recipe_producer_id = this.getItem(position).getMember_id();
        final Boolean recipe_status = this.getItem(position).getRecipe_status();


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
                .resize(gridViewPhoto.getColumnWidth(), gridViewPhoto.getColumnWidth() * 75 / 100)
                .placeholder(R.drawable.photo)   // optional
                .error(R.drawable.icon_pictureloading_error)      // optional
                .into(imageView);
        //設定每個區塊上的資訊
        textView_RecipeName.setText(recipe_name);
        textView_ProducerId.setText(recipe_producer_id);


        //三個View元件共用一個事件
        imageView.setOnClickListener(itemClick);
        textView_RecipeName.setOnClickListener(itemClick);
        textView_ProducerId.setOnClickListener(itemClick);

    return convertView;
    }
    View.OnClickListener itemClick = new View.OnClickListener()
    {
        //給GridView上的每個區塊做觸發
        @Override
        public void onClick(View v)
        {
            Toast.makeText(getContext(),""+v.getTag(),Toast.LENGTH_SHORT).show();
        }
    };
}

