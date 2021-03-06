package cn.manfi.android.project.base;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.manfi.android.project.base.common.Constant;
import cn.manfi.android.project.base.common.NetworkUtil;
import cn.manfi.android.project.base.common.PrefUtil;
import cn.manfi.android.project.base.mvvm.messenger.Messenger;
import io.reactivex.functions.Consumer;

/**
 * Base Application
 * <p>
 * 1.Holding Activities and finish them when app exit.
 * 2.Broadcast Network changed and send message to activities.
 * </p>
 * Created by manfi on 2018/1/22.
 */

public class BaseApp extends Application {

    protected List<Activity> activityList = new ArrayList<>();
    protected BaseApp.NetworkBroadcast networkBroadcast;

    public void onCreate() {
        super.onCreate();
        PrefUtil.init(this);
    }

    /**
     * 添加受到管理的Activity
     * <p>
     * 当有一个以上Activity（BaseActivity已默认调用，其他Activity自行调用）就注册网络监听广播
     * </p>
     *
     * @param activity ~
     */
    public void addActivity(Activity activity) {
        this.activityList.add(activity);
        if (this.networkBroadcast == null && !this.activityList.isEmpty()) {
            this.networkBroadcast = new BaseApp.NetworkBroadcast(this);
            this.registerReceiver(this.networkBroadcast, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    /**
     * 移除受到管理的Activity
     * <p>
     * 当所有Activity（BaseActivity已默认调用，其他Activity自行调用）移除，就注销网络监听广播
     * </p>
     *
     * @param activity ~
     */
    public void removeActivity(Activity activity) {
        this.activityList.remove(activity);
        if (this.networkBroadcast != null && this.activityList.isEmpty()) {
            this.unregisterReceiver(this.networkBroadcast);
            this.networkBroadcast = null;
        }
    }

    /**
     * Finish所有Activity退出应用
     */
    public void exitApp() {
        Iterator var1 = this.activityList.iterator();

        while (var1.hasNext()) {
            Activity activity = (Activity) var1.next();
            activity.finish();
        }

        this.activityList.clear();
        if (this.networkBroadcast != null) {
            this.unregisterReceiver(this.networkBroadcast);
            this.networkBroadcast = null;
        }
    }

    /**
     * 注册网络状态改变消息
     *
     * @param recipient 注册对象
     * @param action    响应Action
     */
    public void registerNetworkMessage(Object recipient, Consumer<Boolean> action) {
        Messenger.getDefault().register(recipient, Constant.MESSENGER_IS_NETWORK_CONNECT, Boolean.class, action);
    }

    /**
     * 注销网路哦状态改变消息
     *
     * @param recipient 注销对象
     */
    public void unregisterNetworkMessage(Object recipient) {
        Messenger.getDefault().unregister(recipient);
    }

    class NetworkBroadcast extends BroadcastReceiver {

        private boolean isHasNetwork;

        public NetworkBroadcast(Context context) {
            this.isHasNetwork = NetworkUtil.isNetworkConnected(context);
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null
                    && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") && NetworkUtil.isNetworkConnected(context) != this.isHasNetwork) {
                this.isHasNetwork = NetworkUtil.isNetworkConnected(context);
                final boolean isNetworkConn = this.isHasNetwork;
                try {
                    Messenger.getDefault().send(isNetworkConn, Constant.MESSENGER_IS_NETWORK_CONNECT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
