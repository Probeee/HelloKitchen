package tw.org.iii.hellokitchen.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tw.org.iii.hellokitchen.Entity.Recipes;
import tw.org.iii.hellokitchen.R;


/**
 * Created by Kevin on 2017/6/11.
 */

public class RecipeGalleryAdapter extends ArrayAdapter<Recipes> implements AbsListView.OnScrollListener
{
    //圖片解析大小為 100 * 100
    private static final int PHOTO_WIDTH = 100;
    private static final int PHOTO_HEIGHT = 100;

    /** 紀錄所有正在下載圖片或等待的task.**/
    private Set<RecipeGalleryAdapter.BitmapDownloadTask> taskCollection;

    /**用來做圖片Caching**/
    private LruCache<String, Bitmap> memoryCache;

    /**Gallery View**/
    private GridView gridViewPhoto;

    /**第一張可見圖片的索引.**/
    private int firstVisiblePhoto;

    /**圖片位址或路徑列表  使用整個Recipe物件**/
    private List<Recipes> recipeObjects;

    /**螢幕一次可以看到多少張圖片.**/
    private int visiblePhotoCount;

    /**紀錄是否是剛進入此程式，用來解決程式不滑動螢幕，不會下載圖片的問題**/
    private boolean isFirstEnter = false ;

    private boolean isLoaded = false;

    /**建構子初始化**/
    public RecipeGalleryAdapter(Context context, List<Recipes> objects, GridView photoGridView,Boolean isFirstEnter)
    {
        super(context, 0, objects );
        this.recipeObjects = objects;
        this.gridViewPhoto = photoGridView;
        this.taskCollection = new HashSet<>();
        this.isFirstEnter = isFirstEnter;

        // 取得app可用的最大記憶體

        int maxMemory = (int) Runtime.getRuntime().maxMemory();

        // 圖片暫存大小為app可用最大記憶體的1/4

        int cacheSize = maxMemory /4;

        this.memoryCache = new LruCache<String, Bitmap>( cacheSize )
        {
            @Override
            protected int sizeOf( String key, Bitmap bitmap )
            {
                return bitmap.getByteCount();
            }
        };
        this.gridViewPhoto.setOnScrollListener( this );
    }

