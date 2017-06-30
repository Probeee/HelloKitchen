package tw.org.iii.hellokitchen.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import tw.org.iii.hellokitchen.Entity.Member;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.TheDefined;

public class ActMessageCompanyToUser extends AppCompatActivity
{
    private List<String> memberList ;
    private String companyAccount;
    private String userAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_message_company_to_user);
        GetInfo();
        InitialComponent();
       /* memberList.add("test01@gmail.com");
        memberList.add("test02@gmail.com");
        memberList.add("test03@gmail.com");
        memberList.add("test04@gmail.com");
        memberList.add("test05@gmail.com");
        memberList.add("test06@gmail.com");*/
        loadUsersFromServer();
        //AddDataToListView();
    }

    private void GetInfo()
    {
        Bundle bundle = getIntent().getExtras();
        companyAccount = bundle.getString("companyAccount");
    }

    private void InitialComponent()
    {
        memberList = new ArrayList<>();
        lvContacter = (ListView) findViewById(R.id.listViewUofCom);
    }

    private void loadUsersFromServer()
    {
        memberList = new ArrayList<>();

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
                    myRequestJsonObject.put("action","downloadallusers");
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
                        if (!responseString.equals(TheDefined.Android_JSON_Value_Fail))
                        {
                            try
                            {
                                JSONArray jsonUsers= new JSONArray(responseString);

                                for (int i = 0; i < jsonUsers.length(); i++)
                                {
                                    String user = jsonUsers.get(i).toString();
                                    memberList.add(user);
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
        CustomAdapter_members adapter = new CustomAdapter_members(this);
        lvContacter.setAdapter(adapter);
        lvContacter.setOnItemClickListener(lvContacter_itemClick);
    }

    AdapterView.OnItemClickListener lvContacter_itemClick = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Intent intent = new Intent();
            intent.setClass(ActMessageCompanyToUser.this, ActMessageUserToCompany.class);
            Bundle bundle = new Bundle();
            TextView textView = (TextView) view.findViewById(R.id.listview_item_user);
            bundle.putString("userAccount",companyAccount );
            bundle.putString("companyAccount",String.valueOf(textView.getText()));
            Log.d("test",companyAccount);
            Log.d("test",String.valueOf(textView.getText()));
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    public class CustomAdapter_members extends BaseAdapter
    {
        private Context context;
        private LayoutInflater inflater;

        public CustomAdapter_members(Context context)
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount()
        {

            return memberList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            String s = memberList.get(position);

            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.layout_list_of_users,null);
                // inflate custom layout called row
                holder = new ViewHolder();
                holder.tv =(TextView) convertView.findViewById(R.id.listview_item_user);

                // initialize textview
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }


            holder.tv.setText(s);


            // set the name to the text;
            return convertView;
        }

    }
    static class ViewHolder
    {
        TextView tv;
    }

    ListView lvContacter;
}
