package tw.org.iii.hellokitchen.Utility;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

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
 * Created by Kevin on 2017/6/10.
 */

public class RecipePhotoGalleryAdapter extends ArrayAdapter<Recipes> implements AbsListView.OnScrollListener
{
    private static final int PHOTO_WIDTH = 120;
    private static final int PHOTO_HEIGHT = 120;

    /**
     * 紀錄所有正在下載圖片或等待的task.
     */
    private Set<BitmapDownloadTask> taskCollection;

    /**
     * 用來做圖片Caching
     */
    private LruCache<String, Bitmap> memoryCache;

    /**
     * Gallery View
     */
    private GridView gridViewPhoto;

    /**
     * 第一章可見圖片的索引.
     */
    private int firstVisiblePhoto;

    /**
     * 圖片位址或路徑列表  使用整個物件。
     */
   // private List<String> photoURLs;
    private List<Recipes> photoObjects;
    /**
     * 螢幕一次可以看到多少張圖片.
     */
    private int visiblePhotoCount;

    /**
     * 紀錄是否是剛進入此程式，用來解決程式不滑動螢幕，不會下載圖片的問題.
     */
    private boolean isFirstEnter = true;

    private String selectedId ;
    /**
     *
     *
     * @param context
     * @param objects 圖片來源列表
     * @param photoWall GridView元件
     */
    public RecipePhotoGalleryAdapter(Context context, List<Recipes> objects, GridView photoWall )
    {
        super(context, 0, objects);
        this.photoObjects = objects;
        this.gridViewPhoto = photoWall;
        this.taskCollection = new HashSet<BitmapDownloadTask>();

        // 取得app可用的最大記憶體

        int maxMemory = (int) Runtime.getRuntime().maxMemory();

        // 圖片暫存大小為app可用最大記憶體的1/8

        int cacheSize = maxMemory / 8;

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

    /**
     * To use something other than TextViews for the array display, for instance, ImageViews,
     * or to have some of data besides toString() results fill the views, override getView(int, View, ViewGroup)
     * to return the type of view you want.
     */
    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        View view;
        if (convertView == null)
            view = LayoutInflater.from(this.getContext()).inflate(R.layout.recipe_photo_view, null);
        else
            // 從cache載入的圖
            view = convertView;

        final String recipe_id = this.getItem(position).getRecipe_id();
        final String url = this.getItem(position).getRecipe_picture();
        final String recipe_name = this.getItem(position).getRecipe_name();
        final String recipe_producer_id = this.getItem(position).getMember_id();
        ImageView photoView = (ImageView) view.findViewById(R.id.photo_Recipe);
        TextView textView_RecipeName = (TextView) view.findViewById(R.id.textView_RecipeName);
        TextView textView_ProducerId = (TextView) view.findViewById(R.id.textView_RecipeProducer);
        // 給圖片設置一個tag已保證在做非同步載入圖片時順序不會亂掉
        photoView.setTag(recipe_id);
        textView_RecipeName.setTag(recipe_id);
        textView_ProducerId.setTag(recipe_id);
        setImageView(url, photoView);
        textView_RecipeName.setText(recipe_name);
        textView_ProducerId.setText(recipe_producer_id);
        photoView.setOnClickListener(itemClick);
        textView_RecipeName.setOnClickListener(itemClick);
        textView_ProducerId.setOnClickListener(itemClick);

        return view;
    }

