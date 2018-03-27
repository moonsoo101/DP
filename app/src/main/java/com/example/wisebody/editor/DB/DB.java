package com.example.wisebody.editor.DB;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by wisebody on 2017. 1. 24..
 */

public class DB {
    String link;
    public DB(String php)
    {
        link = "http://drama.wicean.com/test/php/"+php;
    }

    public String uploadSub(String text, String subName)
    {
        try {
            String tsubName = "../sub/"+subName+".srt";
            Log.d("파일이름2",tsubName);
            String data = URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8");
            data += "&" + URLEncoder.encode("subName", "UTF-8") + "=" + URLEncoder.encode(tsubName, "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString().trim();
            }
        catch(Exception e)
        {
            return new String("Exception: " + e.getMessage());
        }
    }
}
