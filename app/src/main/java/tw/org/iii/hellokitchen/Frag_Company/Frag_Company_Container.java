package tw.org.iii.hellokitchen.Frag_Company;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Company;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.CompanyGalleryAdapterPicasso;
import tw.org.iii.hellokitchen.Utility.TheDefined;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Company_Container#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Company_Container extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * 用來展示圖片的Gallery
     */
    public GridView photoGallery;

    /**
     * GridView所使用的Adapter
     */

    private CompanyGalleryAdapterPicasso adapter;

    private List<Company> companyList;



    public Frag_Company_Container() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Company.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Company_Container newInstance(String param1, String param2) {
        Frag_Company_Container fragment = new Frag_Company_Container();
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
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_company_container, container, false);
        if(companyList == null)
        {
            companyList = new ArrayList<>();
        }
        else
        {
            companyList.clear();
        }

        this.photoGallery = (GridView)v.findViewById(R.id.gridCompanyPhoto );
        return v;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            servlet_Recipe_Data();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        companyList.clear();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        companyList.clear();
    }

    /*從servlet將廠商抓下來*/
    private void servlet_Recipe_Data() throws IOException, JSONException
    {
        final ProgressDialog message = new ProgressDialog(getActivity());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("讀取中...");
        message.setCancelable(false);
        message.show();

        new AsyncTask<Object, Object, List<Company>>()
        {
            @Override
            protected List<Company> doInBackground(Object... params)
            {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidCompanyServlet")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseString = response.body().string();
                        try {
                            JSONArray responseJSON = new JSONArray(responseString);
                            for (int i = 0; i < responseJSON.length(); i++)
                            {
                                JSONObject jsonObject = new JSONObject(responseJSON.get(i).toString());
                                Company myCompanies = new Company(
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_id),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_name),
                                        TheDefined.Web_Server_URL + "/" + jsonObject.getString(TheDefined.Android_JSON_Key_Company_logo),
                                        TheDefined.Web_Server_URL + "/" + jsonObject.getString(TheDefined.Android_JSON_Key_Company_cover),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Cover_intro),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_address),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_tel),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_email),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_owner),
                                        jsonObject.getString(TheDefined.Android_JSON_Key_Company_password),
                                        Boolean.valueOf(jsonObject.get(TheDefined.Android_JSON_Key_Company_status).toString()));


                                companyList.add(myCompanies);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    TheDefined.showToastByRunnable(getActivity(), "伺服器無法取得回應", Toast.LENGTH_LONG);
                    message.cancel();
                    e.printStackTrace();
                }

                return companyList;
            }

            @Override
            protected void onPostExecute(List<Company> objects)
            {
                super.onPostExecute(objects);
                adapter = new CompanyGalleryAdapterPicasso(getActivity(),  companyList, photoGallery);
                photoGallery.setAdapter( adapter );
                message.cancel();
            }
        }.execute();
    }



}
