package tw.org.iii.hellokitchen;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Frag_Login()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frage_Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Login newInstance(String param1, String param2)
    {
        Frag_Login fragment = new Frag_Login();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        //初始化介面
        View v =inflater.inflate(R.layout.frag__login, container, false);
        btn_login =  (Button)v.findViewById(R.id.btn_login);
        btn_register = (Button)v.findViewById(R.id.btn_register);
        btn_login.setOnClickListener(btn_login_click);
        btn_register.setOnClickListener(btn_register_click);

        email = (EditText)v.findViewById(R.id.editText_login_email);
        password = (EditText)v.findViewById(R.id.editText_login_password);
        return v;
    }

    View.OnClickListener btn_login_click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //會員登入
            if (email.getText().toString().trim().equalsIgnoreCase(""))
            {
                email.setError("帳號不能為空白");
            }
            else if (password.getText().toString().trim().equalsIgnoreCase(""))
            {
                password.setError("帳號不能為空白");
            }
            else
            {
                find_the_Account();
            }
        }
    };

    private void find_the_Account()
    {
        //找尋是否有該會員
        MyDBHelper dbHelper = MyDBHelper.getInstance(getActivity());
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        //以下將來要替換掉
        try
        {
            String sqlselection = "member_id= '" +email.getText().toString() + "' AND member_password='"+password.getText().toString()+"'";
            try
            {
                Cursor c = sqLiteDatabase.query("tmember", new String[]{"member_name"}, sqlselection, null, null, null, null);
                c.moveToFirst();
                //Toast.makeText(getActivity(), c.getString(0), Toast.LENGTH_LONG).show();
                dbHelper.setLogin_user(c.getString(0));
                Intent intent = new Intent();
                intent.setClass(getActivity(),ActRealMain.class);
                Bundle bundle = new Bundle();
                bundle.putString(TheDefined.LOGIN_USER_NAME,c.getString(0));
                bundle.putString(TheDefined.LOGIN_USER_MAIL ,email.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
            catch (Exception e)
            {
                Toast.makeText(getActivity(), "帳密有誤!", Toast.LENGTH_LONG).show();
            }
        }
        catch (SQLException ex)
        {
            Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    View.OnClickListener btn_register_click = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            //進入註冊頁面
            Frag_Register frag_register = new Frag_Register();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.replace(R.id.frag_container,frag_register);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    Button btn_login ;
    Button btn_register;
    EditText email;
    EditText password;
}
