package tw.org.iii.hellokitchen.Frag_Ingredients;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tw.org.iii.hellokitchen.Entity.Ingredients;
import tw.org.iii.hellokitchen.R;
import tw.org.iii.hellokitchen.Utility.MyDBHelper;
import tw.org.iii.hellokitchen.Utility.PackageManagerUtils;
import tw.org.iii.hellokitchen.Utility.TheDefined;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frag_Foods_Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Foods_Register extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //宣告
    /*private static final String CLOUD_VISION_API_KEY = "AIzaSyCUQswIVonpnGupn5qZM1PlniXQfh8S2Z4";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String TAG = ActMain.class.getSimpleName();
    private final static int CAMERA_ID = 66 ;
    private final static int PHOTO_ID = 99 ;*/

    private View v ;
    private TextView lblDetails;
    private EditText txtIngredient, txtAmount, txtBuyDate, txtDeadDate;
    private Button btnCamera, btnPhoto, btnAddAmount, btnMinusAmount,
            btnBuyDatePicker, btnDeadDatePicker, btnInsert, btnClear;

    private Ingredients myIngredients;
    private int index =0;
    Calendar buyDate = Calendar.getInstance();
    private List<String> items;
    private List<String> itemsAfterTranslate;

    /*購買日期PickerDialog*/
    private DatePickerDialog.OnDateSetListener buyDate_setting = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            txtBuyDate.setText(String.valueOf(year) + "/" +
                    String.valueOf(month + 1) + "/" +
                    String.valueOf(dayOfMonth));
            myIngredients.setBuyDate(txtBuyDate.getText().toString());
            buyDate.set(year, month , dayOfMonth);
        }
    };

    /*到期日期PickerDialog*/
    private DatePickerDialog.OnDateSetListener buyDead_setting = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            if (calendar.getTimeInMillis() < buyDate.getTimeInMillis()) {
                Toast.makeText(getActivity(), "請選擇購買日期之後日期", Toast.LENGTH_SHORT).show();
                return;
            }
            txtDeadDate.setText(String.valueOf(year) + "/" +
                    String.valueOf(month + 1) + "/" +
                    String.valueOf(dayOfMonth));
            myIngredients.setDeadDate(txtDeadDate.getText().toString());
        }
    };

    /*購買日期按鈕事件*/
    private View.OnClickListener btnBuyDatePicker_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar today = Calendar.getInstance();
            DatePickerDialog message = new DatePickerDialog(getActivity(), buyDate_setting, today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH), today.get(Calendar.DATE));
            message.show();
        }
    };

    /*到期日期按鈕事件*/
    private View.OnClickListener btnDeadDatePicker_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myIngredients.getBuyDate().equals("")) {
                Toast.makeText(getActivity(), "請先選擇購買日期", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Calendar today = Calendar.getInstance();
                DatePickerDialog message = new DatePickerDialog(getActivity(), buyDead_setting, today.get(Calendar.YEAR),
                        today.get(Calendar.MONTH), today.get(Calendar.DATE));
                message.getDatePicker().setMinDate(buyDate.getTimeInMillis());
                message.show();
            }

        }
    };

    /*重新輸入按鈕事件*/
    private View.OnClickListener btnClear_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clear();
        }
    };

    /*開啟照相機按鈕事件*/
    private View.OnClickListener btnCamera_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且
                帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult*/
            ContentValues value = new ContentValues();
            value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri= getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
            startActivityForResult(intent, TheDefined.CAMERA_ID);
        }
    };

    /*開啟照片按鈕事件*/
    private View.OnClickListener btnPhoto_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            /*開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因
              為點選相片後返回程式呼叫onActivityResult*/

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, TheDefined.PHOTO_ID);
        }
    };

    /*加數量按鈕事件*/
    private View.OnClickListener btnAddAmount_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            myIngredients.AddAmount();
            txtAmount.setText(String.valueOf(myIngredients.getAmount()));
        }
    };

    /*減數量按鈕事件*/
    private View.OnClickListener btnMinusAmount_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myIngredients.MinusAmount();
            txtAmount.setText(String.valueOf(myIngredients.getAmount()));
        }
    };

    /*新增食材資料進SQLITE按鈕事件*/
    private View.OnClickListener btnInsert_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myIngredients.getName().equals("")) {
                txtIngredient.setError("食材名稱不能為空白");
            } else if (myIngredients.getAmount() == 0) {
                txtAmount.setError("食材數量至少為1");
            } else if (myIngredients.getBuyDate().equals("")) {
                txtBuyDate.setError("食材購買日期不能為空白");
            } else if (myIngredients.getDeadDate().equals("")) {
                txtDeadDate.setError("食材到期日期不能為空白");
            } else {
                insertIngredients();
                myIngredients = new Ingredients();
            }
        }
    };

    private TextWatcher txtAmount_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!txtAmount.getText().toString().equals("")) {
                myIngredients.setAmount(Integer.parseInt(txtAmount.getText().toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private TextWatcher txtIngredient_TextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            myIngredients.setName(txtIngredient.getText().toString());
        }
    };

    private void clear() {
        txtIngredient.setText("");
        txtAmount.setText("");
        txtBuyDate.setText("");
        txtDeadDate.setText("");
        myIngredients = new Ingredients();
    }

    /*新增食材資料進SQLITE方法*/
    private void insertIngredients() {
        //登錄一個食材
        MyDBHelper dbh =  MyDBHelper.getInstance(getActivity());
        SQLiteDatabase sdb = dbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        values.put("ingredients_id", formatter.format(new Date(System.currentTimeMillis())).toString().trim());
        values.put("ingredients_name", myIngredients.getName().toString().trim());
        values.put("ingredients_buyDate", myIngredients.getBuyDate().toString().trim());
        values.put("ingredients_deadDate", myIngredients.getDeadDate().toString().trim());
        values.put("ingredients_amount", String.valueOf(myIngredients.getAmount()).trim());
        values.put("member_id", "null");

        try
        {
            sdb.insertOrThrow("tingredients", null, values);
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(),"食材登錄成功",Toast.LENGTH_LONG).show();
            clear();
        }
        catch (SQLiteConstraintException e)
        {
            Toast.makeText(getActivity(),"食材登錄失敗",Toast.LENGTH_LONG).show();
        }
    }

    public Frag_Foods_Register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Foods_Register.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Foods_Register newInstance(String param1, String param2) {
        Frag_Foods_Register fragment = new Frag_Foods_Register();
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
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.frag__foods__register, container, false);
        InitialComponet(v);

        return v;

    }

    //拍照完畢或選取圖片後呼叫此函式
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == TheDefined.CAMERA_ID || requestCode == TheDefined.PHOTO_ID ) && data != null) {
            //取得照片路徑uri
            Uri uri = data.getData();
            ContentResolver cr = getActivity().getContentResolver();

            try {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = scaleBitmapDown(BitmapFactory.decodeStream(cr.openInputStream(uri)), 800);

                callCloudVision(bitmap);


            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    /**Google Vision影像辨識**/
    private void callCloudVision(final Bitmap bitmap) throws IOException
    {
        // Switch text to loading
        lblDetails.setText(R.string.loading_message);
        final ProgressDialog message = new ProgressDialog(getActivity());
        message.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        message.setTitle("正在讀取資料");
        message.setCancelable(false);
        message.show();

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(TheDefined.CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getActivity().getPackageName();
                                    visionRequest.getRequestHeaders().set(TheDefined.ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getActivity().getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(TheDefined.ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TheDefined.TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TheDefined.TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TheDefined.TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result)
            {
                callTrans(result);
                lblDetails.setText(result);
                message.dismiss();
            }
        }.execute();
    }

    /*解析分析結果*/
    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        //String message = "I found these things:\n\n";
        String message = "";
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null)
        {
            for (EntityAnnotation label : labels)
            {
                //message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += String.format(Locale.US, " %s", label.getDescription());
                message += ",";
            }
        } else {
            message += "nothing";
        }

        return message;
    }




    /*照片縮小處理*/
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

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


    /**Google 翻譯**/
    private void callTrans(String result)
    {
        final String strMsg[] = result.split(",");
        items = new ArrayList<>();
        itemsAfterTranslate = new ArrayList<>();
        for(int i=0;i<strMsg.length;i++)
        {
            Log.d("test",strMsg[i]);
            items.add(strMsg[i]);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Translate t = new Translate.Builder(
                        AndroidHttp.newCompatibleTransport()
                        , GsonFactory.getDefaultInstance(), null)
                        //Need to update this to your App-Name
                        .setApplicationName("HelloKitchen")
                        .build();
                Translate.Translations.List list = null;
                try
                {
                    list = t.new Translations().list(items, "zh-TW");
                } catch (IOException e)
                {
                    Log.d("test",e.getMessage().toString());
                }
                //Set your API-Key from https://console.developers.google.com/
                Log.d("test","BeforeExcuted");
                list.setKey("AIzaSyAWwN_1aKCGPkR7oPrXBGzzvJGiY12wVFo");
                TranslationsListResponse response = null;
                try
                {
                    response = list.execute();
                }
                catch (IOException e)
                {
                    Log.d("test",e.getMessage().toString());
                }
                Log.d("test","Excuted");
                for(TranslationsResource tr : response.getTranslations())
                {
                    Log.d("test",tr.getTranslatedText());
                    itemsAfterTranslate.add(tr.getTranslatedText().toString());
                }

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        //這邊是呼叫main thread handler幫我們處理UI部分
                        //將解析結果變成List給使用者選擇
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        AlertDialog dialog = builder.setTitle("選擇您的食材").setSingleChoiceItems(itemsAfterTranslate.toArray(new String[itemsAfterTranslate.size()]),0, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                index = which;
                                //String[] city = getResources().getStringArray(R.array.city);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        }).setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("測試用",String.valueOf(which));

                                txtIngredient.setText(itemsAfterTranslate.get(index));
                                myIngredients.setName(itemsAfterTranslate.get(index));
                            }
                        }).create();

                        dialog.show();
                    }
                });
            }
        }).start();
    }




    /*初始化UI物件*/
    private void InitialComponet(View v) {

        lblDetails = (TextView) v.findViewById(R.id.lblDetails);
        txtIngredient = (EditText) v.findViewById(R.id.txtIngredient);
        txtIngredient.addTextChangedListener(txtIngredient_TextWatcher);
        txtAmount = (EditText) v.findViewById(R.id.txtAmount);
        txtAmount.addTextChangedListener(txtAmount_TextWatcher);
        txtBuyDate = (EditText) v.findViewById(R.id.txtBuyDate);
        txtBuyDate.setCursorVisible(false);
        txtBuyDate.setFocusable(false);
        txtBuyDate.setFocusableInTouchMode(false);
        txtDeadDate = (EditText) v.findViewById(R.id.txtDeadDate);
        txtDeadDate.setCursorVisible(false);
        txtDeadDate.setFocusable(false);
        txtDeadDate.setFocusableInTouchMode(false);

        btnCamera = (Button) v.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(btnCamera_Click);
        btnPhoto = (Button) v.findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(btnPhoto_Click);
        btnAddAmount = (Button) v.findViewById(R.id.btnAddAmount);
        btnAddAmount.setOnClickListener(btnAddAmount_Click);
        btnMinusAmount = (Button) v.findViewById(R.id.btnMinusAmount);
        btnMinusAmount.setOnClickListener(btnMinusAmount_Click);
        btnBuyDatePicker = (Button) v.findViewById(R.id.btnBuyDatePicker);
        btnBuyDatePicker.setOnClickListener(btnBuyDatePicker_Click);
        btnDeadDatePicker = (Button) v.findViewById(R.id.btnDeadDatePicker);
        btnDeadDatePicker.setOnClickListener(btnDeadDatePicker_Click);
        btnInsert = (Button) v.findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(btnInsert_Click);
        btnClear = (Button) v.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(btnClear_Click);
        myIngredients = new Ingredients();
    }
}