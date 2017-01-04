package com.lh.permissionlibrary;

/**
 * Created by home on 2017/1/4.
 * 权限记录类
 */

public class Permission {
    private String name;
    private boolean granted;

    public Permission(String name, boolean granted) {
        this.name = name;
        this.granted = granted;
    }

    public String getName() {
        return name;
    }

    public boolean isGranted() {
        return granted;
    }
}