    View.OnClickListener itemClick = new View.OnClickListener()
    {
        //給GridView上的每個選項做觸發
        @Override
        public void onClick(View v)
        {
            Toast.makeText(getContext(),""+v.getTag(),Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 把圖片做caching
     *
     * @param key LruCache的鍵，這裡是圖片的URL位址
     * @param bitmap
     */
    public void addBitmapToMemoryCache( String key, Bitmap bitmap )
    {
        if ( this.getBitmapFromMemoryCache( key ) == null )
            this.memoryCache.put( key, bitmap );
    }

    /**
     * 從LruCache中取回圖片
     *
     * @param key 用圖片URL位址從cache中取回圖片
     * @return 圖片Bitmap或null
     */
    public Bitmap getBitmapFromMemoryCache( String key )
    {
        Bitmap bitmap = this.memoryCache.get( key );
        return bitmap;
    }

    /**
     * 當GridView靜止時才下載圖片，如果正在滑動，則取消下載工作
     */
    @Override
    public void onScrollStateChanged( AbsListView view, int scrollState )
    {
        if ( scrollState == SCROLL_STATE_IDLE )
            loadBitmaps( this.firstVisiblePhoto, this.visiblePhotoCount );
        else
            cancelAllTasks();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
    {
        this.firstVisiblePhoto = firstVisibleItem;
        this.visiblePhotoCount = visibleItemCount;

        // 下載圖片的工作是由onScrollStateChanged()裡面啟動

        // 但第一次進入GridView時，onScrollStateChanged()不會被呼叫到所以要進行第一次進入GridView下載圖片的動作。

        if ( isFirstEnter && visibleItemCount > 0 )
        {
            loadBitmaps( firstVisibleItem, visibleItemCount );
            isFirstEnter = false;
        }
    }

    /**
     * 載入Bitmap物件，這方法會在暫存中檢查所有螢幕可見的圖片物件，如果可見但不在暫存中，則啟動非同步載入圖片工作。
     *
     * @param firstVisiblePhoto 第一個可見圖片的位置索引
     * @param visiblePhotoCount 螢幕可見圖片的數量
     */
    private void loadBitmaps( int firstVisiblePhoto, int visiblePhotoCount )
    {
        for ( int i = firstVisiblePhoto; i < firstVisiblePhoto + visiblePhotoCount; i++ )
        {
            String imageUrl = this.photoObjects.get(i).getRecipe_picture();
            String imageId = this.photoObjects.get(i).getRecipe_id();
            System.out.println( "Load " + imageUrl );
            Bitmap bitmap = this.getBitmapFromMemoryCache( imageUrl );
            ImageView imageView = (ImageView) gridViewPhoto.findViewWithTag( this.photoObjects.get(i).getRecipe_id() );
            if ( bitmap == null )
            {
                // 從遠端伺服器來

                if ( imageUrl.startsWith( "http" ) )
                {

                    BitmapDownloadTask bitmapTask = new BitmapDownloadTask();
                    this.taskCollection.add( bitmapTask );
                    bitmapTask.execute( imageId,imageUrl );
                }
                else
                // 從檔案系統來

                {
                    bitmap = loadBitmapFromFile( imageUrl, PHOTO_WIDTH, PHOTO_HEIGHT );
                    if ( bitmap != null )
                        this.addBitmapToMemoryCache( imageUrl, bitmap );

                    if ( imageView != null && bitmap != null )
                        imageView.setImageBitmap( bitmap );
                }
            }
            else
            {
                if ( imageView != null && bitmap != null )
                    imageView.setImageBitmap( bitmap );
            }
        }
    }

    /**
     * 給ImageView設置圖片，這方法會去找暫存，如果沒有暫存圖，則直接先給定一張預設圖。
     *
     * @param imageUrl
     * @param imageView
     */
    private void setImageView(String imageUrl, ImageView imageView )
    {
        Bitmap bitmap = getBitmapFromMemoryCache( imageUrl );
        if ( bitmap != null )
            imageView.setImageBitmap( bitmap );
        else
            imageView.setImageResource( R.drawable.photo );
    }

    /**
     * 取消所有下載中和等待中的圖片下載工作。
     */
    public void cancelAllTasks()
    {
        if ( this.taskCollection != null )
            for ( BitmapDownloadTask task : this.taskCollection )
                task.cancel( true );
    }

    /**
     * 圖片非同步下載的工作執行續
     *
     *
     *
     */
    class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap>
    {
        // Switch text to loading


        private String bitmapUrl;
        private String bitmapId;
        @Override
        protected Bitmap doInBackground( String... params )
        {
            this.bitmapId = params[ 0 ];
            this.bitmapUrl = params[ 1 ];
            Bitmap bitmap = null;
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
                addBitmapToMemoryCache( this.bitmapUrl, bitmap );
            return bitmap;
        }

        @Override
        protected void onPostExecute( Bitmap bitmap )
        {
            // 根據Tag找到對應的ImageView元件，把剛剛下載的圖片呈現出來

            ImageView imageView = (ImageView) gridViewPhoto.findViewWithTag( bitmapId );
            if ( imageView != null && bitmap != null )
                imageView.setImageBitmap( bitmap );
            taskCollection.remove( this );
        }
    }

    /**
     * 下載Bitmap。
     *
     * @param bitmapUrl
     * @param width Bitmap要呈現的寬度
     * @param height Bitmap要呈現的高度
     * @return
     * @throws IOException
     */
    public static Bitmap downloadBitmap( String bitmapUrl, int width, int height ) throws IOException
    {
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
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
//     System.out.printf("%d, %d, %s, %d\n", imageHeight, imageWidth, imageType, options.inSampleSize );

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
        }
        return bitmap;
    }

    /**
     * 從手機儲存載入圖片。
     *
     * @param photoPath
     * @param width  Bitmap要呈現的寬度
     * @param height Bitmap要呈現的高度
     * @return
     */
    public static Bitmap loadBitmapFromFile(String photoPath, int width, int height )
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( photoPath, options );
        options.inSampleSize = calculateImageSampleSize( options, width, height );
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile( photoPath, options );
        return bitmap;
    }

    /**
     * 從URL建立連線後取得InputStream
     *
     * @param URL_String
     * @return
     * @throws IOException
     */
    public static InputStream getInputStreamFromURL( final String URL_String ) throws IOException
    {
        HttpURLConnection conn = null;
        URL url = new URL(URL_String);
        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10*000);
        conn.setReadTimeout(10 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        return conn.getInputStream();
    }


    /**
     * 依照要顯示的長寬來計算Bitmap要sample的比例
     *
     * @param options
     * @param reqWidth 需要的寬度
     * @param reqHeight 需要的高度
     * @return
     */
    public static int calculateImageSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight )
    {
        final int rawH = options.outHeight;
        final int rawW = options.outWidth;
        int imageSampleSize = 1;

        if ( rawH > reqHeight || rawW > reqWidth )
        {
            final int halfH = rawH / 2;
            final int halfW = rawW / 2;

            while ( ( ( halfH / imageSampleSize ) > reqHeight ) && ( ( halfW / imageSampleSize ) > reqWidth ) )
                imageSampleSize *= 2;
        }
        return imageSampleSize;
    }
    
}
