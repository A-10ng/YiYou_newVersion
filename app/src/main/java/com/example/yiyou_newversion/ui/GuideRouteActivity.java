package com.example.yiyou_newversion.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Route;
import com.example.yiyou_newversion.model.Data;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.yiyou_newversion.utils.Utils.dp2px;

public class GuideRouteActivity extends AppCompatActivity {

    private static final String TAG = "GuideRouteActivity";
    private static final int HAS_NO_ROUTES = 0;
    private static final int HAS_SOME_ROUTES = 1;
    private static final int UPDATE_ROUTES = 2;
    //显示行程信息
    private TextView tv_route;
    private Data data = new Data();
    private LinearLayout linearLayout;
    private TextView tv_day1;
    private EditText et_day1;
    private ImageButton btn_create;
    private Button btn_save;
    // “+”按钮控件List
    private LinkedList<ImageButton> listIBTNAdd;
    //textView控件List
    private LinkedList<TextView> listTextView;
    // “+”按钮ID索引
    private int btnIDIndex = 1000;
    //edittext控件List
    private LinkedList<EditText> listEditText;
    // “-”按钮控件List
    private LinkedList<ImageButton> listIBTNDel;
    private int iETContentWidth;   // EditText控件宽度
    private int iETContentHeight;  // EditText控件高度
    private int iTVContentWidth;   //textView控件高度
    private int iTVContentHeight;   //textView控件高度
    private int iIBContentWidth;   //imageButton控件边长
    private float fDimRatio = 1.0f; // 尺寸比例（实际尺寸/xml文件里尺寸）
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAS_NO_ROUTES:
                    tv_route.setText("暂无行程信息！");
                    break;
                case HAS_SOME_ROUTES:
                    Route CurrentRoutes = (Route) msg.obj;

                    //分割字符串，显示路线
                    String[] num = CurrentRoutes.getRoute().split("_");
                    StringBuilder str_route = new StringBuilder();
                    for (int i = 0; i < num.length; i++) {
                        str_route.append("Day " + (i + 1) + "：" + num[i] + "\n");
                    }
                    tv_route.setText(str_route.toString());
                    break;
                case UPDATE_ROUTES:
                    Log.i(TAG, "CurrentRoute: "+Data.CurrentRoute);

                    //分割字符串，显示路线
                    String[] num1 = Data.CurrentRoute.split("_");
                    StringBuilder str_route1 = new StringBuilder();
                    for (int i = 0; i < num1.length; i++) {
                        str_route1.append("Day " + (i + 1) + "：" + num1[i] + "\n");
                    }
                    tv_route.setText(str_route1.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_route);

        findAllViews();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //获取当前队伍的旅游路线，用于显示出来
                Route CurrentRoutes = data.getCurrentRoutes();

