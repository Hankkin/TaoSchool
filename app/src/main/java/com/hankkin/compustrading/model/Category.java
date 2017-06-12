package com.hankkin.compustrading.model;


import cn.bmob.v3.BmobObject;

/**
 * Created by Hankkin on 15/11/29.
 */
public class Category extends BmobObject{
    private Integer id;
    private String name;
    private String desc;
    private int pid;

    public Category(Integer id,String name, String desc, int pid) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }



}
