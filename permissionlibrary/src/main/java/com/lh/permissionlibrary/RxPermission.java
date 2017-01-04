package com.lh.permissionlibrary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by home on 2017/1/4.
 * RxPermission主类
 */

public class RxPermission {

    public static final int REQUEST_CODE = 100;

    private WeakReference<Context> mContext;
    private Map<String, PublishSubject<Permission>> mSubjects;

    private static RxPermission mInstance;

    private RxPermission(Context context) {
        mContext = new WeakReference<>(context);
        mSubjects = new HashMap<>();
    }

    public static RxPermission getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RxPermission(context.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * @param permissions 权限名
     * @return 如果有一个权限未通过则返回false
     */
    public Observable<Boolean> requset(String... permissions) {
        return requsetEach(permissions).buffer(permissions.length).flatMap(new Func1<List<Permission>, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(List<Permission> permissions) {
                for (Permission permission : permissions) {
                    if (!permission.isGranted()) {
                        return Observable.just(false);
                    }
                }
                return Observable.just(true);
            }
        });
    }

    /**
     * @param permissions 权限名
     * @return 返回每一个权限的获取状态
     */
    public Observable<Permission> requsetEach(String... permissions) {
        if (mContext.get() == null) {
            throw new IllegalStateException("RxPermissions.Context missing");
        }
        List<Observable<Permission>> observables = new ArrayList<>();
        List<String> needPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext.get(), permission) == PackageManager.PERMISSION_DENIED) {
                needPermissions.add(permission);
            } else {
                observables.add(Observable.just(new Permission(permission, true)));
            }
        }
        for (int i = 0; i < needPermissions.size(); i++) {
            PublishSubject<Permission> subject = PublishSubject.create();
            mSubjects.put(needPermissions.get(i), subject);
            observables.add(subject);
        }
        if (needPermissions.size() > 0) {
            Intent intent = new Intent(mContext.get(), PermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Permission", needPermissions.toArray(new String[needPermissions.size()]));
            mContext.get().startActivity(intent);
        }
        return Observable.concat(observables);
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
                if (subject == null) {
                    throw new IllegalStateException("RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                }
                mSubjects.remove(permissions[i]);
                subject.onNext(new Permission(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED));
                subject.onCompleted();
            }
        }
    }

}