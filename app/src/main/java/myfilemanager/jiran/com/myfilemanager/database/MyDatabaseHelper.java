package myfilemanager.jiran.com.myfilemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FileManager.db";

    private static final int DATABASE_VERSION = 1;
    
    public final static String TAG_TABLE = "TagItem"; // name of table
    public final static String FILE_TABLE = "FileItem"; // name of table

    public final static String TAG_ID = "tag_id";
    public final static String TAG_NAME = "tag_name";
    public final static String TAG_COLOR = "tag_color";


    public final static String FILE_ID = "file_id";
    public final static String FILE_NAME = "file_name";
    public final static String FILE_PATH = "file_path";
    public final static String FILE_TAG_ID = "file_tag_id";
    public final static String FILE_TAG_COLOR = "file_tag_color";
    public final static String FILE_FAVORITE = "file_favorite";


    // Database creation sql statement
    private static final String DATABASE_CREATE_TAG_ITEM =
    		"create table " + TAG_TABLE + "(" + TAG_ID + " integer primary key autoincrement," +
                                    TAG_NAME + " text not null," +
                                    TAG_COLOR + " text not null);";

    private static final String DATABASE_INIT1_TAG_ITEM =
            "insert into " + TAG_TABLE + "(" + TAG_NAME + "," + TAG_COLOR + ") values ('업무자료','1');";
    private static final String DATABASE_INIT2_TAG_ITEM =
            "insert into " + TAG_TABLE + "(" + TAG_NAME + "," + TAG_COLOR + ") values ('개인자료','2');";
    private static final String DATABASE_INIT3_TAG_ITEM =
            "insert into " + TAG_TABLE + "(" + TAG_NAME + "," + TAG_COLOR + ") values ('공유자료','3');";

    private static final String DATABASE_CREATE_FILE_ITEM =
            "create table " + FILE_TABLE + "(" + FILE_ID + " integer primary key autoincrement," +
                    FILE_NAME + " text not null," +
                    FILE_PATH + " text not null," +
                    FILE_TAG_ID + " text not null," +
                    FILE_TAG_COLOR + " text not null," +
                    FILE_FAVORITE + " text not null);";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TAG_ITEM);
        database.execSQL(DATABASE_CREATE_FILE_ITEM);
        database.execSQL(DATABASE_INIT1_TAG_ITEM);
        database.execSQL(DATABASE_INIT2_TAG_ITEM);
        database.execSQL(DATABASE_INIT3_TAG_ITEM);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(),"Upgrading database from version " + oldVersion + " to "
                         + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + FILE_TABLE);
        onCreate(database);
    }
}