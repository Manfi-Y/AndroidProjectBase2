package cn.manfi.android.project.simple.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        binding.toolbar.setTitle(R.string.app_name);
        binding.setClick(this);

        rxPermissions = new RxPermissions(activity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_DataBindingSimple:
                startActivity(new Intent(activity, DataBindSimpleActivity.class));
                break;
            case R.id.layout_RxJavaSimple:
                startActivity(new Intent(activity, RxJavaSimpleActivity.class));
                break;
            case R.id.layout_RetrofitSimple:
                startActivity(new Intent(activity, RetrofitSimpleActivity.class));
                break;
            case R.id.layout_MVVMSimple:
                startActivity(new Intent(activity, MVVMSimpleActivity.class));
                break;
            case R.id.layout_RGDNewsListSimple:
                startActivity(new Intent(activity, NewsListSimpleActivity.class));
                break;
            case R.id.layout_CheckPermissionSimple:
                startActivity(new Intent(activity, CheckPermissionSimpleActivity.class));
                break;
            case R.id.layout_ViewPagerSimple:
                startActivity(new Intent(activity, ViewPagerSimpleActivity.class));
                break;
        }
    }
}
