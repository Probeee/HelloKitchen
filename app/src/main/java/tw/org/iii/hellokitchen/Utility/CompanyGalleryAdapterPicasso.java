package tw.org.iii.hellokitchen.Utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;


import tw.org.iii.hellokitchen.Activity.ActCompanyDetail;
import tw.org.iii.hellokitchen.Activity.ActRecipeDetail;
import tw.org.iii.hellokitchen.Entity.Company;
import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;

/**
 * Created by Kevin on 2017/6/12.
 */


public class CompanyGalleryAdapterPicasso extends ArrayAdapter<Company> implements AbsListView.OnScrollListener
{

    private Context context;
    private LayoutInflater inflater;
    /**圖片位址或路徑列表  使用整個Companye物件**/
    private List<Company> companyObjects;
    /**Gallery View**/
    private GridView gridViewPhoto;



    public CompanyGalleryAdapterPicasso(Context context, List<Company> objects, GridView photoGridView)
    {
        super(context, 0, objects );
        this.context = context;
        this.companyObjects = objects;
        this.gridViewPhoto = photoGridView;
        inflater = LayoutInflater.from(context);


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
    public View getView(final int position, View convertView, ViewGroup parent )
    {
        //根據view上的position當作index,來抓取該物件的資料
        final String company_id = this.getItem(position).getCompany_id();
        final String company_name = this.getItem(position).getCompany_name();


        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.company_photo_view,parent,false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.photo_Company);
        TextView textView_RecipeName = (TextView)convertView.findViewById(R.id.textView_CompanyName);


        // 給圖片設置一個tag
        textView_RecipeName.setTag(company_id);
        imageView.setTag(company_id);


        //將網址跟imageView物件傳至setImageView這個Class做事情
        Picasso
                .with(context)
                .load(companyObjects.get(position).getCompany_cover())
                .config(Bitmap.Config.ALPHA_8)
                .resize(gridViewPhoto.getColumnWidth()*90/100, gridViewPhoto.getColumnWidth()*50/100)
                .tag(company_id)
                .placeholder(R.drawable.photo)   // optional
                .error(R.drawable.icon_pictureloading_error)      // optional
                .into(imageView);

        //設定每個區塊上的資訊
        textView_RecipeName.setText(company_name);


        //三個View元件共用一個事件
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                itemClick(position);
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


        return convertView;
    }

    private void itemClick(int position)
    {
        //給GridView上的每個區塊做觸發
        Intent intent = new Intent();
       intent.setClass(getContext(), ActCompanyDetail.class);


        Bundle bundle = new Bundle();


        bundle.putString("companyId", companyObjects.get(position).getCompany_id());
        bundle.putString("companyName", companyObjects.get(position).getCompany_name());
        bundle.putString("company_logo", companyObjects.get(position).getCompany_logo());
        bundle.putString("company_cover", companyObjects.get(position).getCompany_cover());
        bundle.putString("company_intro", companyObjects.get(position).getCompany_intro());
        bundle.putString("company_address",companyObjects.get(position).getCompany_address());
        bundle.putString("company_tel", companyObjects.get(position).getCompany_tel());
        bundle.putString("company_email", companyObjects.get(position).getCompany_email());
        bundle.putString("company_owner", companyObjects.get(position).getCompany_owner());
       // bundle.putString("company_password", companyObjects.get(position).getCompany_password());
        bundle.putBoolean("company_status", companyObjects.get(position).isCompany_status());


        intent.putExtras(bundle);
        getContext().startActivity(intent);

        //Toast.makeText(getContext(),""+v.getTag(),Toast.LENGTH_SHORT).show();
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
}

