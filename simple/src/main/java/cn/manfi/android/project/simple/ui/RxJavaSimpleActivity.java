package cn.manfi.android.project.simple.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.manfi.android.project.base.common.RxDisposedManager;
import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityRxJavaSimpleBinding;
import cn.manfi.android.project.simple.ui.base.SwipeBackAppActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava simple
 * Created by manfi on 2017/9/19.
 */

public class RxJavaSimpleActivity extends SwipeBackAppActivity {

    private ActivityRxJavaSimpleBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_rx_java_simple);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("RxJavaSimpleActivity.onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("RxJavaSimpleActivity.onDestroy");
        RxDisposedManager.dispose(activity);
    }

    @Override
    protected void initView() {
        binding.btnStart.setOnClickListener(v -> rxJava8());
    }

    private void rxJava1() {
        //创建一个上游 Observable：
        Observable<Integer> observable = Observable.create(e -> {
            e.onNext(1);
            e.onNext(2);
            e.onNext(3);
            e.onComplete();
        });
        //创建一个下游 Observer
        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                LogUtil.d(DEBUG, TAG, "subscribe");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                LogUtil.d(DEBUG, TAG, integer + "");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtil.d(DEBUG, TAG, "error");
            }

            @Override
            public void onComplete() {
                LogUtil.d(DEBUG, TAG, "complete");
            }
        };

        //建立连接
        observable.subscribe(observer);
    }

    private void rxJava2() {
        List<String> strList = new ArrayList<>();
        strList.add("a");
        strList.add("b");
        strList.add("c");
        Observable.fromIterable(strList)
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        System.out.println("RxJavaSimpleActivity.onNext:" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void rxJava3() {
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    System.out.println("RxJavaSimpleActivity.doOnSubscribe");
                })
                .doFinally(() -> System.out.println("RxJavaSimpleActivity.rxJava3 doFinally"))
                .doOnTerminate(() -> {
                    System.out.println("RxJavaSimpleActivity.doOnTerminate");
                })
                .subscribeWith(new Observer<Long>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("RxJavaSimpleActivity.onSubscribe:" + d.isDisposed());
                        d.dispose();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("RxJavaSimpleActivity.onNext:" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("RxJavaSimpleActivity.onComplete");
                    }
                });
    }

    private void rxJava4() {
        Observable.intervalRange(0, 10, 0, 1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doFinally(() -> System.out.println("RxJavaSimpleActivity.doFinally"))
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("RxJavaSimpleActivity.onSubscribe");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("RxJavaSimpleActivity.onNext:" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("RxJavaSimpleActivity.onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("RxJavaSimpleActivity.onComplete");
                    }
                });
    }

    private void rxJava5() {
        Observable.just(true)
                .concatMap(b -> {
                    System.out.println("rxJava5 concatMap:" + Thread.currentThread().getName());
                    Observable<Long> ob = Observable.just(1L);
                    ob
                            .subscribe(new Observer<Long>() {

                                @Override
                                public void onSubscribe(Disposable d) {
                                    System.out.println("rxJava5-1 onSubscribe:" + Thread.currentThread().getName());
                                }

                                @Override
                                public void onNext(Long aLong) {
                                    System.out.println("rxJava5-1 onNext:" + Thread.currentThread().getName());
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                    System.out.println("rxJava5-1 onComplete:" + Thread.currentThread().getName());
                                }
                            });
                    return ob;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("rxJava5 onSubscribe:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("rxJava5 onNext:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("rxJava5 onError:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("rxJava5 onComplete:" + Thread.currentThread().getName());
                    }
                });
    }

    private void rxJava6() {
        Observable.just(false)
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Observable.just(1);
                    } else {
                        return Observable.just(2);
                    }
                })
                .subscribe(System.out::println);
    }

    private void rxJava7() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        RxDisposedManager.addDisposed(activity, d);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        System.out.println("RxJavaSimpleActivity:rxJava7 onNext:" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("RxJavaSimpleActivity:rxJava7 onComplete");
                    }
                });
    }

    private void rxJava8() {
        Observable<Integer> ob1 = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onComplete();
        });

        Observable<Integer> ob2 = Observable.create(emitter -> {
            Thread.sleep(3000);
            emitter.onNext(2);
            emitter.onComplete();
        });

        Observable.zip(ob1, ob2, (integer, integer2) -> integer + integer2)
                .timeout(4, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("RxJava8 onSubscribe");
                    }

                    @Override
                    public void onNext(Integer o) {
                        System.out.println("RxJava8 onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("RxJava8 onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("RxJava8 onComplete");
                    }
                });
    }
}
