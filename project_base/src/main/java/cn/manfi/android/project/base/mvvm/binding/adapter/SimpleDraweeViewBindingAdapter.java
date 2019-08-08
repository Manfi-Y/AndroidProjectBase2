package cn.manfi.android.project.base.mvvm.binding.adapter;

import androidx.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Fresco SimpleDraweeView Binding Adapter
 * Created by manfi on 2017/12/22.
 */

public final class SimpleDraweeViewBindingAdapter {

    @BindingAdapter({"imgUri"})
    public static void setImageUri(SimpleDraweeView simpleDraweeView, String uri) {
        if (!TextUtils.isEmpty(uri)) {
            simpleDraweeView.setImageURI(Uri.parse(uri));
        }
    }
}
