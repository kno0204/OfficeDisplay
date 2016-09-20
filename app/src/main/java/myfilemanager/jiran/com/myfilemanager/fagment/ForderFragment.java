package myfilemanager.jiran.com.myfilemanager.fagment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import com.melnykov.fab.FloatingActionButton;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Handler;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.activity.MainActivity;
import myfilemanager.jiran.com.myfilemanager.adapter.FolderAdapter;
import myfilemanager.jiran.com.myfilemanager.common.ClipBoard;
import myfilemanager.jiran.com.myfilemanager.common.PasteTaskExecutor;
import myfilemanager.jiran.com.myfilemanager.common.SortUtils;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.data.FileItem;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;
import myfilemanager.jiran.com.myfilemanager.database.ManagerDataSource;
import myfilemanager.jiran.com.myfilemanager.database.MyDatabaseHelper;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

/**
 * Created by user on 2016-08-03.
 */


public class ForderFragment extends Fragment{

    public Context mMainContext;

    public int mListType = 0;

    public String mCurrentPath;
    public String mStartPath;
    public TagItem mTagItem;
    public String mSearch;

    private AbsListView mListView;
    private GridView mGridView;
    private FloatingActionButton mFab;
    private LinearLayout mFoler_path_layout;
    //private TextView mFoler_path;
    private HorizontalScrollView foler_path_scroll_layout;
    private static int mView_Type = 0;

    public static boolean mActionMode = false;

    private ArrayList<String> mDataSource;

    private FolderAdapter mFolderAdapter;

    private ImageView list_bottom_menu_paste;
    private ImageView list_bottom_menu_sort;
    private ImageView list_bottom_menu_check;

    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public ForderFragment(){
        setRetainInstance(true);
    }

    public ForderFragment(Context context, String path, boolean startBack){
        mMainContext = context;
        mCurrentPath = path;
        if(startBack){
            mStartPath = path;
        }else{
            mStartPath = null;
        }
        setRetainInstance(true);
    }

    public ForderFragment(Context context,int type, TagItem item){
        mMainContext = context;
        mListType = type;
        mTagItem = item;
        setRetainInstance(true);
    }

    public ForderFragment(Context context, String currentPath, String search){
        mMainContext = context;
        mCurrentPath = currentPath;
        mSearch = search;
        setRetainInstance(true);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_folder, container, false);
        initLayout(inflater, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        ((MainActivity)mMainContext).finishSearchMode();
        ((MainActivity)mMainContext).toolbarMenu(R.menu.main);

        getActivity().setTitle("");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
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



    public void initLayout(LayoutInflater inflater, View rootView) {
        //final MainActivity context = (MainActivity) getActivity();
        foler_path_scroll_layout =  (HorizontalScrollView) rootView.findViewById(R.id.foler_path_scroll_layout);
        mFoler_path_layout =  (LinearLayout) rootView.findViewById(R.id.foler_path_layout);

        mListView = (ListView) rootView.findViewById(R.id.folder_listview);
        mGridView = (GridView) rootView.findViewById(R.id.folder_gridview);

        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        if(mView_Type == 0){
            changeListView();
        }else{
            changeGridView();
        }
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(mActionMode){
//                    mFolderAdapter.setCheck(position);
//                    mFolderAdapter.notifyDataSetChanged();
//                }else
//                {
//                    final File file = new File(((FileItem) mListView.getAdapter()
//                            .getItem(position)).getPath());
//
//                    if (file.isDirectory()) {
//                        folderListDisplay(file.getAbsolutePath());
//                        // go to the top of the ListView
//                        mListView.setSelection(0);
//                    } else {
//                        //listItemAction(file);
//                    }
//                }
//            }
//        });
//
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mFolderAdapter.setCheck(position);
//                setActionMode();
//                return true;
//            }
//        });
//        mListView.setAdapter(mFolderAdapter);


        //bottom menu
        list_bottom_menu_paste =  (ImageView) rootView.findViewById(R.id.list_bottom_menu_paste);
        list_bottom_menu_paste.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainContext, "붙여넣기", Toast.LENGTH_SHORT).show();
                final PasteTaskExecutor ptc = new PasteTaskExecutor(getActivity(), mCurrentPath);
                ptc.start();
                list_bottom_menu_paste.setVisibility(View.GONE);
//                final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
//                Snackbar.make(viewPos, "붙여넣기", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
            }
        });
        list_bottom_menu_sort =  (ImageView) rootView.findViewById(R.id.list_bottom_menu_sort);
        list_bottom_menu_sort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(mMainContext, "정렬", Toast.LENGTH_SHORT).show();
                dialogSetSort();
