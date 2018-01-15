package cn.manfi.android.project.simple.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityViewpagerSimpleBinding;

/**
 * ViewPager Simple Activity
 * Created by manfi on 2018/1/2.
 */

public class ViewPagerSimpleActivity extends BaseActivity {

    private ActivityViewpagerSimpleBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(activity, R.layout.activity_viewpager_simple);
        dataBinding.setViewModel(new ViewPagerSimpleViewModel(activity));
    }

    @Override
    protected void initView() {
        initToolbar();

        dataBinding.stl.setViewPager(dataBinding.vp);
    }

    void initToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("TabLayout ViewPager Simple");
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
