package com.example.yiyou_newversion.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.model.Data;

import java.util.Timer;
import java.util.TimerTask;

public class QuitTeamActivity extends AppCompatActivity {

    private Button btn_sure;
    private Button btn_hesitate;
    private Data data = new Data();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quit_team);

        btn_sure = findViewById(R.id.btn_sure);
        btn_hesitate = findViewById(R.id.btn_hesitate);

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean result = data.QuitThisTeam();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result == true){
                                    Toast.makeText(QuitTeamActivity.this,"退队成功！",Toast.LENGTH_SHORT).show();
                                    Data.CurrentTeamName = "none";
                                    final Intent intent2 = new Intent(QuitTeamActivity.this, TouristMainActivity.class);
                                    Timer timer = new Timer();
                                    TimerTask ts = new TimerTask() {
                                        @Override
                                        public void run() {
                                            startActivity(intent2);
                                            finish();
                                        }
                                    };
                                    timer.schedule(ts,1000);
                                }else {
                                    Toast.makeText(QuitTeamActivity.this,"退队失败，发生未知错误！",Toast.LENGTH_SHORT).show();
                                    final Intent intent2 = new Intent(QuitTeamActivity.this, TouristMainActivity.class);
                                    Timer timer = new Timer();
                                    TimerTask ts = new TimerTask() {
                                        @Override
                                        public void run() {
                                            startActivity(intent2);
                                            finish();
                                        }
                                    };
                                    timer.schedule(ts,1000);
                                }
                            }
                        });

                    }
                });
                thread.start();
            }
        });


        btn_hesitate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuitTeamActivity.this,Tourist_TeamateActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
