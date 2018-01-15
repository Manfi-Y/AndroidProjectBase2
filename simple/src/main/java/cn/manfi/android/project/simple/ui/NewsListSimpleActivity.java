package cn.manfi.android.project.simple.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityNewsListBinding;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;

/**
 * 新闻列表Simple
 * Created by manfi on 2017/12/22.
 */

public class NewsListSimpleActivity extends SwipeBackAppActivity {

    private ActivityNewsListBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_list);
        dataBinding.setViewModel(new NewsListViewModel(this));
    }

    @Override
    protected void initView() {
        initToolbar();

        dataBinding.srl.setColorSchemeResources(R.color.colorAccent);

        dataBinding.getViewModel().requestNews("5");
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
