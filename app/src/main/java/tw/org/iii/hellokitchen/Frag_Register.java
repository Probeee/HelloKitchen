package tw.org.iii.hellokitchen;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
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
 * Use the {@link Frag_Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Register extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frag_Register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frage_Register.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Register newInstance(String param1, String param2) {
        Frag_Register fragment = new Frag_Register();
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
        View v = inflater.inflate(R.layout.frag__register, container, false);
        email = (EditText)v.findViewById(R.id.editText_email);
        password = (EditText)v.findViewById(R.id.editText_password);
        passCheck = (EditText)v.findViewById(R.id.editText_passwordCheck);
        name = (EditText)v.findViewById(R.id.editText_name);
        tel = (EditText) v.findViewById(R.id.editText_tel);
        btn_reg = (Button)v.findViewById(R.id.btn_register);
        btn_reg.setOnClickListener(btn_reg_click);


        return v;
    }

    View.OnClickListener btn_reg_click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //檢查錯誤
            if (email.getText().toString().trim().equalsIgnoreCase(""))
            {
                email.setError("帳號不能為空白");
            }
            else if (!email.getText().toString().trim().contains("@"))
            {
                email.setError("非電子信箱");
            }
            else if (password.getText().toString().trim().equalsIgnoreCase(""))
            {
                password.setError("密碼不能為空白");
            }
            else if (passCheck.getText().toString().trim().equalsIgnoreCase(""))
            {
                passCheck.setError("驗證密碼不能為空白");
            }
            else if (!password.getText().toString().trim().equalsIgnoreCase(passCheck.getText().toString().trim()))
            {
                passCheck.setError("密碼不相同");
            }
            else if (name.getText().toString().trim().equalsIgnoreCase(""))
            {
                name.setError("姓名不能為空白");
            }
            else if (tel.getText().toString().trim().equalsIgnoreCase(""))
            {
                tel.setError("電話不能為空白");
            }
            else
            {
                add_New_Account();
            }
        }
    };

    private void add_New_Account()
    {
        //註冊一個新會員
        MyDBHelper dbh =  MyDBHelper.getInstance(getActivity());
        SQLiteDatabase sdb = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("member_id",email.getText().toString().trim());
        values.put("member_name",name.getText().toString().trim());
        values.put("member_password",password.getText().toString().trim());
        values.put("member_tel",tel.getText().toString().trim());
        values.put("member_email",email.getText().toString().trim());
        values.put("member_fb",email.getText().toString().trim());

        try
        {
            sdb.insertOrThrow("tmember", null, values);
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(),"註冊成功",Toast.LENGTH_LONG).show();
        }
        catch (SQLiteConstraintException e)
        {
            Toast.makeText(getActivity(),"已有該帳號",Toast.LENGTH_LONG).show();
        }
    }

    EditText email;
    EditText password;
    EditText passCheck;
    EditText name;
    EditText tel;
    Button btn_reg;

}
