package com.example.wisebody.editor.ListType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisebody on 2017. 2. 9..
 */

public class coverData {
    public ArrayList<String> title;
    public ArrayList<String> cp;
    public String name;
    public int star;
    public int heart;

    public coverData(ArrayList<String> title, ArrayList<String> cp, String name, int star, int heart)
    {
        this.title = title;
        this.cp = cp;
        this.name = name;
        this.star = star;
        this.heart = heart;
    }
    @Override
    public boolean equals(Object o){
        coverData s = (coverData) o;
        if(title.equals(s.title))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
