package com.example.yiyou_newversion.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Route;
import com.example.yiyou_newversion.model.Data;

import java.util.List;

public class TouristRouteActivity extends AppCompatActivity {

    private static final int HAS_NO_ROUTE = 0;
    private static final int HAS_SOME_ROUTES = 1;
    private Data data = new Data();
    private LinearLayout linearLayout;
    //当前的路线
    private List<Route> routes;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_route);

        findAllViews();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HAS_NO_ROUTE:
                        TextView textView = new TextView(TouristRouteActivity.this);
                        textView.setText("您的导游还未发布行程信息！");
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        textView.setLayoutParams(layoutParams);
                        linearLayout.addView(textView);
                        break;
                    case HAS_SOME_ROUTES:
                        String[] num = routes.get(0).getRoute().split("_");
                        TextView[] Views = new TextView[num.length];
                        for (int i = 0; i < num.length; i++) {
                            Views[i] = new TextView(TouristRouteActivity.this);
                            Views[i].setTextSize(20);
                            Views[i].setText("Day " + (i + 1) + "：" + num[i]);
                            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            para.setMargins(0, 30, 0, 0);
                            Views[i].setLayoutParams(para);
                            linearLayout.addView(Views[i]);
                        }
                        break;

                }
            }
        };


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    routes = data.getRoutes();

                    //如果当前导游还没发布行程信息
                    if (routes.size() == 0) {
                        handler.sendEmptyMessage(HAS_NO_ROUTE);
                    } else {
                        handler.sendEmptyMessage(HAS_SOME_ROUTES);
                    }
                } catch (Exception e) {
                    Toast.makeText(TouristRouteActivity.this, "是不是没网了呀...", Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            }
        });
        thread.start();
    }

    private void findAllViews() {
        linearLayout = findViewById(R.id.activity_tourist_route);
    }
}
