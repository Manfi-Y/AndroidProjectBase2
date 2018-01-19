package cn.manfi.android.project.base.common.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * 上传请求体
 * Created by manfi on 2018/1/16.
 */

public class UploadRequestBody extends RequestBody {

    /**
     * 上传文件
     */
    private File file;
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public UploadRequestBody(File file) {
        this.file = file;
    }

    private UploadOnSubscribe mUploadOnSubscribe;

    public void setUploadOnSubscribe(UploadOnSubscribe uploadOnSubscribe) {
        this.mUploadOnSubscribe = uploadOnSubscribe;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(file);
        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                if (mUploadOnSubscribe != null) {
                    mUploadOnSubscribe.onRead(read);
                }
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }
}