    /**當GridView靜止時才下載圖片，如果正在滑動，則取消下載工作**/
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if ( scrollState == SCROLL_STATE_IDLE )
        {
            loadBitmaps(this.firstVisiblePhoto, this.visiblePhotoCount);
        }
        else
            cancelAllTasks();
    }

    private void loadBitmaps( int firstVisiblePhoto, int visiblePhotoCount )
    {
        isLoaded = true;
        for ( int i = firstVisiblePhoto; i < firstVisiblePhoto + visiblePhotoCount  ; i++ )
        {
            String imageId = this.recipeObjects.get(i).getRecipe_id();
            String imageUrl = this.recipeObjects.get(i).getRecipe_picture();
            Bitmap bitmap = this.getBitmapFromMemoryCache( imageId );
            ImageView imageView = (ImageView) gridViewPhoto.findViewWithTag( this.recipeObjects.get(i).getRecipe_id() );
            if(bitmap!=null)
            {
                if ( imageView != null )
                    imageView.setImageBitmap( bitmap );
            }
            else
            {

                BitmapDownloadTask bitmapTask = new BitmapDownloadTask();
                this.taskCollection.add( bitmapTask );
                bitmapTask.execute( imageId,imageUrl );
            }
        }
    }



    /** 取消所有下載中和等待中的圖片下載工作。**/
    public void cancelAllTasks()
    {
        if ( this.taskCollection != null )
        {
            for (BitmapDownloadTask task : this.taskCollection)
            {
                task.cancel(true);
            }
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        this.firstVisiblePhoto = firstVisibleItem;
        this.visiblePhotoCount = visibleItemCount;
        // 下載圖片的工作是由onScrollStateChanged()裡面啟動
        // 但第一次進入GridView時，onScrollStateChanged()不會被呼叫到所以要進行第一次進入GridView下載圖片的動作。
        if ( isFirstEnter && visibleItemCount > 0 )
        {
            loadAllBitmapsFromHttp(firstVisibleItem, visibleItemCount);
            isFirstEnter = false;
            return;
        }
        loadBitmaps( firstVisibleItem, visibleItemCount );
    }

    /**一次載入所有Bitmap物件，且啟動非同步載入圖片工作。**/
    private void loadAllBitmapsFromHttp(int firstVisibleItem, int visibleItemCount)
    {
        for ( int i = 0; i < recipeObjects.size() ; i++ )
        {
            String imageUrl = this.recipeObjects.get(i).getRecipe_picture();
            String imageId = this.recipeObjects.get(i).getRecipe_id();
            // 從遠端伺服器來
            if (imageUrl.startsWith( "http" ))
             {
                 Log.d("http","true");
                BitmapDownloadTask bitmapTask = new BitmapDownloadTask();
                this.taskCollection.add( bitmapTask );
                 this.isLoaded = false;
                bitmapTask.execute( imageId,imageUrl );
             }
            else
            {
                System.out.print("格式錯誤:"+recipeObjects.get(i).getRecipe_picture());
            }
        }
        loadBitmaps( firstVisibleItem, visibleItemCount );
    }

    /**圖片非同步下載的工作執行續**/
    class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap>
    {
        // Switch text to loading
        private String bitmapId;
        private String bitmapUrl;
        //根據傳來的param來抓下載圖片
        @Override
        protected Bitmap doInBackground( String... params )
        {
            //於背景中執行下載動作
            this.bitmapId = params[ 0 ];
            this.bitmapUrl = params[ 1 ];
            Bitmap bitmap = null;
            Log.d("imageId",bitmapId+"");
            Log.d("imageUrl",bitmapUrl+"");
            try
            {
                bitmap = downloadBitmap( this.bitmapUrl, PHOTO_WIDTH, PHOTO_HEIGHT );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

            // 圖片成功下載後，直接先加到暫存中
            if ( bitmap != null )
            {
                //用key value形勢儲存
                addBitmapToMemoryCache(this.bitmapId, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute( Bitmap bitmap )//執行完上方的部分會來此
        {
            if(isLoaded==true)
            // 根據Tag找到對應的ImageView元件，把剛剛下載的圖片呈現出來
            {
                ImageView imageView = (ImageView) gridViewPhoto.findViewWithTag(bitmapId);
                if (imageView != null && bitmap != null)
                    imageView.setImageBitmap(bitmap);
                //taskCollection.remove( this );
            }
        }
    }
    /**下載Bitmap。**/
    //bitmapUrl Bitmap下載網址
    //width Bitmap要呈現的寬度
    //height Bitmap要呈現的高度
    public static Bitmap downloadBitmap( String bitmapUrl, int width, int height ) throws IOException
    {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//進行縮圖
        InputStream in = getInputStreamFromURL( bitmapUrl );
        try
        {
            bitmap = BitmapFactory.decodeStream( in, null, options );
        }
        finally
        {
            if ( in != null )
                in.close();
        }
        options.inSampleSize = calculateImageSampleSize( options, width, height );

        options.inJustDecodeBounds = false;
        in = getInputStreamFromURL( bitmapUrl );
        try
        {
            bitmap = BitmapFactory.decodeStream( in, null, options );
        }
        finally
        {
            if ( in != null )
                in.close();
            return bitmap;
        }

    }
    /**把圖片做caching **/
    // key LruCache的鍵，這裡是圖片所屬的物件Id
    //bitmap
    public void addBitmapToMemoryCache( String key, Bitmap bitmap )
    {
        if ( this.getBitmapFromMemoryCache( key ) == null )
            //如果再Cache裡面找不到相對應的Key就放去cache
            this.memoryCache.put( key, bitmap );
    }
    /**從LruCache中取回圖片**/
    // key 用圖片所屬的Id從cache中取回圖片
    //return 圖片Bitmap或null
    public Bitmap getBitmapFromMemoryCache( String key )
    {
        Bitmap bitmap = this.memoryCache.get( key );
        return bitmap;
    }

    /**從URL建立連線後取得InputStream**/
    public static InputStream getInputStreamFromURL( final String URL_String ) throws IOException
    {
        HttpURLConnection conn = null;
        URL url = new URL(URL_String);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        return conn.getInputStream();
    }

    /**依照要顯示的長寬來計算Bitmap要sample的比例**/
    //reqWidth 需要的寬度
    //reqHeight 需要的高度

    public static int calculateImageSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight )
    {
        final int rawH = options.outHeight;
        final int rawW = options.outWidth;
        int imageSampleSize = 4;

        if ( rawH > reqHeight || rawW > reqWidth )
        {
            final int halfH = rawH / 2;
            final int halfW = rawW / 2;

            while ( ( ( halfH / imageSampleSize ) > reqHeight ) && ( ( halfW / imageSampleSize ) > reqWidth ) )
                imageSampleSize *= 2;
        }
        return imageSampleSize;
    }

    /**從List裡面的物件產生畫面**/
    @Override
    public View getView(int position, View convertView, ViewGroup parent )
    {
        //根據view上的position當作index,來抓取該物件的資料
        final String recipe_id = this.getItem(position).getRecipe_id();
        final String recipe_name = this.getItem(position).getRecipe_name();
        final String recipe_producer_id = this.getItem(position).getMember_id();

        View view;
        if (convertView == null)
        {
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.recipe_photo_view, null);
        }
        else
        {   // 從cache載入的圖
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.photo_Recipe);
        TextView textView_RecipeName = (TextView) view.findViewById(R.id.textView_RecipeName);
        TextView textView_ProducerId = (TextView) view.findViewById(R.id.textView_RecipeProducer);
        // 給圖片設置一個tag已保證在做非同步載入圖片時順序不會亂掉
        imageView.setTag(recipe_id);
        textView_RecipeName.setTag(recipe_id);
        textView_ProducerId.setTag(recipe_id);
        //將網址跟imageView物件傳至setImageView這個Class做事情
        setImageView(recipe_id, imageView);
        //設定每個區塊上的資訊
        textView_RecipeName.setText(recipe_name);
        textView_ProducerId.setText(recipe_producer_id);
        //三個View元件共用一個事件
        imageView.setOnClickListener(itemClick);
        textView_RecipeName.setOnClickListener(itemClick);
        textView_ProducerId.setOnClickListener(itemClick);
        return view;
    }
    View.OnClickListener itemClick = new View.OnClickListener()
    {
        //給GridView上的每個區塊做觸發
        @Override
        public void onClick(View v)
        {
            Toast.makeText(getContext(),""+v.getTag(),Toast.LENGTH_SHORT).show();
        }
    };
    /**給ImageView設置圖片，這方法會去找暫存，如果沒有暫存圖，則直接先給定一張預設圖。**/
    private void setImageView(String imageId, ImageView imageView )
    {
        Bitmap bitmap = getBitmapFromMemoryCache( imageId );
        if ( bitmap != null )
            imageView.setImageBitmap( bitmap );
        else
            imageView.setImageResource( R.drawable.photo );
    }

}