//                final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
//                Snackbar.make(viewPos, "정렬", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
            }
        });
        list_bottom_menu_check =  (ImageView) rootView.findViewById(R.id.list_bottom_menu_check);
        list_bottom_menu_check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mActionMode){
                    finishActionMode();
                }else{
                    setActionMode();
                }
            }
        });
    }

    public void changeListGridView() {
        if(mView_Type == 0){
            changeGridView();
        }else{
            changeListView();
        }
    }
    public void changeGridView() {
        //final MainActivity context = (MainActivity) getActivity();
        mView_Type = 1;
        changeActionModeList();
        mFolderAdapter = new FolderAdapter(getActivity(), null, mView_Type);
        mGridView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActionMode){
                    mFolderAdapter.setCheck(position);
                    mFolderAdapter.notifyDataSetChanged();
                    toolbarMenuIconChange();
                }else
                {
                    final File file = new File(((FileItem) mGridView.getAdapter()
                            .getItem(position)).getPath());

                    if (file.isDirectory()) {
                        mSearch = null;
                        if((mListType == 1 || mListType == 2) && mStartPath == null){
                            mStartPath = file.getAbsolutePath();
                        }
                        folderListDisplay(file.getAbsolutePath());
                        // go to the top of the ListView
                        mGridView.setSelection(0);
                    } else {
                        Utils.openFile(mMainContext, file);
                    }
                }
            }
        });

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setCheck(position);
                setActionMode();
                toolbarMenuIconChange();
                return true;
            }
        });
        mGridView.setAdapter(mFolderAdapter);
        if(mSearch != null && mSearch.length() > 0){
            folderSearchListDisplay(mCurrentPath, mSearch);
        }else if(mListType == 1){
            folderTagListDisplay(mTagItem);
        }else if(mListType == 2){
            folderFavoriteListDisplay(getResources().getString(R.string.category_favorite));
        }else{
            folderListDisplay(mCurrentPath);
        }
//        mFolderAdapter.addFiles(mCurrentPath);
//        mFolderAdapter.notifyDataSetChanged();
        changeFab();
    }



    public void changeListView() {
        //final MainActivity context = (MainActivity) getActivity();
        mView_Type = 0;
        changeActionModeList();
        mFolderAdapter = new FolderAdapter(getActivity(), null, mView_Type);
        mListView.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.GONE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mActionMode){
                    mFolderAdapter.setCheck(position);
                    mFolderAdapter.notifyDataSetChanged();
                    toolbarMenuIconChange();
                }else
                {
                    final File file = new File(((FileItem) mListView.getAdapter()
                            .getItem(position)).getPath());

                    if (file.isDirectory()) {
                        mSearch = null;
                        if((mListType == 1 || mListType == 2) && mStartPath == null){
                            mStartPath = file.getAbsolutePath();
                        }
                        folderListDisplay(file.getAbsolutePath());
                        // go to the top of the ListView
                        mListView.setSelection(0);
                    } else {
                        Utils.openFile(mMainContext, file);
                    }
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setCheck(position);
                setActionMode();
                toolbarMenuIconChange();
                return true;
            }
        });
        mListView.setAdapter(mFolderAdapter);
        if(mSearch != null && mSearch.length() > 0){
            folderSearchListDisplay(mCurrentPath, mSearch);
        }else if(mListType == 1){
            folderTagListDisplay(mTagItem);
        }else if(mListType == 2){
            folderFavoriteListDisplay(getResources().getString(R.string.category_favorite));
        }else{
            folderListDisplay(mCurrentPath);
        }
