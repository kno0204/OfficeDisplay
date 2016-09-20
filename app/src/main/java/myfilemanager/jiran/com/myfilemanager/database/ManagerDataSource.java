package myfilemanager.jiran.com.myfilemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import myfilemanager.jiran.com.myfilemanager.data.FileItem;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;

public class ManagerDataSource {

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public String stringArray[];
    
    
//    public String itemName;
//	public String itemNumber;
//	public String itemGroupName;
//	
//	public String itemStartDate; 
//	public String itemEndDate;
//
//	public String itemWeek; 
//
//	public boolean itemCall; 
//	public boolean itemMessage;
//	public boolean itemPush;
//	
//	public String itemSendMessage; 
    
//    
//    public final static String FLAG = "flag"; 
//    public final static String LOCATION = "location";
    /**
     * 
     * @param context
     */
    public ManagerDataSource(Context context) {
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insertTagItem(String name, String color) {
        ContentValues values = new ContentValues();
        //values.put(MyDatabaseHelper.SKEEPER_ID, id);
        values.put(MyDatabaseHelper.TAG_NAME, name);
        values.put(MyDatabaseHelper.TAG_COLOR, color);
        return database.insert(MyDatabaseHelper.TAG_TABLE, null, values);
    }

    public long insertFileItem(String name, String path, String tagId, String tagColor, String favorite) {
        ContentValues values = new ContentValues();
        //values.put(MyDatabaseHelper.SKEEPER_ID, id);
        values.put(MyDatabaseHelper.FILE_NAME, name);
        values.put(MyDatabaseHelper.FILE_PATH, path);
        values.put(MyDatabaseHelper.FILE_TAG_ID, tagId);
        values.put(MyDatabaseHelper.FILE_TAG_COLOR, tagColor);
        values.put(MyDatabaseHelper.FILE_FAVORITE, favorite);

        return database.insert(MyDatabaseHelper.FILE_TABLE, null, values);
    }

    public ArrayList<TagItem> getAllTag() {
        String[] cols = new String[] { MyDatabaseHelper.TAG_ID, MyDatabaseHelper.TAG_NAME, MyDatabaseHelper.TAG_COLOR};
        Cursor mCursor = database.query(true, MyDatabaseHelper.TAG_TABLE, cols, null, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        ArrayList<TagItem> tagItems = new ArrayList<TagItem>();

        if (mCursor != null) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                TagItem item = new TagItem();
                item.setId(mCursor.getString(0));
                item.setName(mCursor.getString(1));
                item.setColor(mCursor.getString(2));
                tagItems.add(item);
                mCursor.moveToNext();
            }
        }
        mCursor.close();
        return tagItems; // iterate to get each value.
    }

    public Cursor getAllFile() {
        String[] cols = new String[] { MyDatabaseHelper.FILE_ID, MyDatabaseHelper.FILE_NAME, MyDatabaseHelper.FILE_PATH,
                MyDatabaseHelper.FILE_TAG_ID, MyDatabaseHelper.FILE_TAG_COLOR, MyDatabaseHelper.FILE_FAVORITE};
        Cursor mCursor = database.query(true, MyDatabaseHelper.FILE_TABLE, cols, null, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }


    public ArrayList<FileItem> selectTagFiles(String id) {
        String[] cols = new String[] { MyDatabaseHelper.FILE_ID, MyDatabaseHelper.FILE_NAME, MyDatabaseHelper.FILE_PATH,
                MyDatabaseHelper.FILE_TAG_ID, MyDatabaseHelper.FILE_TAG_COLOR, MyDatabaseHelper.FILE_FAVORITE};
        Cursor mCursor = database.query(true, MyDatabaseHelper.FILE_TABLE, cols, MyDatabaseHelper.FILE_TAG_ID+"=?", new String[]{id}, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        ArrayList<FileItem> tagList = new ArrayList<FileItem>();

        if (mCursor != null) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                FileItem item = new FileItem();
                item.setId(mCursor.getString(0));
                item.setName(mCursor.getString(1));
                item.setPath(mCursor.getString(2));
                item.setTagId(mCursor.getString(3));
                item.setTagColor(mCursor.getString(4));
                item.setFavorite(mCursor.getString(5));

                tagList.add(item);
                mCursor.moveToNext();
            }
        }
        mCursor.close();
        return tagList; // iterate to get each value.
    }

