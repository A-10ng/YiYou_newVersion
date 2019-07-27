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
import android.os.Looper;
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

public class Guide_TeamateActivity extends AppCompatActivity {

    //表示队伍有成员
    private final static int DISPLAY_TEAMATE_SUCCESSFUL = 0;
    //表示队伍没有成员
    private final static int DISPLAY_TEAMATE_FAIL = 1;
    //删除队伍成功
    private final static int DELETE_TEAM_SUCCESSFUL = 2;
    //删除队伍失败
    private final static int DELETE_TEAM_FAIL = 3;

    private Data data = new Data();
    private Handler handler;
    private TextView teamName;
    private TextView teamNum;
    private TextView teamCode;
    private Button btn_teamCode;
    private Button btn_cancelteam;
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
        setContentView(R.layout.activity_guide__teamate);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DISPLAY_TEAMATE_SUCCESSFUL:
                        Button btn[] = new Button[teams.size()];
                        for (int i = 0; i < teams.size(); i++) {
                            btn[i] = new Button(Guide_TeamateActivity.this);
                            btn[i].setId(1000 + i);
                            btn[i].setBackgroundResource(R.drawable.btn_team);
                            btn[i].setAllCaps(false);
                            btn[i].setText("游客" + (i + 1) + "    " + "姓名：" + users.get(i).getUsername() + "    手机号：" +
                                    users.get(i).getPhoneNum());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            activity_guide__teamate.addView(btn[i], layoutParams);
                        }

                        Log.i("guideteamate>>>", Data.CurrentTeamName);
                        teamName.setText(Data.CurrentTeamName);
                        Log.i("teamName:", Data.CurrentTeamName);

                        teamNum.setText(teams.size() + "");
                        teamCode.setText(NoPeopleTeam.get(0).getTeamCode());

                        btn_teamCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog = new Dialog(Guide_TeamateActivity.this, R.style.AppTheme);
                                QRCodeBigpic = new ImageView(Guide_TeamateActivity.this);
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
                                        //弹出的“保存图片”的dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Guide_TeamateActivity.this);
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
                        break;
                    case DISPLAY_TEAMATE_FAIL:
                        teamName.setText(Data.CurrentTeamName);
                        teamNum.setText((NoPeopleTeam.size() - 1) + "");
                        teamCode.setText(NoPeopleTeam.get(0).getTeamCode());
                        btn_teamCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog = new Dialog(Guide_TeamateActivity.this, R.style.AppTheme);
                                QRCodeBigpic = new ImageView(Guide_TeamateActivity.this);
                                QRCodeBigpic.setImageBitmap(byte2bitmap(NoPeopleTeam.get(0).getQrCode()));
                                dialog.setContentView(QRCodeBigpic);
                                dialog.show();

                                final String paths = Environment.getExternalStorageDirectory().getPath() + "/teamCode_" +
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
                                        if (ContextCompat.checkSelfPermission(Guide_TeamateActivity.this,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(Guide_TeamateActivity.this, new String[]{
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        }

                                        //弹出的“保存图片”的dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Guide_TeamateActivity.this);
                                        builder.setItems(new String[]{getResources().getString(R.string.save_picture)}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                saveCroppedImage(((BitmapDrawable) QRCodeBigpic.getDrawable()).getBitmap(), paths);
                                            }
                                        });
                                        builder.show();
                                        return true;
                                    }
                                });
                            }
                        });
                        break;
                    case DELETE_TEAM_SUCCESSFUL:
                        Toast.makeText(Guide_TeamateActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                        Data.CurrentTeamName = "none";
                        Data.CurrentGuidePhoneNum = "none";
                        Intent intent = new Intent(Guide_TeamateActivity.this, GuideMainActivity.class);
                        startActivity(intent);
                        break;
                    case DELETE_TEAM_FAIL:
                        Toast.makeText(Guide_TeamateActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        findAllViews();

        activity_guide__teamate = findViewById(R.id.activity_guide__teamate);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    teams = data.getCurrentTeam();
                    NoPeopleTeam = data.getNoPeopleTeam();

                    //当前队伍已有成员
                    if (teams.size() != 0) {
                        users = data.getCurUsersInfo(teams);
                        handler.sendEmptyMessage(DISPLAY_TEAMATE_SUCCESSFUL);
                    }
                    //当前队伍没有成员
                    else {
                        handler.sendEmptyMessage(DISPLAY_TEAMATE_FAIL);
                    }
                } catch (Exception e) {
                    Toast.makeText(Guide_TeamateActivity.this, "是不是没网了呀...", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
        thread.start();

        btn_cancelteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Guide_TeamateActivity.this);
                dialog.setTitle("您真的要删除该队伍么？");
                dialog.setPositiveButton("狠心删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                try {
                                    boolean result = data.deleteTeamSuccessful();
                                    if (result == true) {
                                        handler.sendEmptyMessage(DELETE_TEAM_SUCCESSFUL);
                                    } else {
                                        handler.sendEmptyMessage(DELETE_TEAM_FAIL);
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(Guide_TeamateActivity.this, "是不是没网了呀...", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            }
                        });
                        thread1.start();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("再考虑一下下", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void saveCroppedImage(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            Toast.makeText(Guide_TeamateActivity.this, "该路径为目录路径！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
            Toast.makeText(Guide_TeamateActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Guide_TeamateActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void findAllViews() {
        teamName = findViewById(R.id.teamName);
        teamNum = findViewById(R.id.teamNum);
        teamCode = findViewById(R.id.teamCode);
        btn_teamCode = findViewById(R.id.btn_teamCode);
        btn_cancelteam = findViewById(R.id.btn_cancelteam);
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
