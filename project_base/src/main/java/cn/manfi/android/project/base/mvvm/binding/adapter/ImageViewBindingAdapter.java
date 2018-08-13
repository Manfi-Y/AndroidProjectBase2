package cn.manfi.android.project.base.mvvm.binding.adapter;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import cn.manfi.android.project.base.mvvm.command.ReplyCommand;

/**
 * ImageView
 * Created by manfi on 2017/12/22.
 */

public final class ImageViewBindingAdapter {

    /**
     * 利用Fresco加载图片到ImageView
     *
     * @param imageView           ~
     * @param uri                 图片uri
     * @param placeholderImageRes 占位图
     * @param width               图片宽度
     * @param height              图片高度
     * @param onSuccessCommand    成功回调命令
     * @param onFailureCommand    失败回调命令
     */
    @BindingAdapter(value = {"imgUri", "placeholderImageRes", "request_width", "request_height", "onSuccessCommand", "onFailureCommand"}, requireAll = false)
    public static void loadImage(final ImageView imageView, String uri,
                                 @DrawableRes int placeholderImageRes,
                                 int width, int height,
                                 final ReplyCommand<Bitmap> onSuccessCommand,
                                 final ReplyCommand<DataSource<CloseableReference<CloseableImage>>> onFailureCommand) {
        imageView.setImageResource(placeholderImageRes);
        if (!TextUtils.isEmpty(uri)) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri));
            if (width > 0 && height > 0) {
                builder.setResizeOptions(new ResizeOptions(width, height));
            }
            ImageRequest request = builder.build();
            DataSource<CloseableReference<CloseableImage>>
                    dataSource = imagePipeline.fetchDecodedImage(request, imageView.getContext());
            dataSource.subscribe(new BaseBitmapDataSubscriber() {

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    if (onFailureCommand != null) {
                        onFailureCommand.execute(dataSource);
                    }
                }

                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    if (onSuccessCommand != null) {
                        onSuccessCommand.execute(bitmap);
                    }
                }
            }, UiThreadImmediateExecutorService.getInstance());
        }
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }
}
