package cn.manfi.android.project.base.common.net.retrofit;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import cn.manfi.android.project.base.common.Config;
import cn.manfi.android.project.base.common.log.LogUtil;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 * 自定义Gson网络响应处理
 * Created by manfi on 2017/9/22.
 */

final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        String response = value.string();
        LogUtil.d(Config.isDebug(), "Response", "请求响应：" + response);

        /*
        由于已经读取了ResponseBody，不能再次读取，所以上面处理输出后，将引用的ResponseBody字符串
        转成Reader给Gson转换成Json。
         */
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader;
        if (charset != null) {
            try {
                reader = new InputStreamReader(inputStream, charset);
                JsonReader jsonReader = gson.newJsonReader(reader);
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        } else {
            return null;
        }
    }
}
