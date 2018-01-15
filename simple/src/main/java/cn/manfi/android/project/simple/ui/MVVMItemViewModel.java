package cn.manfi.android.project.simple.ui;

import android.content.Context;
import android.databinding.ObservableField;

import cn.manfi.android.project.base.mvvm.base.ViewModel;
import cn.manfi.android.project.base.mvvm.command.ReplyCommand;

/**
 * ~
 * Created by manfi on 2017/10/12.
 */

public class MVVMItemViewModel implements ViewModel {

    private Context context;

    public String item;

    public final ObservableField<String> name = new ObservableField<>();

    public ReplyCommand<String> itemClickCommand = new ReplyCommand<>(
            s -> System.out.println("Item点击:" + s)
    );

    public MVVMItemViewModel(Context context, String item) {
        this.context = context;
        this.item = item;
        name.set(this.item);
    }
}
