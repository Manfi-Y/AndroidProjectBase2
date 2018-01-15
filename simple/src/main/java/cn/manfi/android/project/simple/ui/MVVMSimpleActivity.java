package cn.manfi.android.project.simple.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;

import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityMvvmSimpleBinding;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;
import io.reactivex.annotations.Nullable;

/**
 * MVVMSimple
 * Created by manfi on 2017/10/9.
 */

public class MVVMSimpleActivity extends SwipeBackAppActivity {

    private ActivityMvvmSimpleBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(activity, R.layout.activity_mvvm_simple);
        dataBinding.setViewModel(new MVVMSimpleViewModel(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MVVMSimpleActivity.onDestroy");
    }

    @Override
    protected void initView() {
        dataBinding.setFinish(v -> finish());
        dataBinding.rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        dataBinding.getViewModel().requestAllLineCmd.execute();
    }
}