package com.example.yiyou_newversion.ui;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.model.Data;

import static com.example.yiyou_newversion.utils.Utils.md5;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private LinearLayout root_linearlayout;
    private EditText edit_phoneNum;
    private EditText edit_password;
    private Button btn_login;
    private Button btn_register;
    private String phoneNum;
    private String password;
    private ProgressBar progressBar;

    //本次点击“返回键”的时刻
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //取消标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findAllView();

        setAllClickListener();
    }

    private void findAllView() {

        edit_phoneNum = findViewById(R.id.edit_phoneNum);
        edit_password = findViewById(R.id.edit_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        root_linearlayout = findViewById(R.id.root_linearlayout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setAllClickListener() {
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                //显示进度条
                progressBar.setVisibility(View.VISIBLE);

                //获取编辑框内的手机号和密码
                phoneNum = edit_phoneNum.getText().toString().trim();
                password = md5(edit_password.getText().toString().trim());

                if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(password)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "请输入手机号或密码！", Toast.LENGTH_SHORT).show();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Data data = new Data();
                            User user = data.getUserByPhoneNumInLogin(phoneNum);
                            //先判断手机号存在与否
                            if (user != null){
                                //再判断密码是否正确
                                if (user.getPassword() == password || user.getPassword().equals(password)){
                                    //接着判断用户的身份，0为游客，1为导游
                                    if (user.getIdentity() == 0){
                                        //Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "run: loginSuccessful<tourist>");

                                        //将登录成功的用户的信息保留下来
                                        Data.CurrenUerPhoneNum = user.getPhoneNum();
                                        Data.CurrenUerUsername = user.getUsername();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });

                                        startActivity(new Intent(LoginActivity.this,TouristMainActivity.class));
                                        finish();
                                    }else {
                                        Log.i(TAG, "run: loginSuccessful<guide>");

                                        //将登录成功的用户的信息保留下来
                                        Data.CurrenUerPhoneNum = user.getPhoneNum();
                                        Data.CurrenUerUsername = user.getUsername();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });

                                        startActivity(new Intent(LoginActivity.this,GuideActivity.class));
                                        finish();
                                    }
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                    Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                    Log.i(TAG, "run: passwordError");
                                }
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

                                Toast.makeText(LoginActivity.this, "该手机号不存在！", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "run: phoneNumFail");
                            }
                            Looper.loop();
                        }
                    }).start();
                }
                break;
            case R.id.btn_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
