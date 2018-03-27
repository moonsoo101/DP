package com.example.wisebody.editor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wisebody.editor.Util.Upload;

import java.io.File;
import java.io.IOException;

/**
 * Created by wisebody on 2017. 3. 2..
 */

public class Record extends AppCompatActivity {
    MediaRecorder recorder;
    Button record, stop, play, upload;
    EditText indexInput;
    String path;
    int index = 0;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        record = (Button) findViewById(R.id.start);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRec();
            }
        });
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRec();
            }
        });
        indexInput = (EditText)findViewById(R.id.indexInput);
        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // 오디오를 플레이 하기위해 MediaPlayer 객체 player를 생성한다.
                    stopRec();
                    MediaPlayer player = new MediaPlayer();

                    // 재생할 오디오 파일 저장위치를 설정
                    player.setDataSource("http://203.233.111.62/test/Dubbing/test"+indexInput.getText().toString()+".3gp");
                    // 웹상에 있는 오디오 파일을 재생할때
                    // player.setDataSource(Audio_Url);

                    // 오디오 재생준비,시작
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"파일이 없음",Toast.LENGTH_SHORT).show();
                    Log.e("SampleAudioRecorder", "Audio play failed.", e);
                }
            }
        });
        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               uploadVideo();
                //asyncTask 쓰기
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission();

    }

    public void startRec(){
        try {
//            ((subEditor)context).preview.stopPlayback();
//            ((subEditor)context).preview.reset();
//            ((subEditor)context).preview.release();
//            ((subEditor)context).preview = null;
            recorder = new MediaRecorder();
            File file= Environment.getExternalStorageDirectory();
//갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다.
            path=file.getAbsolutePath()+"/test"+index+".3gp";
            Log.d("Recored",path);

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioSamplingRate(44100);
            recorder.setAudioEncodingBitRate(96000);
//이것은 코덱을 설정하는 것이라고 생각하면된다.
            recorder.setOutputFile(path);
//저장될 파일을 저장한
            recorder.prepare();
            recorder.start();
//시작하면된다.
            Toast.makeText(this, "start Record", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void stopRec(){
        if(recorder!=null) {
            recorder.stop();
//멈추는 것이다.
            recorder.release();
            recorder = null;
            Toast.makeText(this, "stop Record", Toast.LENGTH_LONG).show();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
    private void uploadVideo() {
        class UploadVideo extends AsyncTask<String, Void, String> {
            ProgressDialog uploading = new ProgressDialog(getApplicationContext());
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               stopRec();
                uploading = ProgressDialog.show(Record.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                if(s.equals("upload success")) {
                    index++;
                    Toast.makeText(getApplicationContext(),"upload success",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"upload fail",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(String... params) {
                String msg = null;
                Upload u = new Upload();
                    msg = u.uploadVideo(path);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }
}
