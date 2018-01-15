package cn.manfi.android.project.base.common.net.retrofit;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.manfi.android.project.base.common.log.LogUtil;
import cn.manfi.android.project.base.common.net.retrofit.parser.DefaultUrlParser;
import cn.manfi.android.project.base.common.net.retrofit.parser.UrlParser;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * RetrofitUrlManager 以简洁的 Api ,让 Retrofit 不仅支持多 BaseUrl
 * 还可以在 App 运行时动态切换任意 BaseUrl,在多 BaseUrl 场景下也不会影响到其他不需要切换的 BaseUrl
 * Created by manfi on 2017/9/20.
 */

public class RetrofitManager {

    private static final String TAG = "RetrofitManager";
    private static final boolean DEBUG = false;

    private static final String DOMAIN_NAME = "Domain-Name";
    private static final String GLOBAL_DOMAIN_NAME = "Global-Domain-Name";
    public static final String DOMAIN_NAME_HEADER = DOMAIN_NAME + ": ";

    private boolean isRun = true; //默认开始运行,可以随时停止运行,比如你在 App 启动后已经不需要在动态切换 baseurl 了
    private final Map<String, HttpUrl> mDomainNameHub = new HashMap<>();
    private final Interceptor mInterceptor;
    private final List<UrlChangeListener> mListeners = new ArrayList<>();
    private final Map<String, OnRequestProcess> mOnRequestProcessMap = new HashMap<>();
    private UrlParser mUrlParser;

    private RetrofitManager() {
        setUrlParser(new DefaultUrlParser());
        mInterceptor = chain -> {
            if (!isRun()) // 可以在 App 运行时,随时通过 setRun(false) 来结束本管理器的运行,
                return chain.proceed(chain.request());
            return chain.proceed(processRequest(chain.request()));
        };
    }

    private static class RetrofitUrlManagerHolder {

        private static final RetrofitManager INSTANCE = new RetrofitManager();
    }

    public static final RetrofitManager getInstance() {
        return RetrofitUrlManagerHolder.INSTANCE;
    }

    /**
     * 将 {@link OkHttpClient.Builder} 传入,配置一些本管理器需要的参数
     *
     * @param builder ~
     *
     * @return ~
     */
    public OkHttpClient.Builder with(OkHttpClient.Builder builder) {
        return builder.addInterceptor(mInterceptor);
    }

    /**
     * 对 {@link Request} 进行一些必要的加工
     *
     * @param request ~
     *
     * @return ~
     */
    private Request processRequest(Request request) {
        Request newRequest = processRequestUrl(request);
        newRequest = processRequestHeaders(newRequest);
        return newRequest;
    }

    /**
     * 动态改变url
     *
     * @param request ~
     *
     * @return ~
     */
    private Request processRequestUrl(Request request) {
        Request.Builder newBuilder = request.newBuilder();

        String domainName = obtainDomainNameFromHeaders(request);

        HttpUrl httpUrl;

        // 如果有 header，获取 header 中配置的url，否则检查全局的 BaseUrl，未找到则为null
        if (!TextUtils.isEmpty(domainName)) {
            httpUrl = fetchDomain(domainName);
            newBuilder.removeHeader(DOMAIN_NAME);
        } else {
            httpUrl = fetchDomain(GLOBAL_DOMAIN_NAME);
        }

        if (null != httpUrl) {
            HttpUrl newUrl = mUrlParser.parseUrl(httpUrl, request.url());
            LogUtil.d(DEBUG, TAG, "New Url is { " + newUrl.toString() + " } , Old Url is { " + request.url().toString() + " }");

            Object[] listeners = listenersToArray();
            if (listeners != null) {
                for (Object listener : listeners) {
                    ((UrlChangeListener) listener).onUrlChange(newUrl, request.url()); // 通知监听器此 Url 的 BaseUrl 已被改变
                }
            }

            return newBuilder
                    .url(newUrl)
                    .build();
        }

        return newBuilder.build();
    }

