package myfilemanager.jiran.com.myfilemanager.fagment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.MainActivity;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;

/**
 * Created by user on 2016-08-03.
 */


public class MultiFragment extends Fragment {
    public Context mMainContext;

    private LinearLayout category_image_layout;
    private LinearLayout category_video_layout;
    private LinearLayout category_music_layout;
    private LinearLayout category_favorite_layout;
    private LinearLayout category_download_layout;
    private LinearLayout category_internal_storage_layout;

    private LinearLayout tag_content_layout;

    private LinearLayout bottom_tag_menu_layout;
    private ImageView tag_menu_btn;

    private Animation showAni, goneAni;

    private LinearLayout new_tag, delete_tag;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private boolean TAG_DELTE_MODE = false;

    boolean doubleBackToExitPressedOnce = false;

    public MultiFragment(){
        setRetainInstance(true);
    }

    public MultiFragment(Context context){
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

        View rootView = inflater.inflate(R.layout.frag_multi, container, false);
        initLayout(inflater, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((MainActivity)mMainContext).finishSearchMode();
        ((MainActivity)mMainContext).toolbarMenu(R.menu.search);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        getActivity().setTitle(getResources().getString(R.string.app_name));
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
        //Toast.makeText(getActivity(), "onBackPressed", Toast.LENGTH_SHORT).show();
        if(TAG_DELTE_MODE){
            displayTagList(false);
        }else if(!bottomMenu(true)) {
            if (doubleBackToExitPressedOnce) {
                getActivity().finish();
                return true;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getActivity(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);

        }
        return true;
    }

    public void initLayout(LayoutInflater inflater, View rootView) {
        //final MainActivity context = (MainActivity) getActivity();
        //Category
        category_image_layout =  (LinearLayout) rootView.findViewById(R.id.category_image_layout);
        category_video_layout =  (LinearLayout) rootView.findViewById(R.id.category_video_layout);
        category_music_layout =  (LinearLayout) rootView.findViewById(R.id.category_music_layout);
        category_favorite_layout =  (LinearLayout) rootView.findViewById(R.id.category_favorite_layout);
        category_download_layout =  (LinearLayout) rootView.findViewById(R.id.category_download_layout);
        category_internal_storage_layout =  (LinearLayout) rootView.findViewById(R.id.category_internal_storage_layout);
        category_image_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getImageDirectoryPath(), true))
                        .commit();
            }
        });category_video_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getVideoDirectoryPath(), true))
                        .commit();
            }
        });category_music_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getMusicDirectoryPath(), true))
                        .commit();
            }
        });category_favorite_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getFavoriteDirectoryPath()))
