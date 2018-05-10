package cn.manfi.android.project.base.common;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 保存对象到data/data
 * 在手机应用管理清除数据会清除files，清除缓存会清除cache
 * Created by Manfi
 */
public class SaveObjectUtils {

    /**
     * 储存文件到data/data/cache
     *
     * @param context  ~
     * @param fileName 文件名
     * @param obj      对象
     */
    public static void saveObjToCache(Context context, String fileName, Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File opf = new File(context.getCacheDir(), fileName + ".tmp");
            fos = new FileOutputStream(opf);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从data/data/cache获取储存对象
     *
     * @param context  ~
     * @param fileName 文件名
     * @return ~
     */
    public static <T> T getObjectFromCache(Context context, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File opf = new File(context.getCacheDir(), fileName + ".tmp");
            if (!opf.exists()) {
                return null;
            }
            fis = new FileInputStream(opf);
            ois = new ObjectInputStream(fis);
            //noinspection unchecked
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 储存文件到data/data/files
     *
     * @param context  ~
     * @param fileName 文件名
     * @param obj      对象
     */
    public static void saveObjectToFile(Context context, String fileName, Object obj) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从data/data/files获取储存对象
     *
     * @param context  ~
     * @param fileName 文件名
     * @return ~
     */
    public static <T> T getObjectFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(fileName);
            ois = new ObjectInputStream(fis);
            //noinspection unchecked
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从data/data/files删除文件
     *
     * @param context  ~
     * @param fileName ~
     */
    public static boolean deleteObjectFromFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * 从data/data/cache删除文件
     *
     * @param context  ~
     * @param fileName ~
     */
    public static boolean deleteObjectFromCache(Context context, String fileName) {
        File opf = new File(context.getCacheDir(), fileName + ".tmp");
        return opf.delete();
    }
}
