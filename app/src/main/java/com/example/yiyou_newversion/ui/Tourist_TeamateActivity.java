package com.example.yiyou_newversion.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.model.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.yiyou_newversion.utils.Utils.byte2bitmap;

public class Tourist_TeamateActivity extends AppCompatActivity {

    private final static int DISPLAY_TEAMATE = 0;
    private Data data = new Data();
    private Handler handler;
    private TextView teamName;
    private TextView teamNum;
    private TextView teamCode;
    private Button btn_teamCode;
    private EditText phone;
    private Button btn_quit;
    private ImageView QRCodeBigpic;
    private Dialog dialog;
    private LinearLayout activity_guide__teamate;
    //当前的队伍
    private List<Team> teams = new ArrayList<>();
    //当前没有人的队伍
    private List<Team> NoPeopleTeam = new ArrayList<>();
    //当前队伍的成员
    private List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist__teamate);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DISPLAY_TEAMATE:
                        if (teams.size() != 0) {
                            Button btn[] = new Button[teams.size()];
                            for (int i = 0; i < teams.size(); i++) {
                                btn[i] = new Button(Tourist_TeamateActivity.this);
                                btn[i].setId(1000 + i);
                                btn[i].setBackgroundResource(R.drawable.btn_team);
                                btn[i].setAllCaps(false);
                                btn[i].setText("游客" + (i + 1) + "    " + "姓名：" + users.get(i).getUsername() + "    手机号：" +
                                        users.get(i).getPhoneNum());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                activity_guide__teamate.addView(btn[i], layoutParams);
                            }

                            teamName.setText(Data.CurrentTeamName);
                            Log.i("teamName:", Data.CurrentTeamName);

                            teamNum.setText(teams.size() + "");
                            teamCode.setText(NoPeopleTeam.get(0).getTeamCode());

                            btn_teamCode.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog = new Dialog(Tourist_TeamateActivity.this, R.style.AppTheme);
                                    QRCodeBigpic = new ImageView(Tourist_TeamateActivity.this);
                                    QRCodeBigpic.setImageBitmap(byte2bitmap(NoPeopleTeam.get(0).getQrCode()));
                                    dialog.setContentView(QRCodeBigpic);
                                    dialog.show();

                                    final String path = Environment.getExternalStorageDirectory().getPath() + "/teamCode_" +
                                            NoPeopleTeam.get(0).getTeamCode() + ".jpg";
                                    //大图的点击事件（点击会让他消失）
                                    QRCodeBigpic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    //大图的长按监听事件
                                    QRCodeBigpic.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            // Check if we have write permission
                                            if (ContextCompat.checkSelfPermission(Tourist_TeamateActivity.this,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(Tourist_TeamateActivity.this, new String[]{
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                            }

                                            //弹出的“保存图片”的dialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Tourist_TeamateActivity.this);
                                            builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    saveCroppedImage(((BitmapDrawable) QRCodeBigpic.getDrawable()).getBitmap(), path);
                                                }
                                            });
                                            builder.show();
                                            return true;
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
            }
        };

        findAllViews();

        activity_guide__teamate = findViewById(R.id.activity_guide__teamate);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //因为队伍里面肯定包含自己，所以teams的大小肯定不等于0
                teams = data.getCurrentTeam();
                NoPeopleTeam = data.getNoPeopleTeam();
                users = data.getCurUsersInfo(teams);

                handler.sendEmptyMessage(DISPLAY_TEAMATE);
            }
        });
        thread.start();

        //退出队伍按钮
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tourist_TeamateActivity.this, QuitTeamActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void findAllViews() {
        teamName = findViewById(R.id.teamName);
        teamNum = findViewById(R.id.teamNum);
        teamCode = findViewById(R.id.teamCode);
        btn_teamCode = findViewById(R.id.btn_teamCode);
        btn_quit = findViewById(R.id.btn_quit);
    }

    private void saveCroppedImage(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            Toast.makeText(Tourist_TeamateActivity.this, "该路径为目录路径！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
            Toast.makeText(Tourist_TeamateActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Tourist_TeamateActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
