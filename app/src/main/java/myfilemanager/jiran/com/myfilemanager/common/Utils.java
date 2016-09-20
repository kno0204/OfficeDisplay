package myfilemanager.jiran.com.myfilemanager.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import myfilemanager.jiran.com.myfilemanager.R;
import myfilemanager.jiran.com.myfilemanager.data.FileItem;
import myfilemanager.jiran.com.myfilemanager.settings.Settings;

import static android.os.Environment.getStorageState;

/**
 * Created by user on 2016-08-03.
 */
public class Utils {

    private static final int BUFFER = 16384;

    public static boolean isSdMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /** 전체 내장 메모리 크기를 가져온다 */
    public static long getTotalSDCaredMemorySize() {
        //File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(getSDcardDirectoryPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();

        return totalBlocks * blockSize;
    }

    /** 사용가능한 내장 메모리 크기를 가져온다 */
    public static  long getSDCardMemorySize() {
        //File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(getSDcardDirectoryPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    /** 전체 외장 메모리 크기를 가져온다 */
    public static long getTotalExternalMemorySize() {
        if (isStorage(true) == true) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();

            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /** 사용가능한 외장 메모리 크기를 가져온다 */
    public static long getExternalMemorySize() {
        if (isStorage(true) == true) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /** 보기 좋게 MB,KB 단위로 축소시킨다 */
    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                    size /= 1024;
                }
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }

    /** 외장메모리 sdcard 사용가능한지에 대한 여부 판단 */
    public static boolean isStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (!requireWriteAccess &&
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /** 외장메모리 sdcard 사용가능한지에 대한 여부 판단 */
    public static boolean isSDcardDirectory(boolean requireWriteAccess) {
        String sdFileName = getSDcardDirectoryPath();
        Log.i("isSDcardDirectory", "sdFileName => " + sdFileName);
        if(sdFileName != null && sdFileName.length() > 0){
            final File file = new File(sdFileName);

            String state = getStorageState(file);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            } else if (!requireWriteAccess &&
                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                return true;
            }
        }
        return false;
//        if(file.exists() || file.isDirectory()){
//            return true;
//        }
//        return false;
    }
    /**
     * Returns the path to internal storage ex:- /storage/emulated/0
     *
     * @return
     */
    public static String getInternalDirectoryPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Returns the SDcard storage path for samsung ex:- /storage/extSdCard
     *
     * @return
     */
    public static String getSDcardDirectoryPath() {
        String strSDCardPath = System.getenv("SECONDARY_STORAGE");

        if(strSDCardPath != null && strSDCardPath.length() > 0){
            Log.i("getSDcardDirectoryPath", "SECONDARY_STORAGE => " + strSDCardPath);
            return strSDCardPath;
        }

        strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");

        if(strSDCardPath != null && strSDCardPath.length() > 0){
            Log.i("getSDcardDirectoryPath", "EXTERNAL_SDCARD_STORAGE => " + strSDCardPath);
            return strSDCardPath;
        }

        HashSet<String> path = getExternalMounts();

        for(String extSDCardPath : path)
        {
            if(extSDCardPath != null && extSDCardPath.length() > 0){
                String[] array = extSDCardPath.split("/");
                if(array.length > 0){
                    strSDCardPath = "/storage/" + array[array.length-1];
                    Log.i("getSDcardDirectoryPath", "getExternalMounts => " + strSDCardPath);
                    return strSDCardPath;
                }
            }
        }
        return strSDCardPath;
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }


    public static String getImageDirectoryPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public static String getVideoDirectoryPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    public static String getMusicDirectoryPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    public static String getFavoriteDirectoryPath() {
        return System.getenv("SECONDARY_STORAGE");
    }

    public static String getDownloadDirectoryPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }



    /** Tag Color에 따라서 Image Resource value retun */
    public static int getTagColorResourceSmall(String  color) {
        int resource = R.mipmap.tag_red_s;
        if(color.equals("0")){
            resource = R.mipmap.tag_black_s;
        }else if(color.equals("1")){
            resource = R.mipmap.tag_red_s;
        }else if(color.equals("2")){
            resource = R.mipmap.tag_orange_s;
        }else if(color.equals("3")){
            resource = R.mipmap.tag_yellow_s;
        }else if(color.equals("4")){
            resource = R.mipmap.tag_green_s;
        }else if(color.equals("5")){
            resource = R.mipmap.tag_sky_s;
        }else if(color.equals("6")){
            resource = R.mipmap.tag_indigo_s;
        }else if(color.equals("7")){
            resource = R.mipmap.tag_pupple_s;
        }else if(color.equals("8")){
            resource = R.mipmap.tag_black_s;
        }else if(color.equals("9")){
            resource = R.mipmap.tag_grey_s;
        }else if(color.equals("10")){
            resource = R.mipmap.tag_lightgrey_s;
        }
        return resource;
    }

