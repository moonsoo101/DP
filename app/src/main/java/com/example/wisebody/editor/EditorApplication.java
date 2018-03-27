package com.example.wisebody.editor;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

//@ReportsCrashes(
//        resToastText = R.string.error_toast,
//        mode = ReportingInteractionMode.DIALOG,
//        resDialogIcon = android.R.drawable.ic_dialog_info,
//        resDialogText = R.string.error_text,
//        mailTo = "moonsoo@sai42.net" //오류보고를 받을 이메일 주소
//)
public class EditorApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
//        ACRA.init(this);
    }


}