package com.hankkin.compustrading.model;

/**
 * Created by Hankkin on 15/12/6.
 */
public class PersonShow {
    private String title;
    private String content;


    public PersonShow(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
