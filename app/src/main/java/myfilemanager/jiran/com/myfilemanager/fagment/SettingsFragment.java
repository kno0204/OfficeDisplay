package myfilemanager.jiran.com.myfilemanager.fagment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.MainActivity;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;
import myfilemanager.jiran.com.myfilemanager.service.FileObserverService;
import myfilemanager.jiran.com.myfilemanager.service.HDFileObserver;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

/**
 * Created by user on 2016-08-03.
 */


public class SettingsFragment extends Fragment {
    public Context mMainContext;

    LinearLayout new_file_layout;
    LinearLayout memory_space_layout;
    LinearLayout hide_layout;
    LinearLayout language_layout;

    CheckBox new_file_checkbox;
    CheckBox memory_space_checkbox;
    CheckBox hide_checkbox;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    public SettingsFragment(){
        setRetainInstance(true);
    }

    public SettingsFragment(Context context){
        mMainContext = context;
        setRetainInstance(true);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_settings, container, false);
        initLayout(inflater, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((MainActivity)mMainContext).finishSearchMode();
        ((MainActivity)mMainContext).toolbarMenu(R.menu.none);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        getActivity().setTitle(getResources().getString(R.string.left_menu_setting));
        //Toast.makeText(getActivity(), mPath, Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onBackPressed() {
        //getActivity().finish();
        ((MainActivity)mMainContext).initFragment();
        return false;
    }

    public void initLayout(LayoutInflater inflater, View rootView) {
        //final MainActivity context = (MainActivity) getActivity();
        //Setting
        new_file_layout =  (LinearLayout) rootView.findViewById(R.id.new_file_layout);
        new_file_checkbox = (CheckBox) rootView.findViewById(R.id.new_file_checkbox);
        if(Settings.getNewFilesAlram()){
            new_file_layout.setTag(true);
            new_file_checkbox.setChecked(true);
        }else{
            new_file_layout.setTag(false);
            new_file_checkbox.setChecked(false);
        }
        new_file_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((boolean)v.getTag()){
                    v.setTag(false);
                    new_file_checkbox.setChecked(false);
                    Settings.setNewFilesAlram(false);
                    mMainContext.stopService(new Intent(mMainContext, FileObserverService.class));
                }else {
                    v.setTag(true);
                    new_file_checkbox.setChecked(true);
                    Settings.setNewFilesAlram(true);
                    mMainContext.startService(new Intent(mMainContext, FileObserverService.class));

                }
            }
        });
        memory_space_layout =  (LinearLayout) rootView.findViewById(R.id.memory_space_layout);
        memory_space_checkbox =  (CheckBox) rootView.findViewById(R.id.memory_space_checkbox);
        if(Settings.getMemoryAlram()){
            memory_space_layout.setTag(true);
            memory_space_checkbox.setChecked(true);
        }else{
            memory_space_layout.setTag(false);
            memory_space_checkbox.setChecked(false);
        }
        memory_space_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((boolean)v.getTag()){
                    v.setTag(false);
                    memory_space_checkbox.setChecked(false);
                    Settings.setMemoryAlram(false);
                }else {
                    v.setTag(true);
                    memory_space_checkbox.setChecked(true);
                    Settings.setMemoryAlram(true);
                }
            }
        });
        hide_layout =  (LinearLayout) rootView.findViewById(R.id.hide_layout);
        hide_checkbox =  (CheckBox) rootView.findViewById(R.id.hide_checkbox);
        if(Settings.getShowHiddenFiles()){
            hide_layout.setTag(true);
            hide_checkbox.setChecked(true);
        }else{
            hide_layout.setTag(false);
            hide_checkbox.setChecked(false);
        }
        hide_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if((boolean)v.getTag()){
                    v.setTag(false);
                    hide_checkbox.setChecked(false);
                    Settings.setShowHiddenFiles(false);
                }else {
                    v.setTag(true);
                    hide_checkbox.setChecked(true);
                    Settings.setShowHiddenFiles(true);
                }
            }
        });
        language_layout =  (LinearLayout) rootView.findViewById(R.id.language_layout);
        language_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetLanguage();
            }
        });
    }

    public void dialogSetLanguage(){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_select_language, null, false);
        LinearLayout korean = (LinearLayout)view.findViewById(R.id.korean);
        TextView korean_txt = (TextView)view.findViewById(R.id.korean_txt);
        if(Settings.getLanguage() != Settings.LANGUAGE_KOREA){
            korean_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        korean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setLanguage(Settings.LANGUAGE_KOREA);
                applicationRestart();
            }
        });
        LinearLayout english = (LinearLayout)view.findViewById(R.id.english);
        TextView english_txt = (TextView)view.findViewById(R.id.english_txt);
        if(Settings.getLanguage() != Settings.LANGUAGE_ENGLISH){
            english_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        english.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setLanguage(Settings.LANGUAGE_ENGLISH);
                applicationRestart();
            }
        });
        LinearLayout japan = (LinearLayout)view.findViewById(R.id.japan);
        TextView japan_txt = (TextView)view.findViewById(R.id.japan_txt);
        if(Settings.getLanguage() != Settings.LANGUAGE_JAPAN){
            japan_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        japan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setLanguage(Settings.LANGUAGE_JAPAN);
                applicationRestart();
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }


    public void applicationRestart() {
        Intent intent = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
