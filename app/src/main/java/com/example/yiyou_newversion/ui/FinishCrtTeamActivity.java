package com.example.yiyou_newversion.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.model.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.yiyou_newversion.utils.Utils.byte2bitmap;

public class FinishCrtTeamActivity extends AppCompatActivity {

    private TextView txt_guideName;
    private TextView txt_teamName_teamCode;
    private ImageView QRCode;
    private String guideName;
    private Button btn_finish;
    private Dialog dialog;
    private ImageView mImageView;

    private Data data = new Data();

    //CreateTeamActivity传过来的信息的相关变量
    private String TeamName;
    private String TeamCode;
    private byte[] QRByte;

    private Handler handler;
    private final static int DISPLAY_INFO = 0;
    //二维码保存的路径
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_crt_team);

        findAllViews();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DISPLAY_INFO:
                        //拿到CreateTeamActivity传过来的信息
                        Intent intent = getIntent();
                        TeamName = intent.getStringExtra("TeamName");
                        TeamCode = intent.getStringExtra("TeamCode");
                        QRByte = intent.getByteArrayExtra("QRBitmap");

                        txt_guideName.setText("导游（" + guideName + "）");
                        txt_teamName_teamCode.setText(TeamName + "（" + TeamCode + "）");
                        QRCode.setImageBitmap(byte2bitmap(QRByte));

                        path = Environment.getExternalStorageDirectory().getPath() + "/teamCode_" + TeamCode + ".jpg";
                        //点击显示大图，长按保存
                        //对话框显示大图
                        dialog = new Dialog(FinishCrtTeamActivity.this, R.style.AppTheme);
                        mImageView = getImageView();
                        dialog.setContentView(mImageView);

                        btn_finish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Data.CurrentTeamName = "none";
                                Intent intent = new Intent(FinishCrtTeamActivity.this, GuideMainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        //大图的点击事件（点击会让他消失）
                        mImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        //大图的长按监听事件
                        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                // Check if we have write permission
                                if (ContextCompat.checkSelfPermission(FinishCrtTeamActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(FinishCrtTeamActivity.this,new String[]{
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                }

                                //弹出的“保存图片”的dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(FinishCrtTeamActivity.this);
                                builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveCroppedImage(((BitmapDrawable) mImageView.getDrawable()).getBitmap(), path);
                                    }
                                });
                                builder.show();
                                return true;
                            }
                        });

                        QRCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.show();
                            }
                        });
                        break;
                }
            }
        };

        //获取当前用户的所有信息
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                User currentUser = data.getCurUserInfo();
                //判断当前用户是否存在
                if (currentUser != null) {
                    guideName = currentUser.getUsername();
                }else {
                    guideName = "获取不到";
                }

                handler.sendEmptyMessage(DISPLAY_INFO);
            }
        });
        thread.start();
    }

    private void saveCroppedImage(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            Toast.makeText(FinishCrtTeamActivity.this, "该路径为目录路径！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
            Toast.makeText(FinishCrtTeamActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(FinishCrtTeamActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private ImageView getImageView() {
        ImageView iv = new ImageView(this);
        //宽高
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //设置padding
        iv.setPadding(20, 20, 20, 20);
        //imageview设置图片
        iv.setImageBitmap(((BitmapDrawable) QRCode.getDrawable()).getBitmap());
        return iv;
    }

    private void findAllViews() {
        txt_guideName = findViewById(R.id.guideName);
        txt_teamName_teamCode = findViewById(R.id.teamName_teamCode);
        QRCode = findViewById(R.id.qrCode);
        btn_finish = findViewById(R.id.finish);
    }

    //禁用返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }
}
