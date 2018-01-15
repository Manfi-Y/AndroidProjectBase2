package cn.manfi.android.project.simple.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityDataBindSimpleBinding;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;

/**
 * Data Binding simple
 */
public class DataBindSimpleActivity extends SwipeBackAppActivity {

    private ActivityDataBindSimpleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_data_bind_simple);
    }

    @Override
    protected void initView() {
        binding.setNum(1);

        binding.setAddNum(view -> binding.setNum(binding.getNum() + 1));

        binding.btn.setOnLongClickListener(view -> {
            binding.setNum(binding.getNum() + 10);
            return true;
        });
    }
}
