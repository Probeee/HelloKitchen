package tw.org.iii.hellokitchen.Frag_LoginPage;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tw.org.iii.hellokitchen.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Main extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Frag_Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frage_Main.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Main newInstance(String param1, String param2)
    {
        Frag_Main fragment = new Frag_Main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View v = inflater.inflate(R.layout.frag__main, container, false);
        Button btn_login = (Button)v.findViewById(R.id.btn_main_login);
        btn_login.setOnClickListener(btn_login_click);
        return v;
    }

    View.OnClickListener btn_login_click = new View.OnClickListener()
    {
        //進入登入介面按鈕
        @Override
        public void onClick(View v)
        {
            Frag_Login frage_login = new Frag_Login();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.frag_container,frage_login);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };



}
