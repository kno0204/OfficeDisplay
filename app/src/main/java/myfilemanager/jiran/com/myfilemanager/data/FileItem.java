package myfilemanager.jiran.com.myfilemanager.data;

import java.io.Serializable;

/**
 * Created by user on 2016-08-04.
 */
public class FileItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public FileItem(){}

    private String id;
    private String name;
    private String path;
    private String tag_id;
    private String tag_color;
    private String size;
    private String reg_date;
    private String favorite;

    private boolean check;

    public void setId(String data) { this.id = data; }
    public String getId() { return id; }

    public void setName(String data) { this.name = data; }
    public String getName() { return name; }

    public void setPath(String data) { this.path = data; }
    public String getPath() { return path; }

    public void setTagId(String data) { this.tag_id = data; }
    public String getTagId() { return tag_id; }

    public void setTagColor(String data) { this.tag_color = data; }
    public String getTagColor() { return tag_color; }

    public void setSize(String data) { this.size = data; }
    public String getSize() { return size; }

    public void setRegDate(String data) { this.reg_date = data; }
    public String getRegDate() { return reg_date; }

    public void setFavorite(String data) { this.favorite = data; }
    public String getFavorite() { return favorite; }

    public void setcheck(boolean data) { this.check = data; }
    public boolean getcheck() { return check; }
}
