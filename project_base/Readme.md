网络状态监听：
需要权限
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
在需要的地方注册
App.getInstance().registerNetworkMessage(activity, isNetworkConn -> dataBinding.getViewModel().showToast("网络连接：" + isNetworkConn));
在适当的地方注销
App.getInstance().unregisterNetworkMessage(activity);