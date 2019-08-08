package cn.manfi.android.project.base.mvvm.binding.adapter;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cn.manfi.android.project.base.mvvm.command.ReplyCommand;

/**
 * ~
 * Created by manfi on 2017/12/21.
 */

public final class SwipeRefreshLayoutBindingAdapter {

    @BindingAdapter({"onRefreshCommand"})
    public static void onRefreshCommand(SwipeRefreshLayout swipeRefreshLayout, final ReplyCommand onRefreshCommand) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (onRefreshCommand != null) {
                onRefreshCommand.execute();
            }
        });
    }
}
