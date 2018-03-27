package com.example.wisebody.editor.ListType;

/**
 * Created by wisebody on 2017. 3. 10..
 */

public class suberData {
    public String nickName, language, description, sub;
    public int heartCount, fanCount;
    public double rating;
    public Boolean Iheart;

    public suberData(String nickName, String language, String description, String sub, int heartCount, int fanCount, double rating, Boolean Iheart)
    {
        this.nickName = nickName; this.language = language; this.description = description; this.sub = sub;
        this.heartCount = heartCount; this.fanCount = fanCount; this.rating = rating;
        this.Iheart = Iheart;
    }

}
