package tw.org.iii.hellokitchen.Activity;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Message;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.TheDefined;

public class ActMessageUserToCompany extends AppCompatActivity
{
    private String userAccount ;
    private String companyAccount;
    private List<Message> messageList ;
    private ChatArrayAdapter chatArrayAdapter;
    private LinearLayout singleMessageContainer;
    private LinearLayout singleMessageBigContainer;


    View.OnClickListener btnSendMessage_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            sendChatMessage();
        }
    };

    private void sendChatMessage()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateAndTime = sdf.format(new Date());
        final Message newMessage = new Message(userAccount,companyAccount, editTextMessage.getText().toString(),currentDateAndTime);
        chatArrayAdapter.add(newMessage);
        editTextMessage.setText("");

        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {

                String responseString = "";
                OkHttpClient client = new OkHttpClient();
                JSONObject myRequestJsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try
                {
                    myRequestJsonObject.put("action","uploadnewmessage");
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Sender,newMessage.getSender());
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Receiver,newMessage.getReceiver());
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Message,newMessage.getMessage());
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Time,newMessage.getTime());
                    Log.d("test","objectput");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("test",e.getMessage());
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, myRequestJsonObject.toString());
                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidMessageServlet")
                        .post(body)
                        .build();

                Log.d("test",myRequestJsonObject.toString());
                Log.d("test",TheDefined.Web_Server_URL + "/AndroidMessageServlet");
                try
                {
                    Response response = client.newCall(request).execute();

                    responseString = response.body().string();
                    Log.d("test",responseString);
                    if (response.isSuccessful())
                    {
                      //  responseString = response.body().string();
                        JSONObject responseJsonObj = new JSONObject(responseString);
                        Log.d("test","responseEnter");

                        if (responseJsonObj.getString(TheDefined.Android_JSON_Key_Information).equals(TheDefined.Android_JSON_Value_Success))
                        {
                            try
                            {
                                String toToken = responseJsonObj.getString(TheDefined.Android_User_Phone_Token);
                                if (!toToken.equalsIgnoreCase(TheDefined.Android_JSON_Value_Null)) {
                                    Log.d("response2",responseString);
                                    callFirebaseMessages(toToken, newMessage.getSender());
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                Log.d("test",e.getMessage());
                            }
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"伺服器沒有回應 請重新嘗試",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e)
                {

                    Log.d("test",e.getMessage());
                    e.printStackTrace();
                }

                return null;
            }

        }.execute();
    }

    private void callFirebaseMessages(final String toToken, final String sender) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("body", "來自" + sender + "一則新訊息");
                    jsonObject.put("sound", "default");
                    JSONObject json = new JSONObject();
                    json.put("to", toToken);
                    json.put("notification", jsonObject);
                    json.put("data", jsonObject);

                    Log.d("json2", json.toString());
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .url("https://fcm.googleapis.com/fcm/send")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", TheDefined.FirebaseApiKey)
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    final String responseString = response.body().string();
                    Log.d("responseString", responseString);
                    /*if (response.isSuccessful())
                    {

                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_message_user_to_company);
        GetInfo();
        InitialComponent();
        loadMessagesFromServer();

    }


    private void GetInfo()
    {
        Bundle bundle = getIntent().getExtras();
        userAccount =  bundle.getString("userAccount");
        companyAccount = bundle.getString("companyAccount");
    }

    private void InitialComponent()
    {
        lvMessage = (ListView)findViewById(R.id.listViewMessageUtoCom);
        btnSendMessage = (Button)findViewById(R.id.buttonSendMessage);
        btnSendMessage.setOnClickListener(btnSendMessage_Click);
        editTextMessage = (EditText)findViewById(R.id.inputMessage);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //左上角的返回鍵
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessagesFromServer()
    {
        messageList = new ArrayList<>();

        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {

                String responseString = "";
                OkHttpClient client = new OkHttpClient();
                JSONObject myRequestJsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try
                {
                    myRequestJsonObject.put("action","downloadallmessages");
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Sender,userAccount);
                    myRequestJsonObject.put(TheDefined.Android_JSON_Key_Message_Receiver,companyAccount);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, myRequestJsonObject.toString());

                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidMessageServlet")
                        .post(body)
                        .build();
                Log.d("response",myRequestJsonObject.toString());
                try
                {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                    {
                        responseString = response.body().string();
                        if (!responseString.equals(TheDefined.Android_JSON_Value_Fail)) {
                            try
                            {
                                JSONArray jsonMessages= new JSONArray(responseString);
                                Log.d("response",responseString);
                                for (int i = 0; i < jsonMessages.length(); i++)
                                {
                                    JSONObject jsonRM = new JSONObject(jsonMessages.get(i).toString());
                                    Message myMessage = new Message(jsonRM.getString(TheDefined.Android_JSON_Key_Message_Sender),
                                            jsonRM.getString(TheDefined.Android_JSON_Key_Message_Receiver),
                                            jsonRM.getString(TheDefined.Android_JSON_Key_Message_Message),
                                            jsonRM.getString(TheDefined.Android_JSON_Key_Message_Time));
                                    messageList.add(myMessage);
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        } else {
                            //無對話訊息;
                        }
                    }
                } catch (Exception e)
                {

                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                AddDataToListView();//所有資訊加入ListView
            }

        }.execute();
    }

    private void AddDataToListView()
    {


        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.layout_list_message);
        lvMessage.setAdapter(chatArrayAdapter);
        lvMessage.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        //lvMessage.setAdapter(chatArrayAdapter);
        lvMessage.setSelection(chatArrayAdapter.getCount() - 1);


    }

    public class ChatArrayAdapter extends ArrayAdapter
    {

        private TextView chatText;
        private TextView chatTimeText;



        public void add(Message object)
        {
            messageList.add(object);
            super.add(object);
        }

        public ChatArrayAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }

        public int getCount()
        {
            return messageList.size();
        }

        public Message getItem(int index)
        {
            return messageList.get(index);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            if (row == null)
            {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate( R.layout.layout_list_message, parent, false);
            }
            singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
            singleMessageBigContainer = (LinearLayout) row.findViewById(R.id.singleMessageBigContainer);

            Message chatMessageObj = getItem(position);
            chatText = (TextView) row.findViewById(R.id.singleMessage);
            chatText.setText(chatMessageObj.getMessage());
            chatTimeText = (TextView) row.findViewById(R.id.singleMessageTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try
            {
                Date date = sdf.parse(chatMessageObj.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = sdf2.format(date);
                chatTimeText.setText(dateString);
            } catch (ParseException e)
            {
                e.printStackTrace();
            }

            singleMessageContainer.setGravity(chatMessageObj.getSender().equalsIgnoreCase(companyAccount) ? Gravity.LEFT : Gravity.RIGHT);
            chatText.setBackgroundResource(chatMessageObj.getSender().equalsIgnoreCase(companyAccount) ? R.drawable.out_message_bg:R.drawable.in_message_bg);
            singleMessageBigContainer.setGravity(chatMessageObj.getSender().equalsIgnoreCase(companyAccount) ? Gravity.LEFT : Gravity.RIGHT);

            return row;
        }

    }

    ListView lvMessage;
    EditText editTextMessage;
    Button btnSendMessage;
}