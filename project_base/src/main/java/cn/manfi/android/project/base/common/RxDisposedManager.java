package cn.manfi.android.project.base.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxDisposedManager {

    private static final Object DERELICT = new Object();
    private static HashMap<Object, CompositeDisposable> compositeDisposableHashMap = new HashMap<>();

    private RxDisposedManager() {
    }

    public static synchronized boolean addDisposed(@Nullable Object host, @NonNull Disposable disposable) {
        CompositeDisposable cd = compositeDisposableHashMap.get(host == null ? DERELICT : host);
        if (cd == null) {
            cd = new CompositeDisposable();
            compositeDisposableHashMap.put(host == null ? DERELICT : host, cd);
        }
        return cd.add(disposable);
    }

    /**
     * @param host 如果host为空，则dispose {@link #DERELICT}
     */
    public static synchronized void dispose(@Nullable Object host) {
        CompositeDisposable cd = compositeDisposableHashMap.get(host == null ? DERELICT : host);
        if (cd != null) {
            cd.dispose();
            compositeDisposableHashMap.remove(host == null ? DERELICT : host);
        }
    }

    public static synchronized void disposeAll() {
        for (CompositeDisposable compositeDisposable : compositeDisposableHashMap.values()) {
            compositeDisposable.dispose();
        }
        compositeDisposableHashMap.clear();
    }
}
