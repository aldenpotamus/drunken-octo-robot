package com.example.aldenroberts.testproject;

/**
 * Created by smithbrent on 12/9/14.
 */
public class Calendar {

    private Integer id;
    private String name;
    private String displayName;

    public Calendar(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
