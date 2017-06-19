package tw.org.iii.hellokitchen.Frag_Company;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import tw.org.iii.hellokitchen.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_CompanyDetail_Info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_CompanyDetail_Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String companyIntro ;
    String companyAddress ;
    String companyTel ;
    String companyEmail ;
    String companyOwner ;

    public Frag_CompanyDetail_Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_CompanyDetail_Info.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_CompanyDetail_Info newInstance(String param1, String param2) {
        Frag_CompanyDetail_Info fragment = new Frag_CompanyDetail_Info();
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
        View v = inflater.inflate(R.layout.frag__company_detail_info, container, false);
        GetInfo();
        InitialComponent(v);
        return  v;
    }

    private void InitialComponent(View v)
    {
        textView_intro =(TextView)v.findViewById(R.id.textView_Company_Intro_detail);
        textView_owner = (TextView)v.findViewById(R.id.textView_Company_Owner_detail);
        textView_email = (TextView)v.findViewById(R.id.textView_Company_Email_detail);
        textView_tel = (TextView)v.findViewById(R.id.textView_Company_Tel_detail);
        textView_address = (TextView)v.findViewById(R.id.textView_Company_Address_detail);
        textView_intro.setText(companyIntro);
        textView_owner.setText(companyOwner);
        textView_email.setText(companyEmail);
        textView_tel.setText(companyTel);
        textView_address.setText(companyAddress);



    }

    private void GetInfo()
    {
        companyIntro = getArguments().getString("company_intro",companyIntro);
        companyOwner = getArguments().getString("company_owner");
        companyTel = getArguments().getString("company_tel");
        companyEmail = getArguments().getString("company_email");
        companyAddress = getArguments().getString("company_address");
    }


    TextView textView_intro;
    TextView textView_owner ;
    TextView textView_tel;
    TextView textView_email;
    TextView textView_address;


}