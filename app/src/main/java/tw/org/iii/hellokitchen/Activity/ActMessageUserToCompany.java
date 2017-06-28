package tw.org.iii.hellokitchen.Activity;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    View.OnClickListener btnSendMessage_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            //sendChatMessage();
        }
    };

    private void sendChatMessage()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        chatArrayAdapter.add(new Message(userAccount,companyAccount, editTextMessage.getText().toString(),currentDateandTime));
        editTextMessage.setText("");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_message_user_to_company);
        GetInfo();
        InitialComponent();
       // loadMessagesFromServer();
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
        editTextMessage = (EditText)findViewById(R.id.inputMessage);
        btnSendMessage = (Button)findViewById(R.id.buttonSendMessage);
        btnSendMessage.setOnClickListener(btnSendMessage_Click);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.layout_list_message);
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
                try
                {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful())
                    {
                        responseString = response.body().string();

                        try
                        {
                            myRequestJsonObject = new JSONObject(responseString);
                            JSONArray jsonMessages= new JSONArray(myRequestJsonObject.get(TheDefined.Android_JSONArray_Key_Message).toString());
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
        lvMessage.setAdapter(chatArrayAdapter);
        lvMessage.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lvMessage.setAdapter(chatArrayAdapter);
        lvMessage.setSelection(chatArrayAdapter.getCount() - 1);
    }

    public class ChatArrayAdapter extends ArrayAdapter
    {

        private TextView chatText;
        private TextView chatTimeText;
        private LinearLayout singleMessageContainer;


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
            Message chatMessageObj = getItem(position);
            chatText = (TextView) row.findViewById(R.id.singleMessage);
            chatText.setText(chatMessageObj.getMessage());
            chatTimeText = (TextView) row.findViewById(R.id.singleMessageTime);
            chatTimeText.setText(chatMessageObj.getTime());
            singleMessageContainer.setGravity(chatMessageObj.getSender().equalsIgnoreCase(companyAccount) ? Gravity.LEFT : Gravity.RIGHT);
            return row;
        }

    }

    ListView lvMessage;
    EditText editTextMessage;
    Button btnSendMessage;
}
