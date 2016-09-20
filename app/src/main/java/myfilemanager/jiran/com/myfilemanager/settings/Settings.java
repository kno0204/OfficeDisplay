package myfilemanager.jiran.com.myfilemanager.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.stericson.RootTools.RootTools;

public final class Settings {

    private Settings() {}

    private static SharedPreferences mPrefs;

    public static final int LANGUAGE_ENGLISH = 0;
    public static final int LANGUAGE_KOREA = 1;
    public static final int LANGUAGE_JAPAN = 2;

    //private static int mTheme;

    public static void updatePreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        //mTheme = Integer.parseInt(mPrefs.getString("preference_theme", Integer.toString(R.style.ThemeLight)));

        rootAccess();
    }

    public static boolean showThumbnail() {
        return mPrefs.getBoolean("showpreview", true);
    }

    public static boolean getNewFilesAlram() {
        return mPrefs.getBoolean("newfilealram", false);
    }

    public static void setNewFilesAlram(boolean alram) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("newfilealram", alram);
        edit.commit();
    }

    public static boolean getMemoryAlram() {
        return mPrefs.getBoolean("memoryalram", false);
    }

    public static void setMemoryAlram(boolean alram) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("memoryalram", alram);
        edit.commit();
    }

    public static boolean getShowHiddenFiles() {
        return mPrefs.getBoolean("displayhiddenfiles", false);
    }

    public static void setShowHiddenFiles(boolean hide) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("displayhiddenfiles", hide);
        edit.commit();
    }

    public static int getSortType() {
        return Integer.parseInt(mPrefs.getString("sort", "0"));
    }

    public static void setSortType(int sort_type) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("sort", sort_type + "");
        edit.commit();
    }

    public static int getLanguage() {
        return Integer.parseInt(mPrefs.getString("language", "0"));
    }

    public static void setLanguage(int language) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString("language", language + "");
        edit.commit();
    }

    public static boolean rootAccess() {
        return mPrefs.getBoolean("enablerootaccess", true) && RootTools.isAccessGiven();
        //return true;
    }

//    public static boolean reverseListView() {
//        return mPrefs.getBoolean("reverseList", false);
//    }
//
//    public static String getDefaultDir() {
//        return mPrefs.getString("defaultdir", Environment.getExternalStorageDirectory().getPath());
//    }
//
//    public static int getListAppearance() {
//        return Integer.parseInt(mPrefs.getString("viewmode", "1"));
//    }
}
