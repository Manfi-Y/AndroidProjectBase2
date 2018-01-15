package cn.manfi.android.project.base.common.net.retrofit.parser;

import cn.manfi.android.project.base.common.net.retrofit.RetrofitManager;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Url解析器接口
 * Created by manfi on 2017/9/20.
 */
public interface UrlParser {

    /**
     * 将 {@link RetrofitManager#mDomainNameHub} 中映射的 Url 解析成完整的{@link HttpUrl}
     * 用来替换 @{@link Request#url} 达到动态切换 Url
     *
     * @param domainUrl Api 域名
     *
     * @return ~
     */
    HttpUrl parseUrl(HttpUrl domainUrl, HttpUrl url);
}
