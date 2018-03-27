package com.example.wisebody.editor.ListType;

import android.util.Log;

/**
 * Created by wisebody on 2017. 1. 6..
 */

public class seqData implements Comparable<seqData> {
    public String start;
    public String end;
    public String time;
    public String text;
    public String id;
    public seqData(String start, String end, String id)
    {
        this.start = start;
        this.end = end;
        this.id = id;
        timeFormat();
    }
    @Override
    public int compareTo(seqData si) {

        if (Float.parseFloat(this.start) <  Float.parseFloat(si.start)) {   // 내림차순 , 오름차순으로 하려면 < 으로~
            return -1;
        } else if (Float.parseFloat(this.start) ==  Float.parseFloat(si.start)) {
            return 0;
        } else {
            return 1;
        }

    }
    public void timeFormat()
    {
        time = String.format("%02d",(int)(Float.parseFloat(start)/60))+":"+String.format("%02.1f",Float.parseFloat(start)%60)+"~"+String.format("%02d",(int)(Float.parseFloat(end)/60))+":"+String.format("%02.1f",Float.parseFloat(end)%60);
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof seqData)) return false;
        seqData s = (seqData) o;
        if(start.equals(s.start))
        {
            return true;
        }
        else {
            return false;
        }
    }

}