                if (CurrentRoutes == null) {
                    handler.sendEmptyMessage(HAS_NO_ROUTES);
                } else {
                    Message message = Message.obtain();
                    message.obj = CurrentRoutes;
                    message.what = HAS_SOME_ROUTES;

                    handler.sendMessage(message);
                }
            }
        });
        thread.start();

        listIBTNAdd = new LinkedList<ImageButton>();
        listIBTNDel = new LinkedList<ImageButton>();
        listTextView = new LinkedList<TextView>();
        listEditText = new LinkedList<EditText>();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iETContentWidth = et_day1.getWidth();   // EditText控件宽度
                iETContentHeight = et_day1.getHeight();  // EditText控件高度
                iTVContentWidth = tv_day1.getWidth();   //textView控件高度
                iTVContentHeight = tv_day1.getHeight();   //textView控件高度
                iIBContentWidth = btn_create.getWidth();
                fDimRatio = iETContentWidth / 150;

                addContent(v);
            }
        });
        listIBTNAdd.add(btn_create);
        listIBTNDel.add(null);
        listEditText.add(et_day1);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = "";
                        Data.CurrentRoute = "none";

                        //检查输入框的内容是否为空，是则提示
                        for (int i = 0; i < listEditText.size(); i++) {
                            if (listEditText.get(i).getText().toString() == null ||
                                    listEditText.get(i).getText().toString().equals("")) {
                                Looper.prepare();
                                Toast.makeText(GuideRouteActivity.this, "请填好相关行程！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                break;
                            } else {
                                content += listEditText.get(i).getText().toString() + "_";
                                Data.CurrentRoute = content;
                            }
                        }

                        //如果当前用户已写好路线
                        if (!Data.CurrentRoute.equals("none")) {
                            String[] contens = Data.CurrentRoute.split("_");
                            //用户填的信息符合要求（eg:有三个框，只填了前两个，则只会提示，不会创建路线）
                            if (contens.length == listEditText.size()) {
                                //当前还没有行程，则新建行程
                                if (tv_route.getText().toString().trim().equals("暂无行程信息！") ||
                                        tv_route.getText().toString().trim() == "暂无行程信息！"){
                                    boolean crtRouteSuccessful = data.crtRouteSuccessful();
                                    //新建行程成功
                                    Looper.prepare();
                                    if (crtRouteSuccessful == true){
                                        Toast.makeText(GuideRouteActivity.this, "新建成功！", Toast.LENGTH_SHORT).show();
                                        handler.sendEmptyMessage(UPDATE_ROUTES);
                                    }
                                    //新建行程失败
                                    else {
                                        Log.i(TAG, "run: crtRouteFail!");
                                        Toast.makeText(GuideRouteActivity.this, "新建失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                                    }
                                    Looper.loop();
                                }
                                //当前已存在行程，则更新行程
                                else {
                                    boolean updateRouteSuccessful = data.updateRouteSuccessful();

                                    Looper.prepare();
                                    //更新行程成功
                                    if (updateRouteSuccessful == true){
                                        Toast.makeText(GuideRouteActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                        handler.sendEmptyMessage(UPDATE_ROUTES);
                                    }
                                    //更新行程失败
                                    else {
                                        Log.i(TAG, "run: ");
                                        Toast.makeText(GuideRouteActivity.this, "更新失败，发生未知错误！", Toast.LENGTH_SHORT).show();
                                    }
                                    Looper.loop();
                                }
                            }
                        }
                    }
                });
                thread.start();
            }
        });
    }

    @SuppressWarnings("ResourceType")
    private void addContent(View v) {
        if (v == null)
            return;

        int index = -1;
        for (int i = 0; i < listIBTNAdd.size(); i++) {
            if (listIBTNAdd.get(i) == v) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            //判断点击按钮的下方是否已经存在按钮，是则将按钮从最底部插入
            try {
                if (listIBTNAdd.get(index + 1) != null) {
                    index = listIBTNAdd.size();
                }
            } catch (Exception e) {
                index += 1;
            }


            //开始添加控件

            //创建外围linearlayout控件
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams ILayoutlayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ILayoutlayoutParams.setMargins(0, (int) (fDimRatio * 15), 0, 0);
            layout.setGravity(Gravity.CENTER);
            layout.setLayoutParams(ILayoutlayoutParams);

            //创建内部textView控件
            TextView tvContent = new TextView(this);
            tvContent.setText("Day " + (index + 1));
            layout.addView(tvContent);
            listTextView.add(tvContent);

            //创建内部edittext控件
            EditText etContent = new EditText(this);
            LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                    dp2px(this,150), dp2px(this,24)
            );
//            try {
////                Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
////                field.setAccessible(true);
////                field.set(etContent, R.drawable.et_cursor_style);
////            } catch (Exception e) {
////
////            }
            //etContent.setBackgroundResource(R.drawable.edit_register_bg);
            etContent.setLayoutParams(etParam);
            etContent.setPadding(0,10,0,10);
            etContent.setBackground(getResources().getDrawable(R.drawable.edit_register_bg));
            etParam.setMargins(dp2px(this,20), 0, 0, 0);
            layout.addView(etContent);
            listEditText.add(etContent);

            //创建“+”按钮
            ImageButton btnAdd = new ImageButton(this);
            LinearLayout.LayoutParams btnAddParam = new LinearLayout.LayoutParams(
                    iIBContentWidth,
                    iIBContentWidth
            );
            btnAddParam.setMargins((int) (fDimRatio * 10), 0, 0, 0);
            btnAdd.setLayoutParams(btnAddParam);
            btnAdd.setBackgroundResource(R.drawable.ibcreatestyle);
            btnAdd.setId(btnIDIndex);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addContent(v);
                }
            });
            layout.addView(btnAdd);
            listIBTNAdd.add(index, btnAdd);

            //创建“-”按钮
            ImageButton btnDelete = new ImageButton(this);
            LinearLayout.LayoutParams btnDeleteParam = new LinearLayout.LayoutParams(
                    iIBContentWidth,
                    iIBContentWidth
            );
            btnDeleteParam.setMargins((int) (fDimRatio * 10), 0, 0, 0);
            btnDelete.setLayoutParams(btnDeleteParam);
            btnDelete.setBackgroundResource(R.drawable.ibdeletestyle);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteContent(v);
                }
            });
            layout.addView(btnDelete);
            listIBTNDel.add(index, btnDelete);

            //将layout同它内部的所有控件都放在最外围的linearLayout容器里
            linearLayout.addView(layout);

            btnIDIndex++;
        }
    }

    private void deleteContent(View v) {
        if (v == null)
            return;

        //判断第几个“-”按钮触发了事件
        int index = -1;
        for (int i = 0; i < listIBTNDel.size(); i++) {
            if (listIBTNDel.get(i) == v) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            listIBTNAdd.remove(index);
            listIBTNDel.remove(index);
            listTextView.remove(index - 1);
            listEditText.remove(index);

            linearLayout.removeViewAt(index);
        }

        for (int i = 0; i < listTextView.size(); i++) {
            int num = 2 + i;
            listTextView.get(i).setText("Day " + num);
        }
    }

    private void findAllViews() {
        linearLayout = findViewById(R.id.linearLayout);
        tv_day1 = findViewById(R.id.tv_day1);
        et_day1 = findViewById(R.id.et_day1);
        btn_create = findViewById(R.id.btn_create);
        btn_save = findViewById(R.id.btn_saveroute);
        tv_route = findViewById(R.id.tv_route);
    }
}
