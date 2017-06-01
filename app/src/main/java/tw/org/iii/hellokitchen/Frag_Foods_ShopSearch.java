package tw.org.iii.hellokitchen;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Foods_ShopSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Foods_ShopSearch extends Fragment implements OnMapReadyCallback
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    double latitude ;
    double longitude;


    public Frag_Foods_ShopSearch()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Foods_ShopSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Foods_ShopSearch newInstance(String param1, String param2)
    {
        Frag_Foods_ShopSearch fragment = new Frag_Foods_ShopSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //AIzaSyA-EBSxktno4wjq7jMwbx50Rb6O_rTZFac
        v  = inflater.inflate(R.layout.frag__foods__shop_search, container, false);
        // Inflate the layout for this fragment
        tv_location = (TextView) v.findViewById(R.id.textView_shopSearch);
        InitialLocation();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView)v.findViewById(R.id.mapView);
        if(mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    private void InitialLocation()
    {
        gps = new GPS_Tracker(getActivity());
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            tv_location.setText("Your Location is - Lat: " + latitude + " Long: "+ longitude  + "\n");
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
        }

    }


    GPS_Tracker gps;
    TextView tv_location;
    GoogleMap mgoogleMap;
    MapView mMapView;
    View v ;
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        MapsInitializer.initialize(getActivity());
        mgoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("You are here").snippet("Hello Kitty"));
        CameraPosition position = CameraPosition.builder().target(new LatLng(latitude,longitude)).zoom(16).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
    }
}
