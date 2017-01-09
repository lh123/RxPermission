package com.lh.rxpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lh.permissionlibrary.Permission;
import com.lh.permissionlibrary.RxPermission;

import io.reactivex.functions.Consumer;

/**
 * Created by home on 2017/1/4.
 */

public class MainActivity extends AppCompatActivity {

    private Button mBtnRequest;
    private TextView mTvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnRequest = (Button) findViewById(R.id.requset_permission);
        mTvResult = (TextView) findViewById(R.id.permission_result);

        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxPermission.getInstance(MainActivity.this)
                        .requsetEach(Manifest.permission.READ_SMS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                mTvResult.append(permission.getName());
                                mTvResult.append(":");
                                mTvResult.append(permission.isGranted()+"\n");
                            }
                        });
            }
        });

    }
}
