package myfilemanager.jiran.com.myfilemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;

import myfilemanager.jiran.com.myfilemanager.R;

/**
 * Created by user on 2016-09-06.
 */
public class HDFileObserver extends FileObserver {
    static final String TAG="HDFileObserver";
    /**
     * should be end with File.separator
     */
    String rootPath;
    static final int mask = (FileObserver.CREATE |
            FileObserver.DELETE |
            FileObserver.DELETE_SELF |
            FileObserver.MODIFY |
            FileObserver.MOVED_FROM |
            FileObserver.MOVED_TO |
            FileObserver.MOVE_SELF);

    public HDFileObserver(String root){
        super(root, mask);

        if (! root.endsWith(File.separator)){
            root += File.separator;
        }
        rootPath = root;
    }

    public void onEvent(int event, String path) {

        switch(event){
            case FileObserver.CREATE:
                Log.d(TAG, "CREATE:" + rootPath + path);
                break;
            case FileObserver.DELETE:
                Log.d(TAG, "DELETE:" + rootPath + path);
                break;
            case FileObserver.DELETE_SELF:
                Log.d(TAG, "DELETE_SELF:" + rootPath + path);
                break;
            case FileObserver.MODIFY:
                Log.d(TAG, "MODIFY:" + rootPath + path);
                break;
            case FileObserver.MOVED_FROM:
                Log.d(TAG, "MOVED_FROM:" + rootPath + path);
                break;
            case FileObserver.MOVED_TO:
                Log.d(TAG, "MOVED_TO:" + path);
                break;
            case FileObserver.MOVE_SELF:
                Log.d(TAG, "MOVE_SELF:" + path);
                break;
            default:
                // just ignore
                break;
        }
    }

    public void close(){
        super.finalize();
    }
}