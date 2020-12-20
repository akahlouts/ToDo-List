package com.akahlouts.todo_list.model;

public class ListItem {
    private String listName;

    public ListItem() {
    }

    public ListItem(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
