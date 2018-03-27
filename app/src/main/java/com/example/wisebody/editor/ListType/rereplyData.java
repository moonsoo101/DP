package com.example.wisebody.editor.ListType;

import java.util.ArrayList;

/**
 * Created by wisebody on 2017. 2. 15..
 */

public class rereplyData extends reply {
    public rereplyData(String name, String text, int heartcount, int day, Boolean Iheart, int viewType)
    {
        this.name = name;this.text =text;
        this.heartcount = heartcount;
        this.day = day;
        this.Iheart = Iheart;
        this.viewType = viewType;
    }

}