//                        .commit();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, 2, null))
                        .commit();
            }
        });category_download_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getDownloadDirectoryPath(), true))
                        .commit();
            }
        });category_internal_storage_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ForderFragment(mMainContext, Utils.getInternalDirectoryPath(), true))
                        .commit();
            }
        });

        //TAG DISPLAY
        tag_content_layout =  (LinearLayout) rootView.findViewById(R.id.tag_content_layout);
        displayTagList(false);
        //TAG DISPLAY


        bottom_tag_menu_layout =  (LinearLayout) rootView.findViewById(R.id.bottom_tag_menu_layout);
        tag_menu_btn =  (ImageView) rootView.findViewById(R.id.tag_menu_btn);
        tag_menu_btn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(TAG_DELTE_MODE){
                    displayTagList(false);
                }else {
                    bottomMenu();
                }
            }
        });

        new_tag =  (LinearLayout) rootView.findViewById(R.id.new_tag);
        new_tag.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                bottomMenu();
                dialogNewTag();
            }
        });
        delete_tag =  (LinearLayout) rootView.findViewById(R.id.delete_tag);
        delete_tag.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                bottomMenu();
                displayTagList(true);
            }
        });
    }

    public void dialogNewTag(){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_new_tag, null, false);

        final EditText edt = (EditText)view.findViewById(R.id.edt);
        final ImageView tagColor1 = (ImageView)view.findViewById(R.id.tag_color_1);
        final ImageView tagColor2 = (ImageView)view.findViewById(R.id.tag_color_2);
        final ImageView tagColor3 = (ImageView)view.findViewById(R.id.tag_color_3);
        final ImageView tagColor4 = (ImageView)view.findViewById(R.id.tag_color_4);
        final ImageView tagColor5 = (ImageView)view.findViewById(R.id.tag_color_5);
        final ImageView tagColor6 = (ImageView)view.findViewById(R.id.tag_color_6);
        final ImageView tagColor7 = (ImageView)view.findViewById(R.id.tag_color_7);
        final ImageView tagColor8 = (ImageView)view.findViewById(R.id.tag_color_8);
        final ImageView tagColor9 = (ImageView)view.findViewById(R.id.tag_color_9);
        final ImageView tagColor10 = (ImageView)view.findViewById(R.id.tag_color_10);

        edt.setTag(1);
        tagColor1.setImageResource(R.mipmap.ic_select_tag);
        tagColor1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(1);
                tagColor1.setImageResource(R.mipmap.ic_select_tag);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });
        tagColor2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(2);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(R.mipmap.ic_select_tag);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(3);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(R.mipmap.ic_select_tag);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(4);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(R.mipmap.ic_select_tag);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(5);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(R.mipmap.ic_select_tag);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(6);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(R.mipmap.ic_select_tag);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(7);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(R.mipmap.ic_select_tag);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(8);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(R.mipmap.ic_select_tag);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(9);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(R.mipmap.ic_select_tag);
                tagColor10.setImageResource(android.R.color.transparent);
            }
        });

        tagColor10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edt.setTag(10);
                tagColor1.setImageResource(android.R.color.transparent);
                tagColor2.setImageResource(android.R.color.transparent);
                tagColor3.setImageResource(android.R.color.transparent);
                tagColor4.setImageResource(android.R.color.transparent);
                tagColor5.setImageResource(android.R.color.transparent);
                tagColor6.setImageResource(android.R.color.transparent);
                tagColor7.setImageResource(android.R.color.transparent);
                tagColor8.setImageResource(android.R.color.transparent);
                tagColor9.setImageResource(android.R.color.transparent);
                tagColor10.setImageResource(R.mipmap.ic_select_tag);
            }
        });

        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        TextView create = (TextView)view.findViewById(R.id.create);
        create.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag_name = edt.getText().toString();
                if(tag_name != null && tag_name.length() > 0){
                    int select = (int)edt.getTag();
                    //Toast.makeText(mMainContext, tag_name + " => " + select, Toast.LENGTH_SHORT).show();
                    MainActivity.mMds.insertTagItem(tag_name, select + "");
                    ((MainActivity)mMainContext).displayTagList();
                    displayTagList(false);

                    if(alertDialog != null){
                        alertDialog.dismiss();
                    }
                }else{
                    final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                    Snackbar.make(viewPos, getResources().getString(R.string.snackbar_input_tagname), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void bottomMenu(){
        bottomMenu(false);
    }
    private boolean bottomMenu(boolean check){
        if(bottom_tag_menu_layout.getVisibility() == View.VISIBLE){
            goneAni = AnimationUtils.loadAnimation(mMainContext, R.anim.main_bottom_menu_slide_bottom);
            bottom_tag_menu_layout.setAnimation(goneAni);
            bottom_tag_menu_layout.setVisibility(View.GONE);
            return true;
        }else{
            if(!check) {
                showAni = AnimationUtils.loadAnimation(mMainContext, R.anim.main_bottom_menu_slide_top);
                bottom_tag_menu_layout.setVisibility(View.VISIBLE);
                bottom_tag_menu_layout.setAnimation(showAni);
            }
            return false;
        }
    }



    public void displayTagList(final boolean delete){
        TAG_DELTE_MODE = delete;
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        ArrayList<TagItem> tagItams = MainActivity.mMds.getAllTag();
        tag_content_layout.removeAllViews();
        if(tagItams != null && tagItams.size() > 0){
            int totalCount = tagItams.size();
            for(int i = 0; i < totalCount; ){
//            }
//            for(final TagItem item : tagItams){
                final TagItem item_1 = tagItams.get(i++);
                View tag_view = inflater.inflate(R.layout.frag_multi_tag_list_itme, null, false);

                LinearLayout tag_1 = (LinearLayout) tag_view.findViewById(R.id.tag_1);
                LinearLayout tag_1_color = (LinearLayout) tag_view.findViewById(R.id.tag_1_color);
                ImageView tag_1_delete = (ImageView) tag_view.findViewById(R.id.tag_1_delete);
                tag_1_color.setBackgroundResource(Utils.getTagColorResourceLarge(item_1.getColor()));
                if(delete){
                    tag_1_delete.setVisibility(View.VISIBLE);
                }else{
                    tag_1_delete.setVisibility(View.GONE);
                }
                TextView tag_1_name = (TextView) tag_view.findViewById(R.id.tag_1_name);
                tag_1_name.setText(item_1.getName());
                tag_1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(delete){
                            dialogDeleteTag(item_1);
                        }else {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, new ForderFragment(mMainContext, 1, item_1))
                                    .commit();
                        }
                    }
                });


                LinearLayout tag_2 = (LinearLayout) tag_view.findViewById(R.id.tag_2);
                if(i < totalCount){
                    tag_2.setVisibility(View.VISIBLE);
                    final TagItem item_2 = tagItams.get(i++);
                    LinearLayout tag_2_color = (LinearLayout) tag_view.findViewById(R.id.tag_2_color);
                    ImageView tag_2_delete = (ImageView) tag_view.findViewById(R.id.tag_2_delete);
                    tag_2_color.setBackgroundResource(Utils.getTagColorResourceLarge(item_2.getColor()));
                    if(delete){
                        tag_2_delete.setVisibility(View.VISIBLE);
                    }else{
                        tag_2_delete.setVisibility(View.GONE);
                    }
                    TextView tag_2_name = (TextView) tag_view.findViewById(R.id.tag_2_name);
                    tag_2_name.setText(item_2.getName());
                    tag_2.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            if(delete){
                                dialogDeleteTag(item_2);
                            }else {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, new ForderFragment(mMainContext, 1, item_2))
                                        .commit();
                            }
                        }
                    });
                }else{
                    tag_2.setVisibility(View.INVISIBLE);
                }


                LinearLayout tag_3 = (LinearLayout) tag_view.findViewById(R.id.tag_3);
                if(i < totalCount){
                    tag_3.setVisibility(View.VISIBLE);
                    final TagItem item_3 = tagItams.get(i++);
                    LinearLayout tag_3_color = (LinearLayout) tag_view.findViewById(R.id.tag_3_color);
                    ImageView tag_3_delete = (ImageView) tag_view.findViewById(R.id.tag_3_delete);
                    tag_3_color.setBackgroundResource(Utils.getTagColorResourceLarge(item_3.getColor()));
                    if(delete){
                        tag_3_delete.setVisibility(View.VISIBLE);
                    }else{
                        tag_3_delete.setVisibility(View.GONE);
                    }
                    TextView tag_3_name = (TextView) tag_view.findViewById(R.id.tag_3_name);
                    tag_3_name.setText(item_3.getName());
                    tag_3.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            if(delete){
                                dialogDeleteTag(item_3);
                            }else {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, new ForderFragment(mMainContext, 1, item_3))
                                        .commit();
                            }
                        }
                    });
                }else{
                    tag_3.setVisibility(View.INVISIBLE);
                }

                tag_content_layout.addView(tag_view);
            }
        }
    }

    public void dialogDeleteTag(final TagItem tag){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_base, null, false);
        LinearLayout dialog_tag_list_layout = (LinearLayout)view.findViewById(R.id.dialog_tag_list_layout);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.dialog_delete_tag));

        TextView content = (TextView) view.findViewById(R.id.content);
        content.setText(getResources().getString(R.string.dialog_delete_tag_info));

        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                displayTagList(false);
            }
        });
        TextView ok = (TextView)view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                MainActivity.mMds.deleteTag(tag.getId());
                ((MainActivity)mMainContext).displayTagList();
                displayTagList(false);
                final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                Snackbar.make(viewPos, getResources().getString(R.string.snackbar_delete), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }
}
