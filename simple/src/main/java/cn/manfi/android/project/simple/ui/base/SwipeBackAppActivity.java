package cn.manfi.android.project.simple.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.manfi.android.project.base.common.permission.AppSettingsDialog;
import cn.manfi.android.project.base.common.permission.PermissionUtils;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.subjects.BehaviorSubject;
import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * 可以滑动返回 Base Activity
 * Created by manfi on 2017/9/20.
 */

public abstract class SwipeBackAppActivity extends SwipeBackActivity implements LifecycleProvider<ActivityEvent> {

    protected final String TAG = getClass().getSimpleName();
    protected final boolean DEBUG = true;

    protected Activity activity;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        activity = this;
        getWindow().getDecorView().post(this::initView);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    protected abstract void initView();

    /**
     * 询问是否需要到系统设置自行打开不允许而且不再询问的权限
     *
     * @param perms ~
     */
    public void askPermanentlyDeniedPermission(String... perms) {
        String[] notGrantPermList = PermissionUtils.checkPermissions(activity, perms);
        final List<String> permanentlyDeniedPermList = new ArrayList<>();
        if (notGrantPermList != null) {
            permanentlyDeniedPermList.addAll(Arrays.asList(notGrantPermList));
        }
        if (permanentlyDeniedPermList.size() > 0) {
            List<String> needGrantPermissionGroupName = PermissionUtils.loadPermissionsGroupName(getApplicationContext(), permanentlyDeniedPermList);
            if (needGrantPermissionGroupName != null && !needGrantPermissionGroupName.isEmpty()) {
                PermissionUtils.onPermissionsPermanentlyDenied(
                        this,
                        PermissionUtils.toPermisionsGroupString(needGrantPermissionGroupName),
                        "需要在系统权限设置授予以下权限",
                        getString(android.R.string.ok),
                        getString(android.R.string.cancel),
                        (dialog, which) -> permanentlyDeniedPermissionDenied(permanentlyDeniedPermList),
                        AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE);
            }
        }
    }

    /**
     * 不允许权限，而且取消到系统自行打开
     *
     * @param permanentlyDeniedPerms 不允许的权限
     */
    protected void permanentlyDeniedPermissionDenied(List<String> permanentlyDeniedPerms) {

    }
}
