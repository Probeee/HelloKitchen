package tw.org.iii.hellokitchen.Frag_Company;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Company_Pictures;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CustomOkHttp3Downloader;
import tw.org.iii.hellokitchen.Utility.TheDefined;

import static com.facebook.FacebookSdk.getApplicationContext;
import static tw.org.iii.hellokitchen.Utility.TheDefined.Web_Server_URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_CompanyDetail_Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_CompanyDetail_Gallery extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    String companyId;
    String companyName;
    String companyLogo;
    String companyCover;

    private List<Company_Pictures> myRMList  ;

    private SliderLayout mDemoSlider;
    HashMap<String,String> Hash_file_maps ;


    public Frag_CompanyDetail_Gallery()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_CompanyDetail_Gallery.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_CompanyDetail_Gallery newInstance(String param1, String param2) {
        Frag_CompanyDetail_Gallery fragment = new Frag_CompanyDetail_Gallery();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag__company_detail__gallery, container, false);
        GetInfo();
        textView_companyName = (TextView)v.findViewById(R.id.textView_Company_Name_detail);
        textView_companyName.setText(companyName);
        linearCover = (LinearLayout)v.findViewById(R.id.linearLayout_company_cover);
        LoadCover(linearCover);
        imageView = (ImageView)v.findViewById(R.id.imageView_CompanyLogo);

        mDemoSlider = (SliderLayout)v.findViewById(R.id.slider);
        myRMList= new ArrayList<>();
        LoadPicList();
        LoadLogo(imageView);
        return v;
    }




    @Override
    public void onStop()
    {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void GetInfo()
    {
        companyId = getArguments().getString("companyId");
        companyName = getArguments().getString("companyName");
        companyLogo = getArguments().getString("company_logo");
        companyCover = getArguments().getString("company_cover");
    }

    private void LoadCover(final LinearLayout ll)
    {
        try
        {
            //用Picasso載入標題圖片
            Picasso picasso = new Picasso.Builder(getActivity())
                    .downloader(new CustomOkHttp3Downloader(getActivity()))
                    .build();
            Picasso.setSingletonInstance(picasso);
        }
        catch (IllegalStateException ignored)
        {

        }

        Picasso.with(getActivity()).load(companyCover).config(Bitmap.Config.ALPHA_8).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                ll.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
                ll.getBackground().setAlpha(80);//設定非透明度60
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

    private void LoadLogo(ImageView im)
    {
        Picasso
                .with(getActivity())
                .load(companyLogo)
                .config(Bitmap.Config.ALPHA_8)
                .placeholder(R.drawable.photo)   // optional
                .error(R.drawable.icon_pictureloading_error)      // optional
                .into(im);
    }

    private void LoadPicList()
    {
        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try {
                    jsonObject.put(TheDefined.Android_JSON_Key_Company_id, companyId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(Web_Server_URL + "/AndroidCompanyPicturesServlet")
                        .post(body)
                        .build();
                try
                {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        try {
                            JSONArray jsonCompanyPicture = new JSONArray(responseString);
                            for (int i = 0; i < jsonCompanyPicture.length(); i++) {
                                JSONObject jsonObject2 = new JSONObject(jsonCompanyPicture.get(i).toString());
                                Company_Pictures myCP = new Company_Pictures(jsonObject2.getString(TheDefined.Android_JSON_Key_Company_Picture_id),
                                        jsonObject2.getString(TheDefined.Android_JSON_Key_Company_id),
                                        jsonObject2.getString(TheDefined.Android_JSON_Key_Company_Picture_path),
                                        "noData",
                                        "noData");
                                Log.d("GetPID", jsonObject2.getString(TheDefined.Android_JSON_Key_Company_Picture_id));
                                Log.d("GetCID", jsonObject2.getString(TheDefined.Android_JSON_Key_Company_id));
                                Log.d("GetPATH", jsonObject2.getString(TheDefined.Android_JSON_Key_Company_Picture_path));
                                myRMList.add(myCP);
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
                AddtoSlider();
            }
        }.execute();
    }

    private void AddtoSlider()
    {
        Hash_file_maps = new HashMap<>();

        for(int i=0;i<myRMList.size();i++)
        {
            Log.d("Name",myRMList.get(i).getPicture_id());
            Log.d("Path",TheDefined.Web_Server_URL + "/"+myRMList.get(i).getPicture_path());
            Hash_file_maps.put(myRMList.get(i).getPicture_id(), TheDefined.Web_Server_URL + "/"+myRMList.get(i).getPicture_path());
        }

        for(String name : Hash_file_maps.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .description(name)
                    .image(Hash_file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);
            mDemoSlider.addSlider(textSliderView);
        }
        int effect =  (int)(Math.random()* 5)-1;
        switch (effect)
        {
            case 0:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Fade);
                break;
            case 1:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                break;
            case 2:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
                break;
            case 3:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.CubeIn);
                break;
            case 4:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.RotateDown);
                break;
            default:
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
                break;
        }

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(3000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    ImageView imageView;
    TextView textView_companyName ;
    LinearLayout linearCover;


}