    public static int getTagColorResourceLb(String  color) {
        int resource = R.mipmap.tag_red_lb;
        if(color.equals("0")){
            resource = R.mipmap.tag_black_lb;
        }else if(color.equals("1")){
            resource = R.mipmap.tag_red_lb;
        }else if(color.equals("2")){
            resource = R.mipmap.tag_orange_lb;
        }else if(color.equals("3")){
            resource = R.mipmap.tag_yellow_lb;
        }else if(color.equals("4")){
            resource = R.mipmap.tag_green_lb;
        }else if(color.equals("5")){
            resource = R.mipmap.tag_sky_lb;
        }else if(color.equals("6")){
            resource = R.mipmap.tag_indigo_lb;
        }else if(color.equals("7")){
            resource = R.mipmap.tag_pupple_lb;
        }else if(color.equals("8")){
            resource = R.mipmap.tag_black_lb;
        }else if(color.equals("9")){
            resource = R.mipmap.tag_grey_lb;
        }else if(color.equals("10")){
            resource = R.mipmap.tag_lightgrey_lb;
        }
        return resource;
    }

    public static int getTagColorResourceLarge(String  color) {
        int resource = R.mipmap.tag_red_l;
        if(color.equals("1")){
            resource = R.mipmap.tag_red_l;
        }else if(color.equals("2")){
            resource = R.mipmap.tag_orange_l;
        }else if(color.equals("3")){
            resource = R.mipmap.tag_yellow_l;
        }else if(color.equals("4")){
            resource = R.mipmap.tag_green_l;
        }else if(color.equals("5")){
            resource = R.mipmap.tag_sky_l;
        }else if(color.equals("6")){
            resource = R.mipmap.tag_indigo_l;
        }else if(color.equals("7")){
            resource = R.mipmap.tag_pupple_l;
        }else if(color.equals("8")){
            resource = R.mipmap.tag_black_l;
        }else if(color.equals("9")){
            resource = R.mipmap.tag_grey_l;
        }else if(color.equals("10")){
            resource = R.mipmap.tag_lightgrey_l;
        }
        return resource;
    }

    //Search Method
    private static void search_file(String dir, String fileName, ArrayList<FileItem> n) {
        File rootDir = new File(dir);
        String[] list = rootDir.list();
        //boolean root = Settings.rootAccess();

        if (list != null && rootDir.canRead()) {
            for (String aList : list) {
                File check = new File(dir + "/" + aList);
                String name = check.getName();

                if (check.isFile() && name.toLowerCase().contains(fileName.toLowerCase())) {
                    FileItem item = new FileItem();
                    item.setName(check.getName());
                    item.setPath(check.getPath());
                    item.setTagId("");
                    n.add(item);
                } else if (check.isDirectory()) {
                    if (name.toLowerCase().contains(fileName.toLowerCase())) {
                        FileItem item = new FileItem();
                        item.setName(check.getName());
                        item.setPath(check.getPath());
                        item.setTagId("");
                        n.add(item);
                        // change this!
                    } else if (check.canRead() && !dir.equals("/")) {
                        search_file(check.getAbsolutePath(), fileName, n);
                    }
//                    else if (!check.canRead() && root) {
//                        ArrayList<String> al = RootCommands.findFiles(check.getAbsolutePath(), fileName);
//
//                        for (String items : al) {
//                            File file = new File(items);
//                            FileItem item = new FileItem();
//                            item.setName(file.getName());
//                            item.setPath(file.getPath());
//                            item.setTagId("");
//                            n.add(item);
//                        }
//                    }
                }
            }
        }
//        else {
//            if (root) {
//                ArrayList<String> items = RootCommands.findFiles(dir, fileName);
//                ArrayList<FileItem> fileitems = new ArrayList<FileItem>();
//                for(String path : items){
//                    File file = new File(path);
//                    FileItem item = new FileItem();
//                    item.setName(file.getName());
//                    item.setPath(file.getPath());
//                    item.setTagId("");
//                    fileitems.add(item);
//                }
//                n.addAll(fileitems);
//            }
//        }
    }