    public ArrayList<FileItem> selectFavoriteFiles() {
        String[] cols = new String[] { MyDatabaseHelper.FILE_ID, MyDatabaseHelper.FILE_NAME, MyDatabaseHelper.FILE_PATH,
                MyDatabaseHelper.FILE_TAG_ID, MyDatabaseHelper.FILE_TAG_COLOR, MyDatabaseHelper.FILE_FAVORITE};
        Cursor mCursor = database.query(true, MyDatabaseHelper.FILE_TABLE, cols, MyDatabaseHelper.FILE_FAVORITE+"=?", new String[]{"Y"}, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        ArrayList<FileItem> favoriteList = new ArrayList<FileItem>();

        if (mCursor != null) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                FileItem item = new FileItem();
                item.setId(mCursor.getString(0));
                item.setName(mCursor.getString(1));
                item.setPath(mCursor.getString(2));
                item.setTagId(mCursor.getString(3));
                item.setTagColor(mCursor.getString(4));
                item.setFavorite(mCursor.getString(5));

                favoriteList.add(item);
                mCursor.moveToNext();
            }
        }
        mCursor.close();
        return favoriteList; // iterate to get each value.
    }

    public FileItem checkFile(String path) {
        String[] cols = new String[] { MyDatabaseHelper.FILE_ID, MyDatabaseHelper.FILE_NAME, MyDatabaseHelper.FILE_PATH,
                MyDatabaseHelper.FILE_TAG_ID, MyDatabaseHelper.FILE_TAG_COLOR, MyDatabaseHelper.FILE_FAVORITE};
        Cursor mCursor = database.query(true, MyDatabaseHelper.FILE_TABLE, cols, MyDatabaseHelper.FILE_PATH+"=?", new String[]{path}, null, null, null, null);

        FileItem item = null;
        if (mCursor != null) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                item = new FileItem();
                item.setId(mCursor.getString(0));
                item.setName(mCursor.getString(1));
                item.setPath(mCursor.getString(2));
                item.setTagId(mCursor.getString(3));
                item.setTagColor(mCursor.getString(4));
                item.setFavorite(mCursor.getString(5));
                mCursor.moveToNext();
            }
        }
        mCursor.close();
        return item; // iterate to get each value.
    }

    public void deleteTag(String id) {
        System.out.println("Comment deleted with id: " + id);
        database.delete(MyDatabaseHelper.TAG_TABLE, MyDatabaseHelper.TAG_ID+"=?", new String[]{id});
    }

    public void deleteFile(String id) {
        System.out.println("Comment deleted with id: " + id);
        database.delete(MyDatabaseHelper.FILE_TABLE, MyDatabaseHelper.FILE_ID+"=?", new String[]{id});
    }

    public int UpdateTagItem(String id, String name, String color){
        ContentValues values=new ContentValues();
        values.put(MyDatabaseHelper.TAG_NAME, name);
        values.put(MyDatabaseHelper.TAG_COLOR, color);
        return database.update(MyDatabaseHelper.TAG_TABLE, values, MyDatabaseHelper.TAG_ID+"=?", new String[]{id});
    }

    public int UpdateFileFavorite(String id, String favorite){
        ContentValues values=new ContentValues();
//        values.put(MyDatabaseHelper.FILE_NAME, name);
//        values.put(MyDatabaseHelper.FILE_PATH, path);
//        values.put(MyDatabaseHelper.FILE_TAG, tag);
//        values.put(MyDatabaseHelper.FILE_TAG_COLOR, tagColor);
        values.put(MyDatabaseHelper.FILE_FAVORITE, favorite);
        return database.update(MyDatabaseHelper.FILE_TABLE, values, MyDatabaseHelper.FILE_ID+"=?", new String[]{id});
    }

    public int UpdateFileTag(String id, String tagId, String tagColor){
        ContentValues values=new ContentValues();
        values.put(MyDatabaseHelper.FILE_TAG_ID, tagId);
        values.put(MyDatabaseHelper.FILE_TAG_COLOR, tagColor);
        return database.update(MyDatabaseHelper.FILE_TABLE, values, MyDatabaseHelper.FILE_ID+"=?", new String[]{id});
    }


