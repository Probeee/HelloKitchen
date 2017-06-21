package tw.org.iii.hellokitchen.Activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.Base64;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.org.iii.hellokitchen.Entity.Recipes_Material;
import tw.org.iii.hellokitchen.Entity.Recipes_Method;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.TheDefined;


public class ActRecipeModify extends AppCompatActivity
{
    private ArrayList<EditText> txtMethodList;    //EditText 食譜製作方式 List
    private ArrayList<EditText> txtMaterialList;  //EditText 食譜食材 List
    private ArrayList<String> myRecipeMethodList;   //存放食譜製作方式 List
    private ArrayList<String> myRecipeMaterialList; //存放食譜食材 List
    private final List<Recipes_Material> myRMList  = new ArrayList<>();
    private final List<Recipes_Method> myRMdList = new ArrayList<>();
    Bitmap recipeBitmap;
    byte recipeImgBytes[];
    String recipeImgBytesBase64 = "";
    private String userEmail;

    String recipeId;
    String recipeName;
    String recipeAmount;
    Boolean recipeStatus ;
    String recipeCooktime ;
    String recipePicture ;
    String memberID ;
    String recipeDetail ;
    String recipeUploadDate ;


    /*加入食譜照片事件(開啟相機)*/
    private View.OnClickListener btnRecipeImgCamera_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且
                帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult*/
            ContentValues value = new ContentValues();
            value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri= ActRecipeModify.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
            startActivityForResult(intent, TheDefined.CAMERA_ID);
        }
    };
    /*加入食譜照片事件(開啟相簿)*/
    private View.OnClickListener btnRecipeImg_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            /*開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
                          為點選相片後返回程式呼叫onActivityResult*/
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, TheDefined.PHOTO_ID);
        }
    };

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == TheDefined.CAMERA_ID || requestCode == TheDefined.PHOTO_ID ) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = ActRecipeModify.this.getContentResolver();

            try
            {
                //讀取照片，型態為Bitmap
                recipeBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                recipeBitmap = scaleBitmapDown(BitmapFactory.decodeStream(cr.openInputStream(uri)), 800);
               // BitmapDrawable background = new BitmapDrawable(recipeBitmap);
                llImageView.setImageBitmap(recipeBitmap);
               // llImageView.setBackground(background);
                //llImageView.getBackground().setAlpha(100);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                recipeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream );
                recipeImgBytes = stream.toByteArray();
                recipeImgBytesBase64 = Base64.encodeBase64String(recipeImgBytes);


            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    /*照片縮小處理*/
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension)
    {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private RadioGroup.OnCheckedChangeListener radioStatus_Checked = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            int p = group.indexOfChild((RadioButton) findViewById(checkedId));
            int count = group.getChildCount();
            switch (checkedId) {
                case R.id.radioStatusFalse:
                    recipeStatus = false;
                    break;
                case R.id.radioStatusTrue:
                    recipeStatus = true;
                    break;
            }
        }
    };

    /*動態新增 txtMethod 事件*/
    private View.OnClickListener btnAddMethodTxtList_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //在view中新增一筆新的list 你可以直接在 personal list 直接增加一筆然後再創立一次view
            myRecipeMethodList.add("");
            addListView();
        }
    };

    //刪除 txtMethod
    private View.OnClickListener btnDelete_Click= new View.OnClickListener() {

        @Override
        public void onClick(View v)
        {
            if (myRecipeMethodList.size() == 1)
            {
                Toast.makeText(ActRecipeModify.this, "最少要一個步驟", Toast.LENGTH_SHORT).show();
                return;
            }
            Button delBtn =  (Button)v; //在new 出所按下的按鈕
            int id = delBtn.getId(); //獲取被點擊的按鈕的id
            Log.i("id", String.valueOf(id));
            txtMethodList.get(id); //從 objectList得到此比資料
            txtMethodList.remove(id);
            myRecipeMethodList.remove(id);
            addListView(); //重新整理 view
        }
    };

    /*動態新增 txtMaterial 事件*/
    private View.OnClickListener btnAddMaterialTxtList_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            myRecipeMaterialList.add("");
            addListView();
        }
    };
    //刪除 txtMaterial
    private View.OnClickListener btnDeleteMaterial_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myRecipeMaterialList.size() == 1)
            {
                Toast.makeText(ActRecipeModify.this, "最少要一個食材", Toast.LENGTH_SHORT).show();
                return;
            }
            Button delBtn =  (Button)v; //在new 出所按下的按鈕
            int id = delBtn.getId(); //獲取被點擊的按鈕的id
            Log.i("id", String.valueOf(id));
            txtMaterialList.get(id); //從 objectList得到此比資料
            txtMaterialList.remove(id);
            myRecipeMaterialList.remove(id);
            addListView(); //重新整理 view
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_recipe_upload);

        SharedPreferences table = ActRecipeModify.this.getSharedPreferences("LoginUser",0);
        userEmail = table.getString("UserEmail","");
        Bundle bundle = getIntent().getExtras();
        recipeId = bundle.getString("recipeId");
        recipeName = bundle.getString("recipeName");
        recipeAmount = bundle.getString("recipeAmount");
        recipeStatus =  bundle.getBoolean("recipeStatus");
        recipeCooktime = bundle.getString("recipeCooktime");
        recipePicture =bundle.getString("recipePicture");
        memberID =bundle.getString("MemberID");
        recipeDetail = bundle.getString("recipeDetail");
        recipeUploadDate = bundle.getString("recipeUploadDate");
        InitialComponent();
        LoadJSONData(recipeId);

    }

    private void InitialComponent()
    {
        buttonView = LayoutInflater.from(ActRecipeModify.this).inflate(R.layout.activity_act_recipe_upload_object_button, null);
        buttonViewMaterial = LayoutInflater.from(ActRecipeModify.this).inflate(R.layout.activity_act_recipe_upload_object_button_material, null);

        txtMethodList = new ArrayList<>();
        txtMaterialList = new ArrayList<>();

        myRecipeMaterialList = new ArrayList<>();
       // myRecipeMaterialList.add("");
        myRecipeMethodList = new ArrayList<>();
        //myRecipeMethodList.add("");

        ll_in_sv = (LinearLayout) findViewById(R.id.ll_in_sv);
        ll_in_sv_Material = (LinearLayout) findViewById(R.id.ll_in_sv_Material);
        llImageView = (ImageView) findViewById(R.id.llimageView_recipe);
       // llImageView = (LinearLayout) findViewById(R.id.llImageView);

        btnAddMethodTxtList = (Button) buttonView.findViewById(R.id.btnAddMethodTxtList);
        btnAddMethodTxtList.setOnClickListener(btnAddMethodTxtList_Click);
        btnAddMaterialTxtList = (Button) buttonViewMaterial.findViewById(R.id.btnAddMaterialTxtList);
        btnAddMaterialTxtList.setOnClickListener(btnAddMaterialTxtList_Click);

        /*固定EditText和Button初始*/
        txtRecipeName = (EditText) findViewById(R.id.txtRecipeName);
        txtRecipeAmount = (EditText) findViewById(R.id.txtRecipeAmount);
        txtRecipeCookTime = (EditText) findViewById(R.id.txtRecipeCookTime);
        txtRecipeDetail = (EditText) findViewById(R.id.txtRecipeDetail);

        btnRecipeImgCamera = (Button) findViewById(R.id.btnRecipeImgCamera);
        btnRecipeImgCamera.setOnClickListener(btnRecipeImgCamera_Click);
        btnRecipeImg = (Button) findViewById(R.id.btnRecipeImg);
        btnRecipeImg.setOnClickListener(btnRecipeImg_Click);

        btnRecipeInsert = (Button) findViewById(R.id.btnRecipeInsert);
        btnRecipeInsert.setText("更新");
       // btnRecipeInsert.setOnClickListener(btnRecipeInsert_Click);
        //btnRecipeCancel = (Button) findViewById(R.id.btnRecipeCancel);
        //btnRecipeCancel.setOnClickListener(btnRecipeCancel_Click);

        radioStatus = (RadioGroup) findViewById(R.id.radioStatus);
        radioStatus.setOnCheckedChangeListener(radioStatus_Checked);
        recipeStatus = true;

       // ll_in_sv.removeAllViews();
      // ll_in_sv_Material.removeAllViews();

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

    private void LoadJSONData(final String recipeId)
    {
        new AsyncTask<Void, Object, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                String responseString = "";

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();  //用來當內層被丟進陣列內的JSON物件
                try
                {
                    jsonObject.put(TheDefined.Android_JSON_Key_Recipe_id, recipeId);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                Request request = new Request.Builder()
                        .url(TheDefined.Web_Server_URL + "/AndroidRecipeDetailServlet")
                        .post(body)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        responseString = response.body().string();

                        try
                        {
                            jsonObject = new JSONObject(responseString);

                            JSONArray jsonRecipeMaterial = new JSONArray(jsonObject.get(TheDefined.Android_JSONArray_Key_Recipe_Material).toString());
                            for (int i = 0; i < jsonRecipeMaterial.length(); i++)
                            {
                                JSONObject jsonRM = new JSONObject(jsonRecipeMaterial.get(i).toString());
                                Recipes_Material myRM = new Recipes_Material(jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Material_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Material_name),
                                        "noData",
                                        "noData");
                                myRMList.add(myRM);
                            }
                            JSONArray jsonRecipeMethod = new JSONArray(jsonObject.get(TheDefined.Android_JSONArray_Key_Recipe_Method).toString());
                            for (int i = 0; i < jsonRecipeMethod.length(); i++)
                            {
                                JSONObject jsonRM = new JSONObject(jsonRecipeMethod.get(i).toString());
                                Recipes_Method myRMd = new Recipes_Method(jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_id),
                                        jsonRM.getString(TheDefined.Android_JSON_Key_Recipe_Method_detail),
                                        "noData");
                                myRMdList.add(myRMd);
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
                AddDataToView();
            }
        }.execute();

    }

    private void AddDataToView()
    {
        Picasso
                .with(this)
                .load(recipePicture)
                .config(Bitmap.Config.ALPHA_8)
                .resize(llImageView.getMeasuredWidth(), llImageView.getMeasuredHeight())
                .tag(recipeId)
                .placeholder(R.drawable.photo)   // optional
                .error(R.drawable.icon_pictureloading_error)      // optional
                .into(llImageView);

        txtRecipeName.setText(recipeName);
        txtRecipeDetail.setText(recipeDetail);
        txtRecipeAmount.setText(recipeAmount);
        txtRecipeCookTime.setText( recipeCooktime.replace("分鐘",""));
        addListViewFromData();
        addListView();

    }

    private void addListViewFromData()
    {
        for(int i=0;i<myRMList.size();i++)
        {
            myRecipeMaterialList.add(myRMList.get(i).getMaterial_name());
        }
        for(int i=0;i<myRMdList.size();i++)
        {
            myRecipeMethodList.add(myRMdList.get(i).getMethod_detail());
        }
    }

    /*動態新增 txtMethod 和 txtMaterial 方法*/
    public void addListView()
    {
        ll_in_sv.removeAllViews();
        ll_in_sv_Material.removeAllViews();
        //recipeMaterial資料來源
        for (int i =0; i < myRecipeMaterialList.size(); i++)
        {
            View view = LayoutInflater.from(ActRecipeModify.this).inflate(R.layout.activity_act_recipe_upload_object_material, null); //物件來源
            LinearLayout llMaterial = (LinearLayout) view.findViewById(R.id.llMaterial); //取得recipe_upload_object中LinearLayout

            lblMaterial = (TextView) llMaterial.findViewById(R.id.lblMaterial);
            lblMaterial.setText(String.valueOf(i + 1));

            txtMaterial = (EditText) llMaterial.findViewById(R.id.txtMaterial); //獲取LinearLayout中各元件
            txtMaterial.setText(myRecipeMaterialList.get(i)); //放入recipeMaterial相關資料來源
            txtMaterial.setId(i);  //將txtMaterial帶入id 以供監聽時辨識使用

            btnDeleteMaterial = (Button) llMaterial.findViewById(R.id.btnDeleteMaterial);
            btnDeleteMaterial.setId(i);  //將btnDeleteMaterial帶入id 以供監聽時辨識使用
            btnDeleteMaterial.setOnClickListener(btnDeleteMaterial_Click); //設定監聽method

            txtMaterialList.add(txtMaterial); //將txtMaterial的元件放入map並存入list中

            //將上面新建的例元件新增到主頁面的ll_in_sv中
            ll_in_sv_Material.addView(view);
        }

        /*每次view更新將txtMaterial上的文字記錄下來*/
        for (int i = 0; i < txtMaterialList.size(); i++)
        {
            MyRecipeMaterialTextWatcher(txtMaterialList.get(i));
        }

        //最後一筆都放上新增按鈕
        ll_in_sv_Material.addView(buttonViewMaterial);

        //recipeMethod資料來源
        for (int i = 0; i <myRecipeMethodList.size(); i++) {

            View view = LayoutInflater.from(ActRecipeModify.this).inflate(R.layout.activity_act_recipe_upload_object, null); //物件來源
            LinearLayout llMethod = (LinearLayout) view.findViewById(R.id.llMethod); //取得recipe_upload_object中LinearLayout

            lblMethod = (TextView) llMethod.findViewById(R.id.lblMethod);
            lblMethod.setText(String.valueOf(i + 1));

            txtMethod = (EditText) llMethod.findViewById(R.id.txtMethod); //獲取LinearLayout中各元件
            txtMethod.setText(myRecipeMethodList.get(i)); //放入recipeMethod相關資料來源
            txtMethod.setId(i);  //將txtMethod帶入id 以供監聽時辨識使用

            btnDelete = (Button) llMethod.findViewById(R.id.btnDelete);
            btnDelete.setId(i);  //將btnDelete帶入id 以供監聽時辨識使用
            btnDelete.setOnClickListener(btnDelete_Click); //設定監聽method

            txtMethodList.add(txtMethod); //將txtMethod的元件放入map並存入list中

            //將上面新建的例元件新增到主頁面的ll_in_sv中
            ll_in_sv.addView(view);
        }
        /*每次view更新將txtMethod上的文字記錄下來*/
        for (int i = 0; i < txtMethodList.size(); i++)
        {
            MyRecipeMethodTextWatcher(txtMethodList.get(i));
        }
        //最後一筆都放上新增按鈕
        ll_in_sv.addView(buttonView);
    }


    /*將RecipeMaterial更新最新輸入資料*/
    private void MyRecipeMaterialTextWatcher(final EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                myRecipeMaterialList.set(editText.getId(), editText.getText().toString());
            }
        });
    }

    /*將RecipeMethod更新最新輸入資料*/
    private void MyRecipeMethodTextWatcher(final EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                myRecipeMethodList.set(editText.getId(), editText.getText().toString());
            }
        });
    }


    private TextView lblMethod, lblMaterial;
    private EditText txtMethod, txtMaterial, txtRecipeName, txtRecipeAmount,
            txtRecipeCookTime, txtRecipeDetail;
    private Button btnRecipeInsert, btnRecipeCancel, btnAddMethodTxtList,
            btnDelete, btnAddMaterialTxtList, btnDeleteMaterial, btnRecipeImgCamera, btnRecipeImg;
    private LinearLayout ll_in_sv, ll_in_sv_Material;
    private View buttonView, buttonViewMaterial;
    //private LinearLayout llImageView;
    private ImageView llImageView;
    private RadioGroup radioStatus;
}
