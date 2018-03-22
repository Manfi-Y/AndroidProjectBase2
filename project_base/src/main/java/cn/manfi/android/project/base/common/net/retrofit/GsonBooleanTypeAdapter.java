package cn.manfi.android.project.base.common.net.retrofit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Gson数据类型转布尔值
 * <p>
 * '@JsonAdapter(GsonBooleanTypeAdapter.class)'
 * </p> Created by manfi on 2018/3/22.
 */

public class GsonBooleanTypeAdapter implements JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int code = json.getAsInt();
        return code >= 1;
    }
}
