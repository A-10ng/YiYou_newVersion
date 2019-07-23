package com.example.yiyou_newversion.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.model.Data;
import com.example.yiyou_newversion.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CreateTeamActivity extends AppCompatActivity {

    //随机生成的六位大写字母的队伍码
    String TeamCodeByMethod = "";

    private EditText mTeamName;
    private EditText mTravelTime;
    private EditText mTeamIntro;
    private Button mCreateTeam;
    private String TeamName;
    private String TravelTime;
    private String TeamIntro;
    private String TeamCode;

    private Data data = new Data();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        findAllViews();

        mCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户填写的信息
                TeamName = mTeamName.getText().toString().trim();
                TravelTime = mTravelTime.getText().toString().trim();
                TeamIntro = mTeamIntro.getText().toString().trim();

                //用户至少要填写队伍名称
                if (TeamName.equals("") || TeamName == null) {
                    Toast.makeText(CreateTeamActivity.this, "请至少填写队伍名称！", Toast.LENGTH_SHORT).show();
                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            TeamCode = createTeamCode();
                            Bitmap QRBitmap = createQRCode(TeamCode);

                            //如果创建队伍成功
                            if (data.crtTeamSuccessful(TeamName,TravelTime,TeamIntro,
                                    TeamCode,"0", Utils.bitmap2byte(QRBitmap))){
                                Toast.makeText(CreateTeamActivity.this, "创建队伍成功！", Toast.LENGTH_SHORT).show();

                                //将队伍码，二维码的bitmap，队伍名称放进intent传到FinishCrtTeamActivity
                                final Intent intent = new Intent(CreateTeamActivity.this, FinishCrtTeamActivity.class);
                                intent.putExtra("TeamCode", TeamCode);
                                intent.putExtra("QRBitmap", Utils.bitmap2byte(QRBitmap));
                                intent.putExtra("TeamName", TeamName);

                                Timer timer = new Timer();
                                TimerTask ts = new TimerTask() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                        finish();
                                    }
                                };
                                timer.schedule(ts, 1000);
                            }else {
                                //创建队伍失败则提示
                                Toast.makeText(CreateTeamActivity.this, "创建队伍失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    });
                    thread.start();
                }
            }
        });

    }

    private void findAllViews() {
        mTeamName = findViewById(R.id.edit_teamName);
        mTravelTime = findViewById(R.id.edit_travelTime);
        mTeamIntro = findViewById(R.id.edit_teamIntro);
        mCreateTeam = findViewById(R.id.btn_create);
    }

    private String createTeamCode() {
        //随机生成六位大写字母的队伍码
        for (int i = 0; i < 6; i++) {
            int value = (int) (Math.random() * 26 + 65);
            TeamCodeByMethod += (char) value;
        }

        //判断随机生成的队伍码在数据库中是否存在
        List<Team> teams = data.findTeamsByTeamCode(TeamCodeByMethod);
        while (teams.size() >= 1) {
            for (int i = 0; i < 6; i++) {
                int value = (int) (Math.random() * 26 + 65);
                TeamCodeByMethod += (char) value;
            }
            teams = data.findTeamsByTeamCode(TeamCodeByMethod);
        }
        return TeamCodeByMethod;
    }

    private Bitmap createQRCode(String url) {
        try {
            int w = 480;
            int h = 480;
            if (url == null || url.equals("") || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];

            //下面这里按照二维码的算法，逐个生成二维码的图片
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }

            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
