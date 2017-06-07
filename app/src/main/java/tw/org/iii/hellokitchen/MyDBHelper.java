package tw.org.iii.hellokitchen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kevin on 2017/5/28.
 */

public class MyDBHelper extends SQLiteOpenHelper
{
    // 資料庫名稱
    private final static String _DBName = "foods.db";
    // 資料庫版本，要更新資料庫時需增加版本號
    private final static int _DBVersion = 1;
    //Singleton
    private  static MyDBHelper instance ;
    //存取登入者
    private  static String login_user;

    //建立Table-tmember
    private final static String sqltmember = " CREATE TABLE tmember " +
            "(member_id VARCHAR(100) NOT NULL ," +"member_name VARCHAR(50) NOT NULL,"
            + "member_email VARCHAR(100) NOT NULL," + "member_password VARCHAR(50) NOT NULL,"
            + "member_tel VARCHAR(15) NULL DEFAULT NULL," + "member_fb_id VARCHAR(128) NULL DEFAULT NULL ,"
            +" PRIMARY KEY (member_id));";
    //建立Table-tingredients
    private final static  String sqlingredients = " CREATE TABLE tingredients " +
            "(ingredients_id VARCHAR(100) NOT NULL ," +"ingredients_name VARCHAR(50) NOT NULL,"
            + "ingredients_buyDate VARCHAR(100) NOT NULL," + "ingredients_deadDate VARCHAR(50) NOT NULL,"+"ingredients_amount VARCHAR(50) NOT NULL,"
            + "member_id VARCHAR(50) NULL DEFAULT NULL,"   +" PRIMARY KEY (ingredients_id));";

    public static  MyDBHelper getInstance(Context ctx)
    {
        if(instance==null)
        {
            instance = new MyDBHelper(ctx.getApplicationContext());
        }
        return instance;
    }
    private MyDBHelper(Context context)
    {
        super(context, _DBName, null, _DBVersion);
        //context.deleteDatabase(_DBName);//刪除整個資料庫用
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(sqltmember);
        db.execSQL(sqlingredients);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //還在研究

    }

    public static String getLogin_user()
    {
        return login_user;
    }

    public void setLogin_user(String login_user)
    {
         MyDBHelper.login_user = login_user;
    }
}