//    public Cursor selectAllSilent(String week) {
//        Cursor mCursor = database.rawQuery("SELECT " + MyDatabaseHelper.SKEEPER_ID + "," +
//                MyDatabaseHelper.SKEEPER_NAME + "," +
//                MyDatabaseHelper.SKEEPER_GROUPNAME + "," +
//                MyDatabaseHelper.SKEEPER_NUMBER + "," +
//                MyDatabaseHelper.SKEEPER_START_DATE + "," +
//                MyDatabaseHelper.SKEEPER_END_DATE + "," +
//                MyDatabaseHelper.SKEEPER_SAVE_WEEK + "," +
//                MyDatabaseHelper.SKEEPER_WEEK + "," +
//                MyDatabaseHelper.SKEEPER_CALL + "," +
//                MyDatabaseHelper.SKEEPER_MESSAGE + "," +
//                MyDatabaseHelper.SKEEPER_PUSH + "," +
//                MyDatabaseHelper.SKEEPER_SEND_MESSAGE + "," +
//                MyDatabaseHelper.SKEEPER_SKEEP +
//                " FROM " + MyDatabaseHelper.SKEEPER_TABLE +
//                " WHERE (" + MyDatabaseHelper.SKEEPER_SKEEP + " LIKE '4') " +
//                " AND (" + MyDatabaseHelper.SKEEPER_WEEK + " LIKE '%" + week +"%')", null);
////        if (mCursor != null) {
////            mCursor.moveToFirst();
////        }
//        return mCursor; // iterate to get each value.
//    }



//
//    public int UpdateSkeeperItem(String id, String name,String groupName, String number, String startDate, String endDate, String saveWeek, String week, String call, String message, String push, String sendMessage, String skeep){
//       ContentValues values=new ContentValues();
//       values.put(MyDatabaseHelper.SKEEPER_NAME, name);
//       values.put(MyDatabaseHelper.SKEEPER_GROUPNAME, groupName);
//       values.put(MyDatabaseHelper.SKEEPER_NUMBER, number);
//       values.put(MyDatabaseHelper.SKEEPER_START_DATE, startDate);
//       values.put(MyDatabaseHelper.SKEEPER_END_DATE, endDate);
//       values.put(MyDatabaseHelper.SKEEPER_SAVE_WEEK, saveWeek);
//       values.put(MyDatabaseHelper.SKEEPER_WEEK, week);
//       values.put(MyDatabaseHelper.SKEEPER_CALL, call);
//       values.put(MyDatabaseHelper.SKEEPER_MESSAGE, message);
//       values.put(MyDatabaseHelper.SKEEPER_PUSH, push);
//       values.put(MyDatabaseHelper.SKEEPER_SEND_MESSAGE, sendMessage);
//       values.put(MyDatabaseHelper.SKEEPER_SKEEP, skeep);
//       return database.update(MyDatabaseHelper.SKEEPER_TABLE, values, MyDatabaseHelper.SKEEPER_ID+"=?", new String[]{id});
//      }
//
//    public int UpdateSkeeperSkeep(String id, String skeep){
//        ContentValues values=new ContentValues();
//        values.put(MyDatabaseHelper.SKEEPER_SKEEP, skeep);
//        return database.update(MyDatabaseHelper.SKEEPER_TABLE, values, MyDatabaseHelper.SKEEPER_ID+"=?", new String[]{id});
//    }
//
//    public int UpdateSkeeperWeek(String id, String week){
//        ContentValues values=new ContentValues();
//        values.put(MyDatabaseHelper.SKEEPER_WEEK, week);
//        return database.update(MyDatabaseHelper.SKEEPER_TABLE, values, MyDatabaseHelper.SKEEPER_ID+"=?", new String[]{id});
//    }
//
//    public void deleteSkeeper(String id) {
//        System.out.println("Comment deleted with id: " + id);
//        database.delete(MyDatabaseHelper.SKEEPER_TABLE, MyDatabaseHelper.SKEEPER_ID+"=?", new String[]{id});
//    }

}