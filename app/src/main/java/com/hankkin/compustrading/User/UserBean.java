package com.hankkin.myapplication.User;

import cn.bmob.v3.BmobObject;

/**
 * Created by Hankkin on 15/11/28.
 */
public class UserBean extends BmobObject {
    private String name;
    private String tel;
    private String icon_url;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
