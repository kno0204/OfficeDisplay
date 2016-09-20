package myfilemanager.jiran.com.myfilemanager.data;

import java.io.Serializable;

/**
 * Created by user on 2016-08-04.
 */
public class TagItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public TagItem(){}

    private String id;
    private String name;
    private String color;

    public void setId(String data) { this.id = data; }
    public String getId() { return id; }

    public void setName(String data) { this.name = data; }
    public String getName() { return name; }

    public void setColor(String data) { this.color = data; }
    public String getColor() { return color; }
}
