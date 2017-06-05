package tw.org.iii.hellokitchen;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Foods_Container#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Foods_Container extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Frag_Foods_Container() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Foods_Container.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Foods_Container newInstance(String param1, String param2) {
        Frag_Foods_Container fragment = new Frag_Foods_Container();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag__foods__container, container, false);

        //初始化頁面
        frag_foods_deadline = new Frag_Foods_Deadline();
        frag_foods_register = new Frag_Foods_Register();
        frag_foods_shopSearch = new Frag_Foods_ShopSearch();
        fragMgr = getFragmentManager();
        fragmentTransaction = fragMgr.beginTransaction();
        fragmentTransaction.add(R.id.frag_container_food,frag_foods_deadline).commit();

        //設置TabLayout
        TabLayout mTabs = (TabLayout) v.findViewById(R.id.tab_food);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int position = tab.getPosition();
                fragMgr = getFragmentManager();
                fragmentTransaction = fragMgr.beginTransaction();
                if(position==0)
                {
                    //進入註冊頁面
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_container_food,frag_foods_deadline);
                    fragmentTransaction.commit();
                }
                if(position==1)
                {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_container_food,frag_foods_register);
                    fragmentTransaction.commit();
                }
                if(position==2)
                {
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.frag_container_food,frag_foods_shopSearch);
                    fragmentTransaction.commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }

        });
        return  v;
    }
    Frag_Foods_Deadline frag_foods_deadline;
    Frag_Foods_Register frag_foods_register;
    Frag_Foods_ShopSearch frag_foods_shopSearch;

    FragmentManager fragMgr;
    FragmentTransaction fragmentTransaction ;

}
