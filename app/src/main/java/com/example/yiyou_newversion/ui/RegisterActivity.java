package com.example.yiyou_newversion.ui;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.model.Data;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.yiyou_newversion.utils.Utils.bitmap2byte;
import static com.example.yiyou_newversion.utils.Utils.md5;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 0;
    public static final int IDENTITY_TOURIST = 0;
    public static final int IDENTITY_GUIDE = 1;
    public static final int CHOOSE_PHOTO = 1;
    private EditText edit_username;
    private EditText edit_phoneNum;
    private RadioButton radio_male;
    private RadioButton radio_female;
    private EditText edit_password;
    private EditText edit_company;
    private RadioButton radio_tourist;
    private RadioButton radio_guide;
    private Button btn_upload_avatar;
    private Button btn_save;
    private TextView click2login;
    private int gender;
    private int identity;
    private String username;
    private String phoneNum;
    private String password;
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //取消标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        init();
    }

    private void init() {
        edit_username = findViewById(R.id.edit_username);
        edit_phoneNum = findViewById(R.id.edit_phoneNum);
        radio_male = findViewById(R.id.radio_male);
        radio_female = findViewById(R.id.radio_male);
        edit_password = findViewById(R.id.edit_password);
        edit_company = findViewById(R.id.edit_company);
        radio_tourist = findViewById(R.id.radio_tourist);
        radio_guide = findViewById(R.id.radio_guide);
        btn_upload_avatar = findViewById(R.id.btn_upload_avatar);
        btn_save = findViewById(R.id.btn_save);
        click2login = findViewById(R.id.click2login);

        btn_upload_avatar.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        click2login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击跳转至登录界面
            case R.id.click2login:
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;

            //上传头像
            case R.id.btn_upload_avatar:
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.
                            permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;

            //点击保存按钮
            case R.id.btn_save:
                username = edit_username.getText().toString();
                password = edit_password.getText().toString();
                company = edit_company.getText().toString();
                phoneNum = edit_phoneNum.getText().toString();

                //判断所选身份
                if (radio_tourist.isChecked()) {
                    identity = IDENTITY_TOURIST;
                } else {
                    identity = IDENTITY_GUIDE;
                }

                //判断所选性别
                if (radio_male.isChecked()) {
                    gender = GENDER_MALE;
                } else {
                    gender = GENDER_FEMALE;
                }

                if (username == null || username.equals("") || password == null || password.equals("")
                        || company == null || company.equals("") || phoneNum == null
                        || phoneNum.equals("") || phoneNum.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                //当前btn_upload_avatar的内容
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) btn_upload_avatar.getBackground();
                                Bitmap current_avatar_bitmap = bitmapDrawable.getBitmap();

                                //原本btn_upload_avatar的内容，如果当前的内容等于原本的，说明用户没有选择头像，则使用默认的头像
                                Bitmap upload_avatar_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_upload);
                                //默认头像
                                Bitmap default_avatar_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);

                                Data data = new Data();

                                //先判断是否已经存在该手机号
                                if (data.hasThisPhoneNum(phoneNum)) {
                                    Toast.makeText(RegisterActivity.this, "您已经注册过该手机号了！", Toast.LENGTH_SHORT).show();
                                } else {
                                    //用户未选择头像则使用默认头像
                                    if (current_avatar_bitmap.equals(upload_avatar_bitmap)) {
                                        if (data.isRegisterSuccessful(username, md5(password), company,
                                                identity, gender, bitmap2byte(default_avatar_bitmap), phoneNum)) {
                                            Toast.makeText(RegisterActivity.this, "注册成功，即将前往登录！", Toast.LENGTH_SHORT).show();

                                            //设置定时任务，一秒后跳至登录界面
                                            Timer timer = new Timer();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            };
                                            timer.schedule(task, 1000);

                                        } else {
                                            Toast.makeText(RegisterActivity.this, "注册失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        //用户选择头像则使用选择的头像
                                        if (data.isRegisterSuccessful(username, md5(password), company,
                                                identity, gender, bitmap2byte(current_avatar_bitmap), phoneNum)) {
                                            Toast.makeText(RegisterActivity.this, "注册成功，即将前往登录！", Toast.LENGTH_SHORT).show();

                                            //设置定时任务，一秒后跳至登录界面
                                            Timer timer = new Timer();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            };
                                            timer.schedule(task, 1000);

                                        } else {
                                            Toast.makeText(RegisterActivity.this, "注册失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "是不是没网了呀...", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    }).start();
                }
                break;
        }
    }

    //打开相册
    private void openAlbum() {
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.setType("image/*");
        startActivityForResult(intent2, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4及以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {

        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {

        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //将上传头像处的图片换成所选的图片
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //picture.setImageBitmap(bitmap);
            btn_upload_avatar.setBackground(new BitmapDrawable(getResources(), bitmap));
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