//        mFolderAdapter.addFiles(mCurrentPath);
//        mFolderAdapter.notifyDataSetChanged();
        changeFab();
    }

    public void changeFab() {
        if(mView_Type == 0){
            //fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_pab_btn));
            mFab.attachToListView(mListView);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getActivity(), "fab" , Toast.LENGTH_SHORT).show();

                    final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                    Snackbar.make(viewPos, "Replace with your own action", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                }
            });
        }else{
            //fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_pab_btn));
            mFab.attachToListView(mGridView);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getActivity(), "fab" , Toast.LENGTH_SHORT).show();
                    final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                    Snackbar.make(viewPos, "Replace with your own action", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    public void folderListDisplay(String path) {
        mCurrentPath = path;
        //mFoler_path.setText(mCurrentPath);
        setFolderFathLayout(mCurrentPath);
        foler_path_scroll_layout.post(new Runnable() {
            public void run() {
                foler_path_scroll_layout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        mFolderAdapter.addFiles(path, 0);
    }

    public void folderTagListDisplay(TagItem tag) {
        //mFoler_path.setText(tag.getName());
        setFolderFathLayout(tag.getName());
        mCurrentPath = "/";
        foler_path_scroll_layout.post(new Runnable() {
            public void run() {
                foler_path_scroll_layout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        mFolderAdapter.addTagFiles(tag);
    }

    public void folderSearchListDisplay(String currentPath, String search) {
        //mFoler_path.setText(title);
        setFolderFathLayout(search);
        foler_path_scroll_layout.post(new Runnable() {
            public void run() {
                foler_path_scroll_layout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

        SearchTask mTask = new SearchTask(getActivity(), currentPath);
        mTask.execute(search);
    }

    public void folderFavoriteListDisplay(String title) {
        //mFoler_path.setText(title);
        setFolderFathLayout(title);
        mCurrentPath = "/";
        foler_path_scroll_layout.post(new Runnable() {
            public void run() {
                foler_path_scroll_layout.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        mFolderAdapter.addFavoriteFiles();
    }

    public void setFolderFathLayout(String path) {
        mFoler_path_layout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        String[] arrayPath = path.split("/");
        if (path.contains("/") && arrayPath.length > 1) {
            String makePath = "";
            for (int i = 1; i < arrayPath.length; i++) {
                View path_view = inflater.inflate(R.layout.frag_folder_path_item, null, false);
                TextView foler_path = (TextView) path_view.findViewById(R.id.foler_path);
                foler_path.setText("/" + arrayPath[i]);
                makePath += ("/" + arrayPath[i]);
                path_view.setTag(makePath);
                path_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        folderListDisplay((String) v.getTag());
                    }
                });
                mFoler_path_layout.addView(path_view);
            }
        } else {
            View path_view = inflater.inflate(R.layout.frag_folder_path_item, null, false);
            TextView foler_path = (TextView) path_view.findViewById(R.id.foler_path);
            foler_path.setText(path);
            mFoler_path_layout.addView(path_view);
        }
    }

    public void toolbarMenuIconChange(){
        ArrayList<FileItem> items = mFolderAdapter.getCheckList();
        boolean allhide = true;
        boolean allfavorite = true;
        if(items != null && items.size() > 0){
            for(FileItem item : items){
                if(allhide){
                    if(!item.getName().startsWith(".")){
                        allhide = false;
                    }
                }
                if(allfavorite){
                    if(item.getFavorite() == null || !item.getFavorite().equals("Y") ){
                        allfavorite = false;
                    }
                }
            }
            if(allhide){
                ((MainActivity)mMainContext).toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_unhide);
            }else{
                ((MainActivity)mMainContext).toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_hide);
            }
            if(allfavorite){
                ((MainActivity)mMainContext).toolbar.getMenu().getItem(2).setIcon(R.mipmap.ic_unfavorite);
            }else{
                ((MainActivity)mMainContext).toolbar.getMenu().getItem(2).setIcon(R.mipmap.ic_favorite);
            }
        }else{
            ((MainActivity)mMainContext).toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_hide);
            ((MainActivity)mMainContext).toolbar.getMenu().getItem(2).setIcon(R.mipmap.ic_favorite);
        }
    }

    public void setActionMode() {
        mActionMode = true;
        ((MainActivity)mMainContext).toolbar.getMenu().clear();
        ((MainActivity)mMainContext).toolbar.inflateMenu(R.menu.action_mode);
        ((MainActivity)mMainContext).finishSearchMode();
        mFolderAdapter.notifyDataSetChanged();
    }

    public void finishActionMode() {
        mActionMode = false;
        ((MainActivity)mMainContext).toolbar.getMenu().clear();
        ((MainActivity)mMainContext).toolbar.inflateMenu(R.menu.main);
        if(ClipBoard.isEmpty()){
            list_bottom_menu_paste.setVisibility(View.GONE);
        }else{
            list_bottom_menu_paste.setVisibility(View.VISIBLE);
        }
        mFolderAdapter.setCheckClear();
        mFolderAdapter.notifyDataSetChanged();
    }

    public void changeActionModeList() {
        ((MainActivity)mMainContext).toolbar.getMenu().clear();
        if(mView_Type == 0){
            ((MainActivity)mMainContext).toolbar.inflateMenu(R.menu.main);
        }else{
            ((MainActivity)mMainContext).toolbar.inflateMenu(R.menu.main_thum);
        }
    }

    public boolean onBackPressed() {

        if(mActionMode){
            finishActionMode();
            return true;
        }else if (mCurrentPath != null && mCurrentPath.equals("/")) {
            //getActivity().finish();
            ((MainActivity)mMainContext).initFragment();
            return false;
        } else{
            try {
                if(mSearch != null && mSearch.length() > 0){
                    mSearch = null;
                    folderListDisplay(mCurrentPath);
                }else {
                    if(mStartPath != null && mStartPath.equals(mCurrentPath)){
                        if(mListType == 1){
                            folderTagListDisplay(mTagItem);
                        }else if(mListType == 2){
                            folderFavoriteListDisplay(getResources().getString(R.string.category_favorite));
                        }else {
                            ((MainActivity) mMainContext).initFragment();
                        }
                    }else {
                        File file = new File(mCurrentPath);
                        folderListDisplay(file.getParent());
                    }
                }
            }catch(Exception e){
                //getActivity().finish();
                ((MainActivity)mMainContext).initFragment();
                return false;
            }
            // get position of the previous folder in ListView
            //mListView.setSelection(mListAdapter.getPosition(file.getPath()));
            return true;
        }
    }

    public void hideMethod() {
        final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
        int check_count = mFolderAdapter.getCheckCount();
        ArrayList<FileItem> items = mFolderAdapter.getCheckList();
        if(items != null && items.size() > 0){
            boolean allHide = true;
            for(FileItem item : items){
                if(!item.getName().startsWith(".")){
                    allHide = false;
                    break;
                }
            }
            for(FileItem item : items){
                if(allHide){
                    File from = new File(item.getPath());
                    File directory = from.getParentFile();
                    File to = new File(directory, item.getName().replaceFirst(".", ""));
                    from.renameTo(to);
                    item.setPath(directory.getPath() + File.separator + item.getName().replaceFirst(".", ""));
                    item.setName(item.getName().replaceFirst(".", ""));
                }else{
                    if(!item.getName().startsWith(".")){
                        File from = new File(item.getPath());
                        File directory = from.getParentFile();
                        File to = new File(directory, "." + item.getName());
                        from.renameTo(to);
                        item.setPath(directory.getPath() + File.separator + "." + item.getName());
                        item.setName("." + item.getName());
                    }
                }
            }
            if(allHide){
                Snackbar.make(viewPos, check_count + getResources().getString(R.string.snackbar_file_unhide), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }else{
                Snackbar.make(viewPos, check_count + getResources().getString(R.string.snackbar_file_hide), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }else{
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_no_select), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        finishActionMode();
    }
    public void copyMethod() {
        final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
        int count = mFolderAdapter.getCheckCount();

        if(count > 0){
            ArrayList<FileItem> items = mFolderAdapter.getCheckList();
            ClipBoard.cutCopy(items);
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_copy), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }else{
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_no_select), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        finishActionMode();
    }
    public void favoriteMethod() {

        final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
        int check_count = mFolderAdapter.getCheckCount();
        ArrayList<FileItem> items = mFolderAdapter.getCheckList();
        if(items != null && items.size() > 0){
            boolean allFavorite = true;
            for(FileItem item : items){
                if(item.getFavorite() == null || !item.getFavorite().equals("Y")){
                    allFavorite = false;
                    break;
                }
            }
            for(FileItem item : items){
                if(allFavorite){
                    if(item.getTagId() != null && item.getTagId().length() > 0){
                        MainActivity.mMds.UpdateFileFavorite(item.getId(), "N");
                    }else{
                        MainActivity.mMds.deleteFile(item.getId());
                    }
                    item.setFavorite("N");
                }else {
                    if(item.getTagId() != null && item.getTagId().length() > 0){
                        MainActivity.mMds.UpdateFileFavorite(item.getId(), "Y");
                    }else{
                        MainActivity.mMds.insertFileItem(item.getName(), item.getPath(), "", "", "Y");
                    }
                    item.setFavorite("Y");
                }
            }

            if(allFavorite){
                Snackbar.make(viewPos, check_count + getResources().getString(R.string.snackbar_file_unfavorite), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }else{
                Snackbar.make(viewPos, check_count + getResources().getString(R.string.snackbar_file_favorite), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }else{
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_no_select), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        finishActionMode();
    }
    public void tagMethod(TagItem tag) {

        final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);

        ArrayList<FileItem> items = mFolderAdapter.getCheckList();
        if(items != null && items.size() > 0){
            for(FileItem item : items){
                if(tag == null){
                    if(item.getFavorite() != null && item.getFavorite().equals("Y")){
                        MainActivity.mMds.UpdateFileTag(item.getId(), "", "");
                    }else{
                        MainActivity.mMds.deleteFile(item.getId());
                    }
                    item.setTagId("");
                    item.setTagColor("");
                }else {
                    if(item.getFavorite() != null && item.getFavorite().equals("Y")){
                        MainActivity.mMds.UpdateFileTag(item.getId(), tag.getId(), tag.getColor());
                    }else{
                        MainActivity.mMds.insertFileItem(item.getName(), item.getPath(), tag.getId(), tag.getColor(), "N");
                    }
//                    if (item.getTagId() != null && item.getTagId().length() > 0) {
//                        MainActivity.mMds.UpdateFileTag(item.getId(), tag.getId(), tag.getColor());
//                    } else {
//                        MainActivity.mMds.insertFileItem(item.getName(), item.getPath(), tag.getId(), tag.getColor(), "N");
//                    }
                    item.setTagId(tag.getId());
                    item.setTagColor(tag.getColor());
                }
            }
//            Snackbar.make(viewPos, "태그", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
        }else{
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_no_select), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        finishActionMode();
    }
    public void deleteMethod() {
        int check_count = mFolderAdapter.getCheckCount();

        if(check_count > 0){
            dialogDeleteFile(check_count);
        }else{
            final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
            Snackbar.make(viewPos, getResources().getString(R.string.snackbar_no_select), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

            finishActionMode();
        }
    }

    public void dialogSetSort(){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_select_sort, null, false);
        LinearLayout sort_by_name = (LinearLayout)view.findViewById(R.id.sort_by_name);
        TextView sort_by_name_txt = (TextView)view.findViewById(R.id.sort_by_name_txt);
        if(Settings.getSortType() != SortUtils.SORT_ALPHA){
            sort_by_name_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        sort_by_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setSortType(SortUtils.SORT_ALPHA);
                mFolderAdapter.reSort();
            }
        });
        LinearLayout sort_by_extension = (LinearLayout)view.findViewById(R.id.sort_by_extension);
        TextView sort_by_extension_txt = (TextView)view.findViewById(R.id.sort_by_extension_txt);
        if(Settings.getSortType() != SortUtils.SORT_TYPE){
            sort_by_extension_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        sort_by_extension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setSortType(SortUtils.SORT_TYPE);
                mFolderAdapter.reSort();
            }
        });
        LinearLayout sort_by_date = (LinearLayout)view.findViewById(R.id.sort_by_date);
        TextView sort_by_date_txt = (TextView)view.findViewById(R.id.sort_by_date_txt);
        if(Settings.getSortType() != SortUtils.SORT_DATE){
            sort_by_date_txt.setTextColor(getResources().getColorStateList(R.color.gray3));
        }
        sort_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                Settings.setSortType(SortUtils.SORT_DATE);
                mFolderAdapter.reSort();
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void dialogDeleteFile(final int check_count){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_base, null, false);
        TextView title = (TextView)view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.dialog_delete_file));
        TextView content = (TextView)view.findViewById(R.id.content);
        content.setText(check_count + getResources().getString(R.string.dialog_delete_file_info));
        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        TextView ok = (TextView)view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                ArrayList<FileItem> items = mFolderAdapter.getCheckList();

                for(FileItem item : items){
                    MainActivity.mMds.deleteFile(item.getId());
                    File select = new File(item.getPath());
                    if(select != null && select.exists()){
                        select.delete();
                    }
                }
                Snackbar.make(viewPos, check_count + getResources().getString(R.string.snackbar_file_delete), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                mFolderAdapter.refresh();
                finishActionMode();

                if(alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }
    public void listRefresh(){
        mFolderAdapter.refresh();
    }
    public void dialogSelectTag(){
        builder = new AlertDialog.Builder(mMainContext);
        LayoutInflater inflater = LayoutInflater.from(mMainContext);
        View view = inflater.inflate(R.layout.dialog_select_tag, null, false);
        LinearLayout dialog_tag_list_layout = (LinearLayout)view.findViewById(R.id.dialog_tag_list_layout);

        ArrayList<TagItem> tagItams = MainActivity.mMds.getAllTag();
        dialog_tag_list_layout.removeAllViews();
        if(tagItams != null && tagItams.size() > 0) {
            for (final TagItem item : tagItams) {
                View tag_view = inflater.inflate(R.layout.dialog_select_tag_list_item, null, false);
                ImageView tag_color = (ImageView) tag_view.findViewById(R.id.tag_color);
                tag_color.setImageResource(Utils.getTagColorResourceSmall(item.getColor()));
                TextView tag_name = (TextView) tag_view.findViewById(R.id.tag_name);
                tag_name.setText(item.getName());
                tag_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tagMethod(item);
                        if(alertDialog != null){
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                    }
                });
                dialog_tag_list_layout.addView(tag_view);
            }
//            View tag_view = inflater.inflate(R.layout.dialog_select_tag_list_item, null, false);
//            ImageView tag_color = (ImageView) tag_view.findViewById(R.id.tag_color);
//            tag_color.setImageResource(Utils.getTagColorResourceSmall("0"));
//            TextView tag_name = (TextView) tag_view.findViewById(R.id.tag_name);
//            tag_name.setText("Tag Relese");
//            tag_view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tagMethod(null);
//                    if(alertDialog != null){
//                        alertDialog.dismiss();
//                        alertDialog = null;
//                    }
//                }
//            });
//            dialog_tag_list_layout.addView(tag_view);

        }
        LinearLayout create_new_tag = (LinearLayout) view.findViewById(R.id.create_new_tag);
        create_new_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                dialogNewTag();
            }
        });
        LinearLayout delete_tag = (LinearLayout) view.findViewById(R.id.delete_tag);
        delete_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                tagMethod(null);
            }
        });
        TextView cancel = (TextView)view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        TextView create = (TextView)view.findViewById(R.id.create);
        create.setVisibility(View.GONE);
