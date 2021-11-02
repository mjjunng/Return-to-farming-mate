package com.android.become_a_farmer;

public class RecyclerItem {
    private String title;
    private String sub;

    public RecyclerItem() {
    }

    public RecyclerItem(String title, String sub) {
        this.title = title;
        this.sub = sub;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }
}