    /**
     * 根据BaseUrl处理请求头
     *
     * @param request ~
     *
     * @return ~
     */
    private Request processRequestHeaders(Request request) {
        String baseUrl = request.url().host();
        Request newRequest = null;
        OnRequestProcess onRequestProcess = mOnRequestProcessMap.get(baseUrl);
        if (onRequestProcess != null) {
            newRequest = onRequestProcess.onProcessRequest(request);
        }
        return newRequest == null ? request : newRequest;
    }

    /**
     * 管理器是否在运行
     *
     * @return ~
     */
    public boolean isRun() {
        return this.isRun;
    }

    /**
     * 控制管理器是否运行,在每个域名地址都已经确定,不需要再动态更改时可设置为 false
     *
     * @param run ~
     */
    public void setRun(boolean run) {
        this.isRun = run;
    }

    /**
     * 全局动态替换 BaseUrl，优先级： Header中配置的url > 全局配置的url
     * 除了作为备用的 BaseUrl ,当你项目中只有一个 BaseUrl ,但需要动态改变
     * 这种方式不用在每个接口方法上加 Header,也是个很好的选择
     *
     * @param url ~
     */
    public void setGlobalDomain(String url) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(GLOBAL_DOMAIN_NAME, Utils.checkUrl(url));
        }
    }

    /**
     * 获取全局 BaseUrl
     */
    public HttpUrl getGlobalDomain() {
        return mDomainNameHub.get(GLOBAL_DOMAIN_NAME);
    }

    /**
     * 移除全局 BaseUrl
     */
    public void removeGlobalDomain() {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(GLOBAL_DOMAIN_NAME);
        }
    }

    /**
     * 存放 Domain 的映射关系
     *
     * @param domainName ~
     * @param domainUrl  ~
     */
    public void putDomain(String domainName, String domainUrl) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.put(domainName, Utils.checkUrl(domainUrl));
        }
    }

    /**
     * 取出对应 DomainName 的 Url
     *
     * @param domainName ~
     *
     * @return ~
     */
    public HttpUrl fetchDomain(String domainName) {
        return mDomainNameHub.get(domainName);
    }

    public void removeDomain(String domainName) {
        synchronized (mDomainNameHub) {
            mDomainNameHub.remove(domainName);
        }
    }

    public void clearAllDomain() {
        mDomainNameHub.clear();
    }

    public boolean haveDomain(String domainName) {
        return mDomainNameHub.containsKey(domainName);
    }

    public int domainSize() {
        return mDomainNameHub.size();
    }

    public void putRequestProcess(String domainUrl, OnRequestProcess onRequestProcess) {
        synchronized (mOnRequestProcessMap) {
            mOnRequestProcessMap.put(domainUrl, onRequestProcess);
        }
    }

    public OnRequestProcess fetchRequestProcess(String domainName) {
        return mOnRequestProcessMap.get(domainName);
    }

    public void removeRequestProcess(String domainName) {
        synchronized (mOnRequestProcessMap) {
            mOnRequestProcessMap.remove(domainName);
        }
    }

    public void clearAllRequestProcess() {
        mOnRequestProcessMap.clear();
    }

    /**
     * 可自行实现 {@link UrlParser} 动态切换 Url 解析策略
     *
     * @param parser ~
     */
    public void setUrlParser(UrlParser parser) {
        this.mUrlParser = parser;
    }

    /**
     * 注册当 Url 的 BaseUrl 被改变时会被回调的监听器
     *
     * @param listener ~
     */
    public void registerUrlChangeListener(UrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    /**
     * 注销当 Url 的 BaseUrl 被改变时会被回调的监听器
     *
     * @param listener ~
     */
    public void unregisterUrlChangeListener(UrlChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private Object[] listenersToArray() {
        Object[] listeners = null;
        synchronized (mListeners) {
            if (mListeners.size() > 0) {
                listeners = mListeners.toArray();
            }
        }
        return listeners;
    }


    /**
     * 从 {@link Request#header(String)} 中取出 DomainName
     *
     * @param request ~
     *
     * @return ~
     */
    private String obtainDomainNameFromHeaders(Request request) {
        List<String> headers = request.headers(DOMAIN_NAME);
        if (headers == null || headers.size() == 0)
            return null;
        if (headers.size() > 1)
            throw new IllegalArgumentException("Only one Domain-Name in the headers");
        return request.header(DOMAIN_NAME);
    }
}
