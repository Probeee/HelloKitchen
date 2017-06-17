package tw.org.iii.hellokitchen.Frag_Company;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Use the {@link Frag_CompanyDetail_Map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_CompanyDetail_Map extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String companyAddress ;
    public Frag_CompanyDetail_Map() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_CompanyDetail_Map.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_CompanyDetail_Map newInstance(String param1, String param2) {
        Frag_CompanyDetail_Map fragment = new Frag_CompanyDetail_Map();
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
        View v = inflater.inflate(R.layout.frag__company_detail__map, container, false);
        companyAddress = getArguments().getString("company_address");
        mapview = (MapView)v.findViewById(R.id.mapView_CompanyAddress);
        return v;

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mapViewInitial();
    }
    private void mapViewInitial()
    {
        if(mapview != null)
        {
            mapview.onCreate(null);
            mapview.onResume();
            mapview.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        MapsInitializer.initialize(getActivity());
        mgoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(companyAddress, 5);
            if (addresses.size() > 0)
            {
                Double lat = (double) (addresses.get(0).getLatitude());
                Double lon = (double) (addresses.get(0).getLongitude());

                Log.d("lat-long", "" + lat + "......." + lon);
                final LatLng user = new LatLng(lat, lon);
                /*used marker for show the location */
                Marker hamburg = googleMap.addMarker(new MarkerOptions()
                        .position(user)
                        .title(companyAddress));
                hamburg.showInfoWindow();


                // Move the camera instantly to hamburg with a zoom of 15.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 25));

                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 1000, null);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    MapView mapview;
    GoogleMap mgoogleMap;

}
