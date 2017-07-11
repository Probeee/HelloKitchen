package tw.org.iii.hellokitchen.Frag_LoginPage;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Activity.ActRealMain;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.MyDBHelper;
import tw.org.iii.hellokitchen.Utility.TheDefined;


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
    private SharedPreferences table ;

    CallbackManager callbackManager;
    private AccessToken accessToken;


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
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //初始化介面
        View v =inflater.inflate(R.layout.frag__login, container, false);
        btn_login =  (Button)v.findViewById(R.id.btn_login);
        btn_register = (Button)v.findViewById(R.id.btn_register);
        btn_login.setOnClickListener(btn_login_click);
        btn_register.setOnClickListener(btn_register_click);
        email = (EditText)v.findViewById(R.id.editText_login_email);
        password = (EditText)v.findViewById(R.id.editText_login_password);

        btn_facebook_login = (LoginButton) v.findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setFragment(this);
        btn_facebook_login.setReadPermissions(Arrays.asList("email"));  //取得email權限
        //btn_facebook_login.setReadPermissions(Arrays.asList("user_status"));

        processFacebookLogin();   //facebook按鈕監聽事件方法

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        table = getActivity().getSharedPreferences("LoginUser",0);
        if(!table.getString("UserEmail","").isEmpty() && !table.getString("UserName","").isEmpty())
        {
           // Toast.makeText(getActivity(), table.getString("UserEmail", "") + "-" + table.getString("UserName", ""), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getActivity(),ActRealMain.class);
            Bundle bundle = new Bundle();
            bundle.putString(TheDefined.LOGIN_USER_NAME, table.getString("UserName", ""));
            bundle.putString(TheDefined.LOGIN_USER_MAIL ,table.getString("UserEmail", ""));

            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*App 註冊登入監聽事件*/
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
                servlet_Find_The_Account();
                //find_the_Account();
            }
        }
    };

    /*註冊會員按鈕監聽事件*/
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

   /* *//*SQLite 登入方法*//*
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
                //dbHelper.setLogin_user(c.getString(0));
                Intent intent = new Intent();
                intent.setClass(getActivity(),ActRealMain.class);
                Bundle bundle = new Bundle();
                bundle.putString(TheDefined.LOGIN_USER_NAME,c.getString(0));
                bundle.putString(TheDefined.LOGIN_USER_MAIL ,email.getText().toString());
                table = getActivity().getSharedPreferences("LoginUser",0);
                SharedPreferences.Editor row = table.edit();
                row.putString("UserEmail",email.getText().toString()).commit();
                row.putString("UserName",c.getString(0)).commit();
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
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
    }*/

    /*facebook 註冊登入監聽事件*/
    private void processFacebookLogin() {
        btn_facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            //登入成功

            @Override
            public void onSuccess(LoginResult loginResult) {

                //accessToken之後或許還會用到 先存起來

                accessToken = loginResult.getAccessToken();

                Log.d("FB","access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                /*取的資料後可直接存入servlet*/
                                /*
                                Log.d("FB","complete");
                                Log.d("FB",object.optString("name"));
                                Log.d("FB",object.optString("link"));
                                Log.d("FB",object.optString("id"));
                                Log.d("FB",object.optString("email"));
                                */

                                final String fbEmail = object.optString("email").toString();
                                final String fbId = object.optString("id").toString();
                                final String fbName = object.optString("name").toString();


                                final ProgressDialog message = new ProgressDialog(getActivity());
                                message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                message.setTitle("登入中");
                                message.setCancelable(false);
                                message.show();

                                new Thread(new Runnable(){
                                    public void run(){

                                        table = getActivity().getSharedPreferences("UserToken",0);
                                        OkHttpClient client = new OkHttpClient();
                                        JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                                        try {
                                            jsonObject.put(TheDefined.Android_JSON_Key_Member_Id, fbEmail);
                                            jsonObject.put(TheDefined.Android_JSON_Key_Member_Password, fbId);
                                            jsonObject.put(TheDefined.Android_User_Phone_Token, table.getString("PhoneToken", "Fail"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                                        Request request = new Request.Builder()
                                                .url(TheDefined.Web_Server_URL + "/AndroidLoginAccountServlet")
                                                .post(body)
                                                .build();
                                        try {
                                            Response response = client.newCall(request).execute();
                                            if (response.isSuccessful()) {
                                                String responseString = response.body().string();
                                                //回傳的內容轉存為JSON物件
                                                JSONObject responseJSON = new JSONObject(responseString);
                                                //取得Message的屬性
                                                String info = responseJSON.getString(TheDefined.Android_JSON_Key_Information);
                                                Log.d("info", responseString);
                                                Log.d("member_name", responseString);

                                                if (info.equals(TheDefined.Android_JSON_Value_Success)) {
                                                    Intent intent = new Intent();
                                                    intent.setClass(getActivity(),ActRealMain.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(TheDefined.LOGIN_USER_NAME, fbName);
                                                    bundle.putString(TheDefined.LOGIN_USER_MAIL , fbEmail);

                                                    table = getActivity().getSharedPreferences("LoginUser",0);
                                                    SharedPreferences.Editor row = table.edit();
                                                    row.putString("UserEmail", fbEmail).commit();
                                                    row.putString("UserName", fbName).commit();

                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                    message.cancel();
                                                    getActivity().finish();

                                                } else if (info.equals(TheDefined.Android_JSON_Value_Fail)) {
                                                    /*mysql沒有facebook帳號需要將資料輸入至mysql*/
                                                    jsonObject = new JSONObject();
                                                    try {
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_Id, fbEmail);
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_Name, fbName);
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_Password, fbId);
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_Tel, "noData");
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_Email, fbEmail);
                                                        jsonObject.put(TheDefined.Android_JSON_Key_Member_FB_Id, fbId);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    JSON = MediaType.parse("application/json; charset=utf-8");
                                                    body = RequestBody.create(JSON, jsonObject.toString());

                                                    request = new Request.Builder()
                                                            .url(TheDefined.Web_Server_URL + "/AndroidAddAccountServlet")
                                                            .post(body)
                                                            .build();
                                                    try {
                                                        response = client.newCall(request).execute();
                                                        if (response.isSuccessful()) {
                                                            responseString = response.body().string();
                                                            //回傳的內容轉存為JSON物件
                                                            responseJSON = new JSONObject(responseString);
                                                            //取得Message的屬性
                                                            info = responseJSON.getString(TheDefined.Android_JSON_Key_Information);
                                                            Log.d("info", responseString);

                                                            //在Thread中執行toast
                                                            if (info.equals(TheDefined.Android_JSON_Value_Success)) {
                                                                Intent intent = new Intent();
                                                                intent.setClass(getActivity(),ActRealMain.class);
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString(TheDefined.LOGIN_USER_NAME, fbName);
                                                                bundle.putString(TheDefined.LOGIN_USER_MAIL , fbEmail);

                                                                table = getActivity().getSharedPreferences("LoginUser",0);
                                                                SharedPreferences.Editor row = table.edit();
                                                                row.putString("UserEmail", fbEmail).commit();
                                                                row.putString("UserName", fbName).commit();

                                                                intent.putExtras(bundle);
                                                                startActivity(intent);
                                                                message.cancel();
                                                                getActivity().finish();
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                                            message.cancel();
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });

                //包入你想要得到的資料 送出request

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消

            @Override
            public void onCancel() {
                // App code

                Log.d("FB","CANCEL");
            }

            //登入失敗

            @Override
            public void onError(FacebookException exception) {
                // App code

                Log.d("FB",exception.toString());
            }
        });
    }

    /*將登入資料打包JSON上傳至伺服器servlet驗證*/   /* 需要改一下帶三個參數 帳號, 密碼, 是否為facebook使用者 */
    private void servlet_Find_The_Account() {

        message = new ProgressDialog(getActivity());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("登入中");
        message.setCancelable(false);
        message.show();

        new Thread(new Runnable(){
            public void run(){

                table = getActivity().getSharedPreferences("UserToken",0);

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try {
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Id, email.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_JSON_Key_Member_Password, password.getText().toString().trim());
                    jsonObject.put(TheDefined.Android_User_Phone_Token, table.getString("PhoneToken", "Fail"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidLoginAccountServlet")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        //回傳的內容轉存為JSON物件
                        JSONObject responseJSON = new JSONObject(responseString);
                        //取得Message的屬性
                        String info = responseJSON.getString(TheDefined.Android_JSON_Key_Information);
                        String memberName = responseJSON.getString(TheDefined.Android_JSON_Key_Member_Name);



                        Log.d("info", responseString);
                        Log.d("member_name", responseString);

                        if (info.equals(TheDefined.Android_JSON_Value_Success))
                        {
                            String memberIdent = responseJSON.getString(TheDefined.LOGIN_USER_IDENT);
                            Log.d("errorMsg",memberIdent);

                            Intent intent = new Intent();
                            intent.setClass(getActivity(),ActRealMain.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(TheDefined.LOGIN_USER_NAME, memberName);
                            bundle.putString(TheDefined.LOGIN_USER_MAIL , email.getText().toString());

                            table = getActivity().getSharedPreferences("LoginUser",0);
                            SharedPreferences.Editor row = table.edit();
                            row.putString("UserEmail", email.getText().toString()).commit();
                            row.putString("UserName", memberName).commit();
                            row.putString("UserIdent",memberIdent).commit();

                            intent.putExtras(bundle);
                            startActivity(intent);

                            message.cancel();
                            getActivity().finish();

                        }
                        else if (info.equals(TheDefined.Android_JSON_Value_Fail))
                        {
                            TheDefined.showToastByRunnable(getActivity(),"帳號密碼有誤",Toast.LENGTH_LONG); //在Thread中執行toast
                            message.cancel();
                        }
                    }
                } catch (Exception e) {
                    //Log.d("errorMsg",e.getMessage());
                    TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                    message.cancel();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( message!=null && message.isShowing() ){
            message.cancel();
        }

    }

    /*取得facebook api Hash Key (不同PC)*/
    private void getHashKey() {
        PackageInfo info;
        try{
            info = getActivity().getPackageManager().getPackageInfo("com.you.name", PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyResult =new String(Base64.encode(md.digest(),0));//String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", KeyResult);
                Toast.makeText(getActivity(), "My FB Key is \n"+ KeyResult, Toast.LENGTH_LONG ).show();
            }
        }catch(PackageManager.NameNotFoundException e1){Log.e("name not found", e1.toString());
        }catch(NoSuchAlgorithmException e){Log.e("no such an algorithm", e.toString());
        }catch(Exception e){Log.e("exception", e.toString());}
    }

    Button btn_login ;
    Button btn_register;
    EditText email;
    EditText password;

    LoginButton btn_facebook_login;

    ProgressDialog message;
}