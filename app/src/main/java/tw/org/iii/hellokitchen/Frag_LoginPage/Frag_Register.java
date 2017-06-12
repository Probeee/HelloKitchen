package tw.org.iii.hellokitchen.Frag_LoginPage;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.MyDBHelper;
import tw.org.iii.hellokitchen.Utility.TheDefined;


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
                try {
                    servlet_Add_New_Account();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //add_New_Account();
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
        values.put("member_fb_id",email.getText().toString().trim());

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

    /*將註冊資料打包JSON上傳至伺服器servlet*/
    private void servlet_Add_New_Account() throws IOException, JSONException {

        final ProgressDialog message = new ProgressDialog(getActivity());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("註冊中");
        message.setCancelable(false);
        message.show();

        new Thread(new Runnable(){
            public void run(){
                try {
                    URL url = new URL(TheDefined.Web_Server_URL + "/AndroidAddAccountServlet");
                    //URLConnection connection = url.openConnection();
                    //用來包覆JSONArray的JSON物件
                    JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件

                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Id, email.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Name, name.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Password, password.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Tel, tel.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Email, email.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_FB_Id, email.getText().toString().trim());

                    HttpClient httpClient = new DefaultHttpClient();

                    httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
                    httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

                    HttpPost post = new HttpPost(String.valueOf(url));

                    //JSON物件放到POST Request
                    StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
                    stringEntity.setContentType("application/json;charset=UTF-8");
                    post.setEntity(stringEntity);
                    //執行POST Request
                    HttpResponse httpResponse = httpClient.execute(post);

                    //取得回傳的內容
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                    //回傳的內容轉存為JSON物件
                    JSONObject responseJSON = new JSONObject(responseString);
                    //取得Message的屬性
                    String info = responseJSON.getString(TheDefined.Android_JSON_Key_Information);
                    Log.d("info", responseString);

                    //在Thread中執行toast
                    if (info.equals(TheDefined.Android_JSON_Value_Success)) {
                        getFragmentManager().popBackStack();
                        TheDefined.showToastByRunnable(getActivity(), "註冊成功", Toast.LENGTH_LONG);
                        message.cancel();
                    } else if (info.equals(TheDefined.Android_JSON_Value_Fail)){
                        TheDefined.showToastByRunnable(getActivity(), "已有該帳號", Toast.LENGTH_LONG);
                        message.cancel();
                    }

                } catch (JSONException e) {
                    TheDefined.showToastByRunnable(getActivity(), "json", Toast.LENGTH_LONG);
                    e.printStackTrace();
                } catch (IOException e) {
                    TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                    message.cancel();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    EditText email;
    EditText password;
    EditText passCheck;
    EditText name;
    EditText tel;
    Button btn_reg;

}