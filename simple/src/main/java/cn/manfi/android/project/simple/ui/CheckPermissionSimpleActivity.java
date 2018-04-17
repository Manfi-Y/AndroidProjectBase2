package cn.manfi.android.project.simple.ui;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.permission.PermissionUtils;
import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityCheckPermissionBinding;

/**
 * Check Permission Simple Activity
 * Created by manfi on 2018/1/3.
 */

public class CheckPermissionSimpleActivity extends BaseActivity {

    private ActivityCheckPermissionBinding binding;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_check_permission);

        binding.setViewModel(new CheckPermissionViewModel(activity));
        rxPermissions = new RxPermissions(activity);
    }

    @Override
    protected void initView() {
        initToolbar();
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        RxView.clicks(binding.btnCheck).throttleFirst(1, TimeUnit.SECONDS)
                .concatMap(o -> rxPermissions.request(perms))
                .subscribe(granted -> {
                    System.out.println("CheckPermissionSimpleActivity.subscribe");
                    if (granted) {
                        binding.getViewModel().showToast("所有权限允许");
                    } else if (!PermissionUtils.somePermissionsNeedAskAgain(activity, perms)) {
                        askPermanentlyDeniedPermission(PermissionUtils.hasPermissions(activity, perms));
                    }
                });
    }

    void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("News List Simple");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
