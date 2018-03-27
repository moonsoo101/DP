package com.example.wisebody.editor.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.wisebody.editor.DB.DB;

/**
 * Created by wisebody on 2017. 1. 24..
 */

public class UploadSub {

//    String index;
//    String start;
//    String end;
    String subName;
    String text;
    String php;

    public UploadSub(String text, String subName)
    {
        this.text = text;
        this.subName = subName;
        php = "uploadSub.php";
    }

    public void uploadToServer(){
        class uploadSub extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                String text = (String) params[0];
                String subName = (String) params[1];
                DB db = new DB(php);
                String result = db.uploadSub(text, subName);
                Log.d("Json",result);
                return result;
            }
        }
        uploadSub task = new uploadSub();
        task.execute(text, subName);
    }
}