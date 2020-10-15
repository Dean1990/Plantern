package com.deanlib.plantern.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;


import com.deanlib.plantern.Plantern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * 文件相关
 * 所有编码为 utf-8
 * <p>
 * Created by dean on 2017/4/24.
 */

public class FileUtils {

    private static final String ENCODING = "utf-8";

    /**
     * 获取Asset文件夹下的文本
     *
     * @param assetFielPath
     * @return
     */
    public static String getAssetText(String assetFielPath) {
        InputStream is = null;
        try {
            is = Plantern.getAppContext().getResources().getAssets().open(assetFielPath);
            String textfile = convertStreamToString(is);
            return textfile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String convertStreamToString(InputStream is)
            throws IOException {
        Writer writer = new StringWriter();

        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, ENCODING));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        String text = writer.toString();
        return text;
    }

    /**
     * 获取应用缓存总大小
     *
     * @return
     * @throws Exception
     */
    public static String getCacheTotalSize() throws Exception {
        long cacheSize = getFileOrDirSize(Plantern.getAppContext().getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFileOrDirSize(Plantern.getAppContext().getExternalCacheDir());
        }
        return FormatUtils.formatFileSize(cacheSize);
    }


    /**
     * 删除所有缓存
     */
    public static void deleteCacheAll() {
        deleteDir(Plantern.getAppContext().getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(Plantern.getAppContext().getExternalCacheDir());
        }
    }

    /**
     * 获取文件或者文件夹大小
     * <p>
     * Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
     * Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileOrDirSize(File file) throws Exception {
        if (!file.exists()) return 0;
        if (!file.isDirectory()) return file.length();

        long length = 0;
        File[] list = file.listFiles();
        if (list != null) { // 文件夹被删除时, 子文件正在被写入, 文件属性异常返回null.
            for (File item : list) {
                length += getFileOrDirSize(item);
            }
        }

        return length;
    }


    /**
     * 检查SD卡是否可用,外部存储
     *
     * @return
     */
    public static boolean isSDCardEnabled() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建目录 在外部存储目录 一般为/sdcard/
     *
     * @param name
     * @return
     */
    public static File createDir(String name) {

        File dir = new File(Environment.getExternalStorageDirectory(), name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 删除目录
     *
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    /**
     * 文件成功失败的回调
     */
    public interface FileCallback {
        void onSuccess(File file);

        void onFail(Exception e);
    }

    public static File createTempFile(String suffix){
        return createTempFile(null,suffix);
    }

    /**
     * 创建临时文件
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return
     */
    public static File createTempFile(String prefix, String suffix) {

        File image = null;
        try {
            image = File.createTempFile(prefix, suffix);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    /**
     * 复制文件，使用FileChannels类
     *
     * @param source
     * @param dest
     */
    public static boolean copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                if (!dest.getParentFile().exists()){
                    dest.getParentFile().mkdirs();
                }
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param uri The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Uri uri) {

//        if (UtilsConfig.isDEBUG())
//            Log.d(TAG + " File -",
//                    "Authority: " + uri.getAuthority() +
//                            ", Fragment: " + uri.getFragment() +
//                            ", Port: " + uri.getPort() +
//                            ", Query: " + uri.getQuery() +
//                            ", Scheme: " + uri.getScheme() +
//                            ", Host: " + uri.getHost() +
//                            ", Segments: " + uri.getPathSegments().toString());

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(Plantern.getAppContext(), uri)) {
            // LocalStorageProvider
//            if (isLocalStorageDocument(uri)) {
//                // The path is the id
//                return DocumentsContract.getDocumentId(uri);
//            }
            // ExternalStorageProvider
//            else
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static Uri getResIdToUri(int resourceId) {
        return Uri.parse("android.resource://" + Plantern.getAppContext().getPackageName() + "/" + resourceId);
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Uri uri) {
        File file = new File(getPath(uri));
        return getMimeType(file);
    }

//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is {@link LocalStorageProvider}.
//     * @author paulburke
//     */
//    public static boolean isLocalStorageDocument(Uri uri) {
//        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
//    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = Plantern.getAppContext().getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (Plantern.isDebug())
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 获取磁盘可用空间
     *
     * @return byte 单位 kb
     */
    public static long getDiskAvailableSize() {
        if (!isSDCardEnabled()) return 0;
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getAbsolutePath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
        // (availableBlocks * blockSize)/1024 KIB 单位
        // (availableBlocks * blockSize)/1024 /1024 MIB单位
    }

    /**
     * 获取cache路径
     *
     * @return
     */
    public static String getDiskCachePath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (Plantern.getAppContext().getExternalCacheDir() != null)
                return Plantern.getAppContext().getExternalCacheDir().getPath();
        }
        return Plantern.getAppContext().getCacheDir().getPath();

    }

//    /**
//     * 获取FileProvider path
//     * fileprovider 转 file 绝对路径
//     */
//    private static String getFPUriToPath(Uri uri) {
//        try {
//            List<PackageInfo> packs = Plantern.getAppContext().getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
//            if (packs != null) {
//                String fileProviderClassName = FileProvider.class.getName();
//                for (PackageInfo pack : packs) {
//                    ProviderInfo[] providers = pack.providers;
//                    if (providers != null) {
//                        for (ProviderInfo provider : providers) {
//                            if (uri.getAuthority().equals(provider.authority)) {
//                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
//                                    Class<FileProvider> fileProviderClass = FileProvider.class;
//                                    try {
//                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
//                                        getPathStrategy.setAccessible(true);
//                                        Object invoke = getPathStrategy.invoke(null, Plantern.getAppContext(), uri.getAuthority());
//                                        if (invoke != null) {
//                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
//                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
//                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
//                                            getFileForUri.setAccessible(true);
//                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
//                                            if (invoke1 instanceof File) {
//                                                String filePath = ((File) invoke1).getAbsolutePath();
//                                                return filePath;
//                                            }
//                                        }
//                                    } catch (NoSuchMethodException e) {
//                                        e.printStackTrace();
//                                    } catch (InvocationTargetException e) {
//                                        e.printStackTrace();
//                                    } catch (IllegalAccessException e) {
//                                        e.printStackTrace();
//                                    } catch (ClassNotFoundException e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
