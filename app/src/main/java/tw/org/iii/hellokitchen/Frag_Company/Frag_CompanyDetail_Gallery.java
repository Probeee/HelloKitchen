package tw.org.iii.hellokitchen.Frag_Company;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CustomOkHttp3Downloader;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_CompanyDetail_Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_CompanyDetail_Gallery extends Fragment {
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
    public Frag_CompanyDetail_Gallery() {
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
        LoadLogo(imageView);
        return v;
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
    ImageView imageView;
    TextView textView_companyName ;
    LinearLayout linearCover;
    GridView gridView;
}
