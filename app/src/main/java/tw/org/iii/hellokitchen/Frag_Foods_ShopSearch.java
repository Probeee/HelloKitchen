package tw.org.iii.hellokitchen;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Foods_ShopSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Foods_ShopSearch extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



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
        View v  = inflater.inflate(R.layout.frag__foods__shop_search, container, false);
        // Inflate the layout for this fragment
        tv_location = (TextView) v.findViewById(R.id.textView_shopSearch);
        InitialLocation();
        GetNearByMarket();
        return v;
    }



    private void InitialLocation()
    {
        gps = new GPS_Tracker(getActivity());
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            tv_location.setText("Your Location is - \nLat: " + latitude + "\nLong: "+ longitude  + "\n");
        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
        }

    }
    private void GetNearByMarket()
    {
    }

    GPS_Tracker gps;
    TextView tv_location;
}
