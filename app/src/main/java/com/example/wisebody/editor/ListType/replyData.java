package com.example.wisebody.editor.ListType;

import java.util.ArrayList;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class replyData extends reply {
    public int rereplycount;
    public boolean visibilityOfChildItems = false;
    public ArrayList<rereplyData> unvisibleChildItems = new ArrayList<>();
    public replyData(String name, String text, int heartcount, int day, Boolean Iheart, int viewType, ArrayList<rereplyData> unvisibleChildItems)
    {
        this.name = name;this.text =text;
        this.heartcount = heartcount;
        this.day = day;
        this.Iheart = Iheart;
        this.viewType = viewType;
        this.unvisibleChildItems = unvisibleChildItems;
        if(unvisibleChildItems!=null)
            rereplycount = unvisibleChildItems.size();
    }

}
