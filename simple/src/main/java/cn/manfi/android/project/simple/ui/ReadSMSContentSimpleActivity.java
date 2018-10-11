package cn.manfi.android.project.simple.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.jakewharton.rxbinding2.view.RxView;

import cn.manfi.android.project.base.common.RxDisposedManager;
import cn.manfi.android.project.base.ui.base.BaseActivity;
import cn.manfi.android.project.simple.R;
import cn.manfi.android.project.simple.databinding.ActivityReadSmsContentBinding;

/**
 * 读取短信内容Simple
 * Create by Manfi on 2018/9/6
 */
public class ReadSMSContentSimpleActivity extends BaseActivity {

    private ActivityReadSmsContentBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_read_sms_content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxDisposedManager.dispose(activity);
    }

    @Override
    protected void initView() {
        RxDisposedManager.addDisposed(activity, RxView.clicks(binding.btnGet)
                .subscribe(o -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getSMSContent();
                    }
                }));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getSMSContent() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(Telephony.Sms.CONTENT_URI, new String[]{
                Telephony.Sms.ADDRESS,   //
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.READ,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE,
        }, null, null, "");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SMSMessage message = new SMSMessage();
                message.address = cursor.getString(0);
                message.body = cursor.getString(1);
                message.date = cursor.getLong(2);
                message.read = getMessageRead(cursor.getInt(3));
                message.status = getMessageStatus(cursor.getInt(4));
                message.type = getMessageType(cursor.getInt(5));
                message.person = getPerson(message.address);
                System.out.println(message.toString());
            }
            cursor.close();
        }
    }

    private String getMessageRead(int anInt) {
        if (1 == anInt) {
            return "已读";
        }
        if (0 == anInt) {
            return "未读";
        }
        return null;
    }

    private String getMessageType(int anInt) {
        if (1 == anInt) {
            return "收到的";
        }
        if (2 == anInt) {
            return "已发出";
        }
        return null;
    }

    private String getMessageStatus(int anInt) {
        switch (anInt) {
            case -1:
                return "接收";
            case 0:
                return "complete";
            case 64:
                return "pending";
            case 128:
                return "failed";
            default:
                break;
        }
        return null;
    }

    private String getPerson(String address) {
        try {
            ContentResolver resolver = getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
            Cursor cursor;
            cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        String name = cursor.getString(0);
                        return name;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class SMSMessage {

        String address;
        String body;
        long date;
        String read;
        String status;
        String type;
        String person;

        @Override
        public String toString() {
            return "SMSMessage{" +
                    "date=" + date +
                    ", address='" + address + '\'' +
                    ", body='" + body + '\'' +
                    ", person='" + person + '\'' +
                    ", read='" + read + '\'' +
                    ", status='" + status + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
