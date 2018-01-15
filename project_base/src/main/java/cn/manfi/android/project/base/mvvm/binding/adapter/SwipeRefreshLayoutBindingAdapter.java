package cn.manfi.android.project.base.mvvm.binding.adapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

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
