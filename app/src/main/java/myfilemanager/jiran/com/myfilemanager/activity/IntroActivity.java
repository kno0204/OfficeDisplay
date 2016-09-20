package myfilemanager.jiran.com.myfilemanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.Locale;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.database.ManagerDataSource;
import myfilemanager.jiran.com.myfilemanager.service.FileObserverService;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

/**
 * Created by user on 2016-08-09.
 */
public class IntroActivity extends Activity {

    private final int MY_PERMISSIONS_REQUEST_STORAGE = 9999;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        Settings.updatePreferences(this);

        MainActivity.path = getIntent().getStringExtra("path");

        if(Settings.getNewFilesAlram()){
            startService(new Intent(this, FileObserverService.class));
        }

        Configuration conf = getResources().getConfiguration();
        if(Settings.getLanguage() == Settings.LANGUAGE_ENGLISH){
            conf.locale = new Locale("en");
        }else if(Settings.getLanguage() == Settings.LANGUAGE_KOREA){
            conf.locale = new Locale("ko");
        }else if(Settings.getLanguage() == Settings.LANGUAGE_JAPAN){
            conf.locale = new Locale("ja");
        }else{
            conf.locale = new Locale("en");
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        Resources resources = new Resources(getAssets(), metrics, conf);
//                /* get localized string */
//        String str = resources.getString(R.string.test);
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            //Toast.makeText(SplashActivity.this,"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
            Handler han = new Handler();
            han.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    if(MainActivity.path != null && MainActivity.path.length() > 0){
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    startActivity(intent);
                    finish();
                }
            }, 1000);
            return;
        }else{
            requestStoragePermission();
        }
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_STORAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //Checking the request code of our request
        if(requestCode == MY_PERMISSIONS_REQUEST_STORAGE){
            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                //Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();

            }else{
                //Displaying another toast if permission is not granted
                //Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
            Handler han = new Handler();
            han.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                    if(MainActivity.path != null && MainActivity.path.length() > 0){
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }
}
