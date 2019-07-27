package com.example.yiyou_newversion.ui;

import android.content.Intent;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JoinTeamActivity extends AppCompatActivity {

    private EditText teamcode;
    private Button btn_sure;
    private Button btn_cancel;
    private Data data = new Data();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        teamcode = findViewById(R.id.edit_teamcode);
        btn_sure = findViewById(R.id.sure);
        btn_cancel = findViewById(R.id.cancel);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String txt_teamcode = teamcode.getText().toString();
                if (txt_teamcode == null || txt_teamcode.equals("")) {
                    Toast.makeText(JoinTeamActivity.this, "请输入队伍码！", Toast.LENGTH_SHORT).show();
                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
                                List<Object> list = data.joinTeamByTeamCode(txt_teamcode);
                                //先判断有没有该队伍
                                if ((boolean) list.get(0) == true) {
                                    //第二个元素为1，说明加入队伍成功;为0说明不成功
                                    if ((int) list.get(1) == 1) {
                                        Toast.makeText(JoinTeamActivity.this, "加入队伍成功！", Toast.LENGTH_SHORT).show();

                                        //设置定时任务，一秒后跳转至首页
                                        final Intent intent = new Intent(JoinTeamActivity.this, TouristMainActivity.class);
                                        Timer timer = new Timer();
                                        TimerTask ts = new TimerTask() {
                                            @Override
                                            public void run() {
                                                startActivity(intent);
                                                finish();
                                            }
                                        };
                                        timer.schedule(ts, 1000);
                                    } else {
                                        Toast.makeText(JoinTeamActivity.this, "加入失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(JoinTeamActivity.this, "不存在该队伍哦！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(JoinTeamActivity.this, "是不是没网了呀...", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    });
                    thread.start();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinTeamActivity.this, TouristMainActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
