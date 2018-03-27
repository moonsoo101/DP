package com.example.wisebody.editor.ListType;

/**
 * Created by wisebody on 2017. 2. 18..
 */

public class recVidData {

    public String cp;
    public String title, nickName, language;
    public int dur, part, heartCount;
    public  boolean Iheart;

    public recVidData(String cp, String title, String nickName, String language, int dur, int part, int heartCouunt, boolean Iheart) {
        this.cp = cp;this.title = title; this.nickName = nickName; this.language = language;
        this.dur = dur;this.part = part; this.heartCount = heartCouunt;
        this.Iheart = Iheart;
    }

}
