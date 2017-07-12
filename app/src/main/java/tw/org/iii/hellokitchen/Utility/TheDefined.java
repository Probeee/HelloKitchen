package tw.org.iii.hellokitchen.Utility;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import tw.org.iii.hellokitchen.Activity.ActMain;

/**
 * Created by Kevin on 2017/5/29.
 */

public class TheDefined
{
    public static final String LOGIN_USER_NAME = "LOGIN_USER_NAME" ;
    public static final String LOGIN_USER_MAIL = "LOGIN_USER_MAIL" ;
    public static final String LOGIN_USER_IDENT = "LOGIN_USER_IDENT" ;
    public static final String GoogleApiKey = "";
    public static final String FirebaseApiKey = "";

    //--GoogleMap & GooglePlace--//
    public static final String StringBuilder_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String Radius = "3000";
    public static final String Language = "zh-TW";
    public static final String KeyWords = "supermarket" + "+" +"%E8%B6%85%E5%B8%82" + "+" + "%e8%8f%9c%e5%b8%82%e5%a0%b4%0d%0a" ;
    public static final String Sensor =  "true" ;
    public static final String ApiKey = "";

    //--GoogleCloudVision--//
    public static final String CLOUD_VISION_API_KEY = "";
    public static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    public static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    public static final String TAG = ActMain.class.getSimpleName();
    public static final int CAMERA_ID = 66 ;
    public static final int PHOTO_ID = 99 ;

    //自訂showToastByRunnable方法
    public static void showToastByRunnable(final Context context, final CharSequence text, final int duration)     {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }

    /*server URL*/
    
    /*JSON Key & Value*/
    public static final String Android_User_Phone_Token = "Android_User_Phone_Token";

    public static final String Android_JSON_Key_Member_Id = "Android_JSON_Key_Member_Id";
    public static final String Android_JSON_Key_Member_Name = "Android_JSON_Key_Member_Name";
    public static final String Android_JSON_Key_Member_Password = "Android_JSON_Key_Member_Password";
    public static final String Android_JSON_Key_Member_Tel = "Android_JSON_Key_Member_Tel";
    public static final String Android_JSON_Key_Member_Email = "Android_JSON_Key_Member_Email";
    public static final String Android_JSON_Key_Member_FB_Id = "Android_JSON_Key_Member_FB_Id";
    //---- JSON KEY For Recipe
    public static final String Android_JSON_Key_Recipe = "Android_JSON_Key_Recipe";
    public static final String Android_JSON_Key_Recipe_id = "Android_JSON_Key_Recipe_id";
    public static final String Android_JSON_Key_Recipe_name = "Android_JSON_Key_Recipe_name";
    public static final String Android_JSON_Key_Member_id = "Android_JSON_Key_Member_id";
    public static final String Android_JSON_Key_Upload_date = "Android_JSON_Key_Upload_date";
    public static final String Android_JSON_Key_Recipe_status = "Android_JSON_Key_Recipe_status";
    public static final String Android_JSON_Key_Recipe_amount = "Android_JSON_Key_Recipe_amount";
    public static final String Android_JSON_Key_Recipe_cooktime = "Android_JSON_Key_Recipe_cooktime";
    public static final String Android_JSON_Key_Recipe_picture = "Android_JSON_Key_Recipe_picture";
    public static final String Android_JSON_Key_Recipe_detail = "Android_JSON_Key_Recipe_detail";
    public static final String Android_JSON_Key_Recipe_picture_file = "Android_JSON_Key_Recipe_picture_file";
    //--------------------------------------------------------------
    public static final String Android_JSON_Key_Recipe_Material = "Android_JSON_Key_Recipe_Material";
    public static final String Android_JSON_Key_Recipe_Material_id="Android_JSON_Key_Recipe_Material_id";
    public static final String Android_JSON_Key_Recipe_Material_name="Android_JSON_Key_Recipe_Material_name";
    public static final String Android_JSON_Key_Recipe_Material_amount="Android_JSON_Key_Recipe_Material_amount";
    public static final String Android_JSON_Key_Recipe_Material_picture="Android_JSON_Key_Recipe_Material_picture";
    //----------------------------------------------------------------------------------
    public static final String Android_JSON_Key_Recipe_Method = "Android_JSON_Key_Recipe_Method";
    public static final String Android_JSON_Key_Recipe_Method_id="Android_JSON_Key_Recipe_Method_id";
    public static final String Android_JSON_Key_Recipe_Method_detail="Android_JSON_Key_Recipe_Method_detail";
    public static final String Android_JSON_Key_Recipe_Method_picture="Android_JSON_Key_Recipe_Method_picture";
    //---------------------------------------------------------------------------------------------------
    public static final String Android_JSONArray_Key_Recipe_Material="Android_JSONArray_Key_Recipe_Material";
    public static final String Android_JSONArray_Key_Recipe_Method = "Android_JSONArray_Key_Recipe_Method";
    public static final String Android_JSONArray_Key_Message = "Android_JSONArray_Key_Message";
    //------------------------------------------------------------------------------------------------
    public static final String Android_JSON_Key_Information = "Android_JSON_Key_Information";  //JSON 回傳訊息Key
    public static final String Android_JSON_Value_Success = "Android_JSON_Value_Success";      //JSON 回傳訊息Value
    public static final String Android_JSON_Value_Fail = "Android_JSON_Value_Fail";            //JSON 回傳訊息Value
    public static final String Android_JSON_Value_Null = "Android_JSON_Value_Null";            //JSON 回傳訊息Value
    //------------------------------------------------------------------------------------------------
    //---- JSON KEY For Company
    public static final String Android_JSON_Key_Company_id = "Android_JSON_Key_Company_id";
    public static final String Android_JSON_Key_Company_name = "Android_JSON_Key_Company_name";
    public static final String Android_JSON_Key_Company_logo= "Android_JSON_Key_Company_logo";
    public static final String Android_JSON_Key_Company_cover = "Android_JSON_Key_Company_cover";
    public static final String Android_JSON_Key_Cover_intro = "Android_JSON_Key_Cover_intro";
    public static final String Android_JSON_Key_Company_address = "Android_JSON_Key_Company_address";
    public static final String Android_JSON_Key_Company_tel = "Android_JSON_Key_Company_tel";
    public static final String Android_JSON_Key_Company_email = "Android_JSON_Key_Company_email";
    public static final String Android_JSON_Key_Company_owner = "Android_JSON_Key_Company_owner";
    public static final String Android_JSON_Key_Company_password = "Android_JSON_Key_Company_password";
    public static final String Android_JSON_Key_Company_status ="Android_JSON_Key_Company_status";
    //-------JSON KEY For CompanyPicture
    public static final String Android_JSON_Key_Company_Picture_id ="Android_JSON_Key_Company_Picture_id";
    public static final String Android_JSON_Key_Company_Picture_description ="Android_JSON_Key_Company_Picture_description";
    public static final String Android_JSON_Key_Company_Picture_name ="Android_JSON_Key_Company_Picture_name";
    public static final String Android_JSON_Key_Company_Picture_path ="Android_JSON_Key_Company_Picture_path";
    //-------JSON KEY For Message
    public static final String Android_JSON_Key_Message_Sender ="Android_JSON_Key_Message_Sender";
    public static final String Android_JSON_Key_Message_Receiver ="Android_JSON_Key_Message_Receiver";
    public static final String Android_JSON_Key_Message_Message ="Android_JSON_Key_Message_Message";
    public static final String Android_JSON_Key_Message_Time ="Android_JSON_Key_Message_Time";

}
