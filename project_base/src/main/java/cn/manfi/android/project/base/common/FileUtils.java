package cn.manfi.android.project.base.common;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件管理工具类
 * Created by Manfi on 15/12/8.
 */
public class FileUtils {

    /**
     * @param context ~
     * @return 返回data/data/files目录
     */
    public static String getDataFilePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * @param context ~
     * @return 返回data/data/cache目录
     */
    public static String getDataCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取SD卡路径
     *
     * @return ~
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * @return 获取SDCard可用空间
     */
    public static long getSDCardAvailableSize() {
        StatFs stat = new StatFs(getSDCardPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * @return SDCard剩余容量是否大于10MB
     */
    public static boolean isSDCardEnough() {
        return getSDCardAvailableSize() > 10485760;
    }

    /**
     * 创建文件路径
     *
     * @param path ~
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createFilePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * App临时文件目录
     *
     * @return ~
     */
    public static String getAppTempDataPath() {
        String rev = getSDCardPath() + "8684/temp/";
        createFilePath(rev);
        return rev;
    }

    /**
     * App储存数据目录
     *
     * @return 如/sdcard/8684/
     */
    public static String getAppDataPath() {
        String rev = getSDCardPath() + "8684/";
        createFilePath(rev);
        return rev;
    }

    /**
     * 解压文件
     *
     * @param zipFilePath 压缩包位置
     * @param desPath     解压后位置
     * @param fileName    保存文件名（空的话直接用解压后名字）
     * @return ~
     */
    public static boolean unZip(String zipFilePath, String desPath, String fileName) {
        int BUFFER = 8 * 1024;
        String strEntry;
        try {
            BufferedOutputStream bos = null;
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                    new FileInputStream(zipFilePath), BUFFER));
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                try {
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(desPath, TextUtils.isEmpty(fileName) ? strEntry : fileName);
                    if (!entryFile.getParentFile().exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        entryFile.getParentFile().mkdirs();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(
                            entryFile), BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        bos.write(data, 0, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        bos.flush();
                        bos.close();
                    }
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 移动文件
     *
     * @param srcFile      源文件
     * @param destFilePath 目标路径
     * @return ~
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean moveFile(File srcFile, File destFilePath) {
        if (!srcFile.exists()) {
            return false;
        }
        if (!destFilePath.getParentFile().exists()) {
            destFilePath.getParentFile().mkdirs();
        }
        return srcFile.renameTo(destFilePath);
    }

    /**
     * 删除Contains keyworkds的文件
     *
     * @param dir      文件夹路径
     * @param keywords 关键字
     * @return ~
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static int deleteFileByKeywords(File dir, String keywords) {
        int deleteCount = 0;
        if (!dir.exists()) {
            return deleteCount;
        }
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.getName().contains(keywords)) {
                file.delete();
                deleteCount++;
            }
        }
        return deleteCount;
    }

    /**
     * 删除文件或文件夹（文件夹需要先删除其下面的所有文件后文件夹才能删除自己）
     *
     * @param fileOrDirectory ~
     */
    public static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
}
