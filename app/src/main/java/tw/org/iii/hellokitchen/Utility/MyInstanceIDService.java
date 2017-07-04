package tw.org.iii.hellokitchen.Utility;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by lyusihsian on 2017/7/2.
 */

public class MyInstanceIDService extends FirebaseInstanceIdService {
    private SharedPreferences table;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Token:"+ token);
        table = this.getSharedPreferences("UserToken",0);
        SharedPreferences.Editor row = table.edit();
        row.putString("PhoneToken", token).commit();

        Log.d("FCM", "tableToken:"+ table.getString("PhoneToken", ""));
    }
}