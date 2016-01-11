package com.hankkin.compustrading.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.hankkin.compustrading.FileUploadListener;
import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.model.Person;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Hankkin on 15/12/20.
 */
public class BaseActivity extends AppCompatActivity {

    /*请求相机Code*/
    public static final int REQUST_CODE_CAMERA = 0;
    /*请求相册Code*/
    public static final int REQUEST_CODE_GALLERY = 1;
    /*发布商品Code*/
    public static final int REQUEST_CODE_FABU = 2;

    public MaterialDialog loadDialog;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


    public Person getCurrentPerson(Context context){
        Person person = Person.getCurrentUser(context,Person.class);
        return person;
    }

    /**
     * 调用相册
     * by Hankkin at:2015-12-20 23:00:16
     * @param act
     */
    public  void getImageFromGallery(Activity act) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        act.startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * 上传图片
     * by Hankkin at:2015-12-20 23:00:34
     */

    public void uploadImg(String filepath,Context context, final FileUploadListener listener){
        BTPFileResponse response = BmobProFile.getInstance(context).upload(filepath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                String url = bmobFile.getUrl();
                listener.success(url);
            }

            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onError(int i, String s) {
                listener.fail();
            }
        });
    }


    public void showLoadingDialog(){
        loadDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.loading,null,false);
        ProgressWheel wheel = (ProgressWheel) view.findViewById(R.id.pw_loading);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,80);
        params.height = HankkinUtils.dip2px(this,80);
        params.width = HankkinUtils.dip2px(this,80);
        wheel.setLayoutParams(params);
        wheel.setBackgroundColor(getResources().getColor(R.color.light_white));
        loadDialog.setView(view);
        loadDialog.setBackgroundResource(getResources().getColor(R.color.transparent));
        loadDialog.show();
    }

    public void dimissDialog(){
        if (loadDialog!=null){
            loadDialog.dismiss();
        }
    }

    /**
     * 初始化图片路径
     *
     * @return
     */
    public static String iniFilePath(Activity act) {
        String filepath = null;
        String path = null;
        File fileSD = null;

        // 准备存储位置
        boolean sdExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (!sdExist) {
            HankkinUtils.showLToast(act, "没有找到SD存储卡");
            return null;

        } else {
            //TODO 内容提示完善
            path = Environment.getExternalStorageDirectory().getPath() + "/compustrading/Camera";
            fileSD = new File(path);
            if (fileSD.exists()) {
                filepath = path + "/" + System.currentTimeMillis() + ".jpg";
            } else {
                fileSD.mkdir();
                filepath = fileSD.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
            }
            return filepath;
        }
    }

    /**
     * 调用相机
     *
     * @param act
     */
    public static void goCamera(Activity act, String filepath) {
        File file = new File(filepath);
        // 启动Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        act.startActivityForResult(intent, REQUST_CODE_CAMERA);
    }

    /**
     * android系统版本选择图库图片解决方法---获取图片路径
     * by Hankkin at:2015-3-10
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
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
                final String selection = MediaStore.MediaColumns._ID + "=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     * android系统版本选择图库图片解决方法--获取数据
     * by Hankkin at:2015-3-10
     *
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
