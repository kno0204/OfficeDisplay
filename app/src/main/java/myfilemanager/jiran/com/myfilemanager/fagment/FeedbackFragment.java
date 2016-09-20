package myfilemanager.jiran.com.myfilemanager.fagment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.MainActivity;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

/**
 * Created by user on 2016-08-03.
 */


public class FeedbackFragment extends Fragment {
    public Context mMainContext;

    EditText email_edt;
    EditText email_comtent_edt;

    public FeedbackFragment(){
        setRetainInstance(true);
    }

    public FeedbackFragment(Context context){
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

        View rootView = inflater.inflate(R.layout.frag_feeback, container, false);
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
        getActivity().setTitle(getResources().getString(R.string.left_menu_feedback));
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
        //Feddback
        email_edt = (EditText) rootView.findViewById(R.id.email_edt);
        final TextView email_count = (TextView) rootView.findViewById(R.id.email_count);
        email_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                email_count.setText(s.toString().length() + "");
            }
        });
        email_comtent_edt = (EditText) rootView.findViewById(R.id.email_comtent_edt);
        final TextView email_contents_count = (TextView) rootView.findViewById(R.id.email_contents_count);
        email_comtent_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                email_contents_count.setText(s.toString().length() + "");
            }
        });


        Button send_btn = (Button) rootView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Configuration conf = getResources().getConfiguration();
//                if(Settings.getLanguage() == Settings.LANGUAGE_ENGLISH){
//                    conf.locale = new Locale("en");
//                }else if(Settings.getLanguage() == Settings.LANGUAGE_KOREA){
//                    conf.locale = new Locale("ko");
//                }else if(Settings.getLanguage() == Settings.LANGUAGE_JAPAN){
//                    conf.locale = new Locale("ja");
//                }else{
//                    conf.locale = new Locale("en");
//                }
//                DisplayMetrics metrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                Resources resources = new Resources(getActivity().getAssets(), metrics, conf);
                /* get localized string */
//                String str = getResources().getString(R.string.left_menu_setting);
//                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

                String email = email_edt.getText().toString();
                String email_comtent = email_comtent_edt.getText().toString();

                if(email == null || email.length() == 0){
                    Toast.makeText(getActivity(), "email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email != null && email.length() > 0 && !Utils.isValidEmail(email)){
                    Toast.makeText(getActivity(), "email 형식이 옳바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email_comtent == null || email_comtent.length() == 0){
                    Toast.makeText(getActivity(), "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"youremail@yahoo.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, email);
                intent.putExtra(Intent.EXTRA_TEXT, email_comtent);
                intent.setType("text/email");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }
}
