package cn.manfi.android.project.base.mvvm.binding.adapter;

import android.databinding.BindingAdapter;
import android.view.View;

import cn.manfi.android.project.base.mvvm.command.ReplyCommand;

/**
 * ~
 * Created by manfi on 2017/12/21.
 */

public final class ViewBindingAdapter {

    @BindingAdapter({"clickCommand"})
    public static void clickCommand(View view, ReplyCommand<View> clickCommand) {
        view.setOnClickListener(v -> {
            if (clickCommand != null) {
                clickCommand.execute(v);
            }
        });
    }
}
