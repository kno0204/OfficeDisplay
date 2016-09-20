package myfilemanager.jiran.com.myfilemanager.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.IntroActivity;

/**
 * Created by user on 2016-09-06.
 */
public class FileObserverService extends Service {

    ArrayList<HDFileObserver> arrayFileObserver;
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("FileObserverService", "onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("FileObserverService", "onStart");
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FileObserverService", "onStartCommand");
        arrayFileObserver = new ArrayList<HDFileObserver>();
        HDFileObserver fileObserver = new HDFileObserver(Environment.getExternalStorageDirectory().getAbsolutePath());
        fileObserver.startWatching();
        arrayFileObserver.add(fileObserver);
        monitorAllFiles(Environment.getExternalStorageDirectory());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("FileObserverService", "onDestroy");
        if(arrayFileObserver != null && arrayFileObserver.size() > 0){
            for(HDFileObserver observer : arrayFileObserver){
                observer.stopWatching();
                observer.close();
            }
            arrayFileObserver = null;
        }
        super.onDestroy();
    }

    private void monitorAllFiles(File root) {
        File[] files = root.listFiles();
        for(File file : files) {
            if(file.isDirectory() && !file.getName().startsWith(".")) {
                HDFileObserver fileObserver = new HDFileObserver(file.getAbsolutePath());
                fileObserver.startWatching();
                arrayFileObserver.add(fileObserver);
                monitorAllFiles(file);
            }
        }
    }

    public void sendNotification(String msg, String path, String name) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        msg += name;

        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra("path", path);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent , PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setTicker(msg)
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }

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
                    sendNotification("CREATE File : ", rootPath, path);
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
}
