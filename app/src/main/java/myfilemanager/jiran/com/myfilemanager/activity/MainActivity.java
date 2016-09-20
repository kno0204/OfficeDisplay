package myfilemanager.jiran.com.myfilemanager.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.adapter.FolderAdapter;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;
import myfilemanager.jiran.com.myfilemanager.database.ManagerDataSource;
import myfilemanager.jiran.com.myfilemanager.fagment.FeedbackFragment;
import myfilemanager.jiran.com.myfilemanager.fagment.ForderFragment;
import myfilemanager.jiran.com.myfilemanager.fagment.MultiFragment;
import myfilemanager.jiran.com.myfilemanager.fagment.SettingsFragment;
import myfilemanager.jiran.com.myfilemanager.preview.IconPreview;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    public Toolbar toolbar;
    FrameLayout mainFrame;
    FrameLayout search_layout;
    EditText search_edit;
    Button search_edit_clear_btn;
    LinearLayout sd_card_memory_layout;
    DonutProgress sd_card_memory;
    LinearLayout sd_card;
    TextView sdcard_total_size;
    TextView sdcard_use_size;

    LinearLayout tag_content_layout;

    public static boolean mSearchMode = false;

    private android.app.FragmentManager fm;
    public static ManagerDataSource mMds;

    public static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMds = new ManagerDataSource(this);

        initLayout();
        initFunction();

        if(MainActivity.path != null && MainActivity.path.length() > 0){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new ForderFragment(MainActivity.this, MainActivity.path, true))
                    .commit();
            MainActivity.path = null;
        }else {
            initFragment();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MainActivity.path != null && MainActivity.path.length() > 0){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new ForderFragment(MainActivity.this, MainActivity.path, true))
                    .commit();
            MainActivity.path = null;
        }

        IntentFilter ejectFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        ejectFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        ejectFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        ejectFilter.addDataScheme("file");
        registerReceiver(ejectReceiver, ejectFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ejectReceiver);
    }

    //    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onKeyDown(int keycode, @NonNull KeyEvent event) {
        if (keycode != KeyEvent.KEYCODE_BACK)
            return false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return requestCurrentFragmentBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            setSearchMode();
            return true;
        }else if (id == R.id.action_view_change) {
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment) {
                ((ForderFragment) fm).changeListGridView();
            }
            return true;
        }else if (id == R.id.action_test1) {
            //Toast.makeText(this, "숨김", Toast.LENGTH_SHORT).show();
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment){
                ((ForderFragment)fm).hideMethod();
            }
            return true;
        }else if (id == R.id.action_test2) {
            //Toast.makeText(this, "복사", Toast.LENGTH_SHORT).show();
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment){
                ((ForderFragment)fm).copyMethod();
            }
            return true;
        }else if (id == R.id.action_test3) {
            //Toast.makeText(this, "즐겨찾기", Toast.LENGTH_SHORT).show();
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment){
                ((ForderFragment)fm).favoriteMethod();
            }
            return true;
        }else if (id == R.id.action_test4) {
            //Toast.makeText(this, "태그", Toast.LENGTH_SHORT).show();
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment){
                ((ForderFragment)fm).dialogSelectTag();
            }
            return true;
        }else if (id == R.id.action_test5) {
            //Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show();
            Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if(fm instanceof ForderFragment){
                ((ForderFragment)fm).deleteMethod();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //public Method
    public void initLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        search_layout = (FrameLayout) findViewById(R.id.search_layout);
        search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit_clear_btn = (Button) findViewById(R.id.search_edit_clear_btn);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mainFrame = (FrameLayout)findViewById(R.id.content_frame);

        fm = getFragmentManager();
    }


    public void initFunction() {
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addView(setCustomNavigationView(R.layout.custom_navigation_view));
        new IconPreview(this);

        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    String search = search_edit.getText().toString();
                    if(search != null && search.length() > 0){
                        Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                        String path = Utils.getInternalDirectoryPath();
                        if(fm instanceof ForderFragment){
                            if(((ForderFragment)fm).mListType != 1 && ((ForderFragment)fm).mListType != 2)
                            path = ((ForderFragment)fm).mCurrentPath;
                        }
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search_edit.getWindowToken(), 0);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, new ForderFragment(MainActivity.this, path, search))
                                .commit();
                    }else{
                    }
                    return true;
                }
                return false;
            }
        });

        search_edit_clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setText("");
            }
        });

    }

    public void initFragment() {
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, new ForderFragment(this, Environment.getExternalStorageDirectory().getAbsolutePath()))
//                .commit();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MultiFragment(this))
                .commit();
    }


    public View setCustomNavigationView(int resourceId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(resourceId, null, false);

        DonutProgress external_storage_memory = (DonutProgress) view.findViewById(R.id.internal_storage_memory);
        TextView external_total_size = (TextView) view.findViewById(R.id.external_total_size);
        TextView external_use_size = (TextView) view.findViewById(R.id.external_use_size);
        long total = Utils.getTotalExternalMemorySize();
        long free = Utils.getExternalMemorySize();
        external_total_size.setText(Utils.formatSize(total));
        external_use_size.setText(Utils.formatSize(total - free));
        int external_size = (int)((free * 100) / total);
        external_storage_memory.setProgress(external_size);

        sd_card_memory_layout = (LinearLayout) view.findViewById(R.id.sd_card_memory_layout);
        sd_card_memory = (DonutProgress) view.findViewById(R.id.sd_card_memory);
        sdcard_total_size = (TextView) view.findViewById(R.id.sdcard_total_size);
        sdcard_use_size = (TextView) view.findViewById(R.id.sdcard_use_size);
        if(Utils.isSDcardDirectory(true)){
            sd_card_memory_layout.setVisibility(View.VISIBLE);
            long sdTotal = Utils.getTotalSDCaredMemorySize();
            long sdFree = Utils.getSDCardMemorySize();
            sdcard_total_size.setText(Utils.formatSize(sdTotal));
            sdcard_use_size.setText(Utils.formatSize(sdTotal - sdFree));
            int sdcard_size = (int)((sdFree * 100) / sdTotal);
            sd_card_memory.setProgress(sdcard_size);
        }else {
            sd_card_memory_layout.setVisibility(View.GONE);
        }

        LinearLayout category = (LinearLayout) view.findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new MultiFragment(MainActivity.this))
                            .commit();
                }
            }
        });

        LinearLayout storage = (LinearLayout) view.findViewById(R.id.storage);
        storage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new ForderFragment(MainActivity.this, Utils.getInternalDirectoryPath(), false))
                            .commit();
                }
            }
        });

        sd_card = (LinearLayout) view.findViewById(R.id.sd_card);
        if(Utils.isSDcardDirectory(true)){
            sd_card.setVisibility(View.VISIBLE);
            sd_card.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, new ForderFragment(MainActivity.this, Utils.getSDcardDirectoryPath(), false))
                                .commit();
                    }
                }
            });
        }else{
            sd_card.setVisibility(View.GONE);
        }

        tag_content_layout = (LinearLayout) view.findViewById(R.id.tag_content_layout);
        displayTagList();

        LinearLayout tag_layout = (LinearLayout) view.findViewById(R.id.tag_layout);
        final ImageView tag_arrow = (ImageView) view.findViewById(R.id.tag_arrow);
        tag_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(tag_content_layout.getVisibility() == View.VISIBLE){
                    tag_content_layout.setVisibility(View.GONE);
                    tag_arrow.setImageResource(R.mipmap.ic_arrow_down);
                }else{
                    tag_content_layout.setVisibility(View.VISIBLE);
                    tag_arrow.setImageResource(R.mipmap.ic_arrow_up);
                }

            }
        });

        LinearLayout setting_layout = (LinearLayout) view.findViewById(R.id.setting_layout);
        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new SettingsFragment(MainActivity.this))
                            .commit();
                }
            }
        });
        LinearLayout feedback_layout = (LinearLayout) view.findViewById(R.id.feedback_layout);
        feedback_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new FeedbackFragment(MainActivity.this))
                            .commit();
                }
            }
        });
        return view;
    }

    public void displayTagList(){
        LayoutInflater inflater = LayoutInflater.from(this);
        ArrayList<TagItem> tagItams = mMds.getAllTag();
        tag_content_layout.removeAllViews();
        if(tagItams != null && tagItams.size() > 0){
            for(final TagItem item : tagItams){
                View tag_view = inflater.inflate(R.layout.custom_navigation_tagitem_layout, null, false);

                LinearLayout tag_layout = (LinearLayout) tag_view.findViewById(R.id.tag_layout);
                ImageView tag_color = (ImageView) tag_view.findViewById(R.id.tag_color);
                tag_color.setImageResource(Utils.getTagColorResourceSmall(item.getColor()));
                TextView tag_name = (TextView) tag_view.findViewById(R.id.tag_name);
                tag_name.setText(item.getName());
                tag_layout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, new ForderFragment(MainActivity.this, 1, item))
                                    .commit();
                        }
                    }
                });
                tag_content_layout.addView(tag_view);
            }
        }
    }

    public boolean requestCurrentFragmentBackPressed() {
        if(mSearchMode){
            finishSearchMode();
            return false;
        }

        Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if(fm instanceof MultiFragment){
            return ((MultiFragment)fm).onBackPressed();
        } else if(fm instanceof SettingsFragment){
            return ((SettingsFragment)fm).onBackPressed();
        } else if(fm instanceof FeedbackFragment){
            return ((FeedbackFragment)fm).onBackPressed();
        }else {
            return ((ForderFragment) fm).onBackPressed();
        }
//        else {
//            return ((FolderFragment)fm).onBackPressed();
//        }
    }

    public void toolbarMenu(int res) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(res);
    }

    public void setSearchMode() {
        mSearchMode = true;
        search_layout.setVisibility(View.VISIBLE);
        search_edit.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_edit, InputMethodManager.SHOW_IMPLICIT);
    }

    public void finishSearchMode() {
        mSearchMode = false;
        search_edit.setText("");
        search_layout.setVisibility(View.GONE);
    }

    public void pasteFinish() {
        Fragment fm = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fm instanceof ForderFragment){
            ((ForderFragment)fm).listRefresh();
        }
    }

    BroadcastReceiver ejectReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                Log.i("msg","SDCard Mount Change   action => " + action);
                if(Utils.isSDcardDirectory(true)){
                    sd_card_memory_layout.setVisibility(View.VISIBLE);
                    sd_card.setVisibility(View.VISIBLE);
                    sd_card_memory_layout.setVisibility(View.VISIBLE);
                    long sdTotal = Utils.getTotalSDCaredMemorySize();
                    long sdFree = Utils.getSDCardMemorySize();
                    sdcard_total_size.setText(Utils.formatSize(sdTotal));
                    sdcard_use_size.setText(Utils.formatSize(sdTotal - sdFree));
                    int sdcard_size = (int)((sdFree * 100) / sdTotal);
                    sd_card_memory.setProgress(sdcard_size);
                }else {
                    sd_card_memory_layout.setVisibility(View.GONE);
                    sd_card.setVisibility(View.GONE);
                }
            }
        }
    };
}

