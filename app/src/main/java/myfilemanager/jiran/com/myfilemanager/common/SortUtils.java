package myfilemanager.jiran.com.myfilemanager.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import myfilemanager.jiran.com.myfilemanager.data.FileItem;

public class SortUtils {

    private SortUtils() {}

    public static final int SORT_ALPHA = 0;
    public static final int SORT_TYPE = 1;
    public static final int SORT_SIZE = 2;
    public static final int SORT_DATE = 3;

    public static void sortList(ArrayList<FileItem> content,
                                String current, int sort_type) {
        int len = content != null ? content.size() : 0;

        if (len == 0)
            return;

        int index = 0;
        FileItem[] items = new FileItem[len];
        content.toArray(items);

        //switch (Settings.getSortType()) {
        switch (sort_type) {
            case SORT_ALPHA:
                Arrays.sort(items, Comparator_ALPH);
                content.clear();

                Collections.addAll(content, items);
                break;
            case SORT_SIZE:
                Arrays.sort(items, Comparator_SIZE);
                content.clear();

                for (FileItem a : items) {
                    if (new File(current + "/" + a.getName()).isDirectory())
                        content.add(index++, a);
                    else
                        content.add(a);
                }
                break;
            case SORT_TYPE:
                Arrays.sort(items, Comparator_TYPE);
                content.clear();

                for (FileItem a : items) {
                    if (new File(current + "/" + a.getName()).isDirectory())
                        content.add(index++, a);
                    else
                        content.add(a);
                }
                break;

            case SORT_DATE:
                Arrays.sort(items, Comparator_DATE);
                content.clear();

                for (FileItem a : items) {
                    if (new File(current + "/" + a.getName()).isDirectory())
                        content.add(index++, a);
                    else
                        content.add(a);
                }
                break;
        }

//        if (Settings.reverseListView()) {
//            Collections.reverse(content);
//        }
    }

    private static final Comparator<? super FileItem> Comparator_ALPH = new Comparator<FileItem>() {

        @Override
        public int compare(FileItem arg0, FileItem arg1) {
            File a = new File(arg0.getPath());
            File b = new File(arg1.getPath());

            if (a.isDirectory() && b.isDirectory()) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }

            if (a.isDirectory()) {
                return -1;
            }

            if (b.isDirectory()) {
                return 1;
            }

            String first = "";
            String second = "";
            first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
            second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
            return first.toLowerCase().compareTo(second.toLowerCase());
        }
    };

    private final static Comparator<? super FileItem> Comparator_SIZE = new Comparator<FileItem>() {

        @Override
        public int compare(FileItem arg0, FileItem arg1) {
            File a = new File(arg0.getPath());
            File b = new File(arg1.getPath());

            if (a.isDirectory() && b.isDirectory()) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }

            if (a.isDirectory()) {
                return -1;
            }

            if (b.isDirectory()) {
                return 1;
            }

            final long lenA = a.length();
            final long lenB = b.length();

            if (lenA == lenB) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }

            if (lenA < lenB) {
                return -1;
            }

            return 1;
        }
    };

    public static String getExtension(String name) {
        String ext;

        if (name.lastIndexOf(".") == -1) {
            ext = "";

        } else {
            int index = name.lastIndexOf(".");
            ext = name.substring(index + 1, name.length());
        }
        return ext;
    }

    private final static Comparator<? super FileItem> Comparator_TYPE = new Comparator<FileItem>() {

        @Override
        public int compare(FileItem arg0, FileItem arg1) {
            File a = new File(arg0.getPath());
            File b = new File(arg1.getPath());

            if (a.isDirectory() && b.isDirectory()) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }

            if (a.isDirectory()) {
                return -1;
            }

            if (b.isDirectory()) {
                return 1;
            }

            final String extA = getExtension(a.getName());
            final String extB = getExtension(b.getName());

            if (extA.isEmpty() && extB.isEmpty()) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }

            if (extA.isEmpty()) {
                return -1;
            }

            if (extB.isEmpty()) {
                return 1;
            }

            final int res = extA.compareTo(extB);
            if (res == 0) {
                String first = "";
                String second = "";
                first = arg0.getName().startsWith(".") ? arg0.getName().replaceFirst(".", "") : arg0.getName();
                second = arg1.getName().startsWith(".") ? arg1.getName().replaceFirst(".", "") : arg1.getName();
                return first.toLowerCase().compareTo(second.toLowerCase());
            }
            return res;
        }
    };

    private final static Comparator<? super FileItem> Comparator_DATE = new Comparator<FileItem>() {

        @Override
        public int compare(FileItem arg0, FileItem arg1) {
            File a = new File(arg0.getPath());
            File b = new File(arg1.getPath());

            if (a.isDirectory() && b.isDirectory()) {
                Long first = a.lastModified();
                Long second = b.lastModified();

                return -first.compareTo(second);
            }

            if (a.isDirectory()) {
                return -1;
            }

            if (b.isDirectory()) {
                return 1;
            }

//            Long first = new File(arg0.getPath()).lastModified();
//            Long second = new File(arg1.getPath()).lastModified();
            Long first = a.lastModified();
            Long second = b.lastModified();

            return -first.compareTo(second);
        }
    };
}