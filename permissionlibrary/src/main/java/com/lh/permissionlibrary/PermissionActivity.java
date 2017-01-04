package com.lh.permissionlibrary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by home on 2017/1/4.
 * 请求权限的Activity
 */

public class PermissionActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = getIntent().getStringArrayExtra("Permission");
        ActivityCompat.requestPermissions(this,permissions,RxPermission.REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RxPermission.getInstance(this).onRequestPermissionsResult(requestCode,permissions,grantResults);
        finish();
    }
}
