package util;

import android.app.Application;

public class ToDoListApi extends Application {
    private String username;
    private String userId;
    private static ToDoListApi instance;

    public static ToDoListApi getInstance() {
        if (instance == null)
            instance = new ToDoListApi();
        return instance;
    }

    public ToDoListApi() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
