package myfilemanager.jiran.com.myfilemanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.common.ListFunctionUtils;
import myfilemanager.jiran.com.myfilemanager.common.SortUtils;
import myfilemanager.jiran.com.myfilemanager.common.Utils;
import myfilemanager.jiran.com.myfilemanager.data.FileItem;
import myfilemanager.jiran.com.myfilemanager.data.TagItem;
import myfilemanager.jiran.com.myfilemanager.fagment.ForderFragment;
import myfilemanager.jiran.com.myfilemanager.preview.IconPreview;
import myfilemanager.jiran.com.myfilemanager.preview.MimeTypes;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

/**
 * Created by user on 2016-08-03.
 */
public class FolderAdapter extends BaseAdapter{

    private final LayoutInflater mInflater;
    private final Resources mResources;
    private ArrayList<FileItem> mDataSource;
    private String mCurrentPath;
    private final Context mContext;

    private int mType;

    ThumbLoader mThumbLoader;

    public FolderAdapter(Context context, LayoutInflater inflater, int type) {
        super();
        mInflater = inflater;
        mContext = context;
        mType = type;
        mDataSource = new ArrayList<>();
        mResources = context.getResources();

        mThumbLoader = new ThumbLoader(context);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public FileItem getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView icon;
        TextView filename;
        TextView filedate;
        TextView filesize;
        RelativeLayout list_checker_layout;
        CheckBox list_checker;

        ImageView favorite_icon;
        ImageView tag_icon;

        LinearLayout showhidden;
        ViewHolder(View view) {
            filename = (TextView) view.findViewById(R.id.filename);
            filedate = (TextView) view.findViewById(R.id.filedate);
            icon = (ImageView) view.findViewById(R.id.icon);

            list_checker_layout = (RelativeLayout) view.findViewById(R.id.list_checker_layout);
            list_checker = (CheckBox) view.findViewById(R.id.list_checker);

            favorite_icon = (ImageView) view.findViewById(R.id.favorite_icon);
            tag_icon = (ImageView) view.findViewById(R.id.tag_icon);

            showhidden = (LinearLayout) view.findViewById(R.id.showhidden);

            if(mType == 0) {
                filesize = (TextView) view.findViewById(R.id.filesize);
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mViewHolder;
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.getDefault());

        if (convertView != null) {
            mViewHolder = (ViewHolder) convertView.getTag();
            //holder.fileimage.setImageResource(R.drawable.stub);
        } else {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            if(mType == 0) {
                convertView = inflater.inflate(R.layout.list_folder, parent, false);
            }else{
                convertView = inflater.inflate(R.layout.grid_folder, parent, false);
            }
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }
        FileItem item = getItem(position);
        final File file = new File(item.getPath());

        if ( MimeTypes.isPicture(file)) {
//            mThumbLoader.DisplayImage(mContext, item.getPath(), item.getRegDate(), "0", mViewHolder.icon, false, false);
            Glide.with(mContext)
                .load(file)
                .into(mViewHolder.icon);
        } else {
            int mimeIcon = 0;

            if (file != null && file.isDirectory()) {
                String[] files = file.list();
                if (file.canRead() && files != null && files.length > 0) {
                    mimeIcon = R.drawable.fd_contain_l;
                    //mimeIcon = mResources.getDrawable(R.drawable.fd_contain_l);
                }else {
                    mimeIcon = R.drawable.fd_l;
                    //mimeIcon = mResources.getDrawable(R.drawable.fd_l);
                }
            } else if (file != null && file.isFile()) {
                final String fileExt = ListFunctionUtils.getExtension(file.getName());
                mimeIcon = MimeTypes.getIconForExt(fileExt);
            }

            if (mimeIcon != 0) {
                //icon.setImageDrawable(mimeIcon);
                mViewHolder.icon.setImageResource(mimeIcon);
            } else {
                // default icon
                mViewHolder.icon.setImageResource(R.drawable.ic_etc);
            }
        }
        //IconPreview.getFileIcon(file, mViewHolder.icon);

        if(ForderFragment.mActionMode){
            mViewHolder.list_checker_layout.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.list_checker_layout.setVisibility(View.GONE);
        }

//        mViewHolder.list_checker.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(mViewHolder.list_checker.isChecked()){
//                    setCheck(position, true);
//                }else{
//                    setCheck(position, false);
//                }
//            }
//        });
        if(item.getcheck()){
            mViewHolder.list_checker.setChecked(true);
        }else{
            mViewHolder.list_checker.setChecked(false);
        }

        mViewHolder.filename.setText(file.getName());
        mViewHolder.filedate.setText(df.format(file.lastModified()));

        if(item.getFavorite() != null && item.getFavorite().equals("Y")){
            mViewHolder.favorite_icon.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.favorite_icon.setVisibility(View.GONE);
        }
        if(item.getTagId() != null && item.getTagId().length() > 0){
            if(mType == 0) {
                mViewHolder.tag_icon.setImageResource(Utils.getTagColorResourceSmall(item.getTagColor()));
            }else{
                mViewHolder.tag_icon.setImageResource(Utils.getTagColorResourceLb(item.getTagColor()));
            }
            mViewHolder.tag_icon.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.tag_icon.setVisibility(View.GONE);
        }

        if(mType == 0) {
            if(!file.isDirectory()){
                mViewHolder.filesize.setText(ListFunctionUtils.formatCalculatedSize(file.length()));
            }else{
                mViewHolder.filesize.setText("");
            }
        }

        if(item.getName().startsWith(".")){
            mViewHolder.showhidden.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.showhidden.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setCheck(int position) {
            mDataSource.get(position).setcheck(!mDataSource.get(position).getcheck());
    }

    public ArrayList<FileItem> getCheckList() {
        ArrayList<FileItem> checkList = new ArrayList<FileItem>();
        for(FileItem item : mDataSource){
            if(item.getcheck()){
                checkList.add(item);
            }
        }
        return checkList;
    }

    public int getCheckCount() {
        int check_count = 0;
        for(FileItem item : mDataSource){
            if(item.getcheck()){
                check_count++;
            }
        }
        return check_count;
    }

    public ArrayList<FileItem> getList() {
        return mDataSource;
    }

    public void setCheckClear() {
        for(FileItem item : mDataSource){
            item.setcheck(false);
        }
    }

    public void addFiles(String path, int sort_type) {
        if (!mDataSource.isEmpty())
            mDataSource.clear();

        mCurrentPath = path;
        mDataSource = ListFunctionUtils.listFiles(path, mContext);

        // sort files with a comparator if not empty
        if (!mDataSource.isEmpty())
            SortUtils.sortList(mDataSource, path, Settings.getSortType());

        notifyDataSetChanged();
    }

    public void reSort() {
        // sort files with a comparator if not empty
        if (!mDataSource.isEmpty())
            SortUtils.sortList(mDataSource, mCurrentPath, Settings.getSortType());

        notifyDataSetChanged();
    }

    public void refresh() {
        if (!mDataSource.isEmpty())
            mDataSource.clear();

        mDataSource = ListFunctionUtils.listFiles(mCurrentPath, mContext);

        // sort files with a comparator if not empty
        if (!mDataSource.isEmpty())
            SortUtils.sortList(mDataSource, mCurrentPath, Settings.getSortType());

        notifyDataSetChanged();
    }

    public void addTagFiles(TagItem tag) {
        if (!mDataSource.isEmpty())
            mDataSource.clear();

        mDataSource = ListFunctionUtils.listTagFiles(tag, mContext);

//        // sort files with a comparator if not empty
//        if (!mDataSource.isEmpty())
//            SortUtils.sortList(mDataSource, path);

        notifyDataSetChanged();
    }

    public void addFavoriteFiles() {
        if (!mDataSource.isEmpty())
            mDataSource.clear();

        mDataSource = ListFunctionUtils.listFavoriteFiles(mContext);

//        // sort files with a comparator if not empty
//        if (!mDataSource.isEmpty())
//            SortUtils.sortList(mDataSource, path);

        notifyDataSetChanged();
    }

    public void addContent(ArrayList<FileItem> files) {
        if (!mDataSource.isEmpty())
            mDataSource.clear();

        mDataSource = files;

        notifyDataSetChanged();
    }

    public int getPosition(String path) {
        return mDataSource.indexOf(path);
    }

    public ArrayList<FileItem> getContent() {
        return mDataSource;
    }

}