    public static ArrayList<FileItem> searchInDirectory(String dir, String fileName) {
        ArrayList<FileItem> names = new ArrayList<FileItem>();
        search_file(dir, fileName, names);
        return names;
    }


    public static void moveToDirectory(File oldFile, File target, Context c) {
        if (!oldFile.renameTo(target) && copyFile(oldFile, target, c)) {
            deleteTarget(oldFile.getAbsolutePath());
        }
    }

    public static boolean copyFile(final File source, final File target, Context context) {
        FileInputStream inStream = null;
        OutputStream outStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            File tempDir = target.getParentFile();

            if (source.isFile())
                inStream = new FileInputStream(source);

            if (source.canRead() && tempDir.isDirectory()) {
                if (source.isFile()) {
                    outStream = new FileOutputStream(target);
                    inChannel = inStream.getChannel();
                    outChannel = ((FileOutputStream) outStream).getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                } else if (source.isDirectory()){
                    File[] files = source.listFiles();

                    if (createDir(target)) {
                        for (File file : files) {
                            copyFile(new File(source, file.getName()), new File(target, file.getName()), context);
                        }
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DocumentFile targetDocument = DocumentFile.fromFile(tempDir);
                    outStream = context.getContentResolver().openOutputStream(targetDocument.getUri());
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Uri uri = MediaStoreUtils.getUriFromFile(target.getAbsolutePath(), context);
                    outStream = context.getContentResolver().openOutputStream(uri);
                } else {
                    return false;
                }

                if (outStream != null && inStream !=null) {
                    byte[] buffer = new byte[BUFFER];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                } else {
                    RootCommands.moveCopyRoot(source.getAbsolutePath(), target.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inStream != null && outStream != null && inChannel != null && outChannel != null) {
                    inStream.close();
                    outStream.close();
                    inChannel.close();
                    outChannel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean renameTarget(String filePath, String newName) {
        File src = new File(filePath);

        String temp = filePath.substring(0, filePath.lastIndexOf("/"));
        File dest = new File(temp + "/" + newName);

        if (src.renameTo(dest)) {
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DocumentFile document = DocumentFile.fromFile(src);

                if (document.renameTo(dest.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean createFile(File file) {
        if (file.exists()) {
            return !file.isDirectory();
        }

        try {
            if (file.createNewFile()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DocumentFile document = DocumentFile.fromFile(file.getParentFile());

            try {
                return document.createFile(MimeTypes.getMimeType(file), file.getName()) != null;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean createDir(File folder) {
        if (folder.exists())
            return false;

        if (folder.mkdir())
            return true;
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DocumentFile document = DocumentFile.fromFile(folder.getParentFile());
                if (document.exists())
                    return true;
            }

            if (Settings.rootAccess()) {
                return RootCommands.createRootdir(folder);
            }
        }

        return false;
    }

    public static void deleteTarget(String path) {
        File target = new File(path);

        if (target.isFile() && target.canWrite()) {
            target.delete();
        } else if (target.isDirectory() && target.canRead() && target.canWrite()) {
            String[] fileList = target.list();

            if (fileList != null && fileList.length == 0) {
                target.delete();
                return;
            } else if (fileList != null && fileList.length > 0) {
                for (String aFile_list : fileList) {
                    File tempF = new File(target.getAbsolutePath() + "/"
                            + aFile_list);

                    if (tempF.isDirectory())
                        deleteTarget(tempF.getAbsolutePath());
                    else if (tempF.isFile()) {
                        tempF.delete();
                    }
                }
            }

            if (target.exists())
                target.delete();
        } else if (!target.delete() && Settings.rootAccess()) {
            RootCommands.deleteRootFileOrDir(target);
        }
    }


    public static void openFile(final Context context, final File target) {
        final String mime = MimeTypes.getMimeType(target);
        final Intent i = new Intent(Intent.ACTION_VIEW);

        if (mime != null) {
            i.setDataAndType(Uri.fromFile(target), mime);
        } else {
            i.setDataAndType(Uri.fromFile(target), "*/*");
        }

        if (context.getPackageManager().queryIntentActivities(i, 0).isEmpty()) {
            Toast.makeText(context, R.string.cantopenfile, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            context.startActivity(i);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.cantopenfile) + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Comment  : 정상적인 이메일 인지 검증.
     */
    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }
}