//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(alertDialog != null){
//                    alertDialog.dismiss();
//                }
//
//            }
//        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
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
        tagColor1.setOnClickListener(new View.OnClickListener() {
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
        tagColor2.setOnClickListener(new View.OnClickListener() {
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

        tagColor3.setOnClickListener(new View.OnClickListener() {
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

        tagColor4.setOnClickListener(new View.OnClickListener() {
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

        tagColor5.setOnClickListener(new View.OnClickListener() {
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

        tagColor6.setOnClickListener(new View.OnClickListener() {
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

        tagColor7.setOnClickListener(new View.OnClickListener() {
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

        tagColor8.setOnClickListener(new View.OnClickListener() {
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

        tagColor9.setOnClickListener(new View.OnClickListener() {
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

        tagColor10.setOnClickListener(new View.OnClickListener() {
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
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        TextView create = (TextView)view.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_name = edt.getText().toString();
                if(tag_name != null && tag_name.length() > 0){
                    int select = (int)edt.getTag();

                    Toast.makeText(mMainContext, tag_name + " => " + select, Toast.LENGTH_SHORT).show();
                    MainActivity.mMds.insertTagItem(tag_name, select + "");
                    ((MainActivity)mMainContext).displayTagList();

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



    //Search Method
    private class SearchTask extends AsyncTask<String, Void, ArrayList<FileItem>> {
        private ProgressDialog pr_dialog;
        private final Context context;
        private String mCurrentPath;

        private SearchTask(Context c, String currentPath) {
            context = c;
            mCurrentPath = currentPath;
        }

        @Override
        protected void onPreExecute() {
            pr_dialog = ProgressDialog.show(context, null,
                    context.getResources().getString(R.string.action_search));
            pr_dialog.setCanceledOnTouchOutside(true);
        }

        @Override
        protected ArrayList<FileItem> doInBackground(String... params) {
            //String location = BrowserTabsAdapter.getCurrentBrowserFragment().mCurrentPath;
            return Utils.searchInDirectory(mCurrentPath, params[0]);
        }

        @Override
        protected void onPostExecute(final ArrayList<FileItem> files) {
            int len = files != null ? files.size() : 0;
            pr_dialog.dismiss();
            if (len == 0) {
                final View viewPos = getActivity().findViewById(R.id.myCoordinatorLayout);
                Snackbar.make(viewPos, getResources().getString(R.string.snackbar_search_result_empty), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            } else {
                mFolderAdapter.addContent(files);
                mFolderAdapter.notifyDataSetChanged();
            }
        }
    }
}
