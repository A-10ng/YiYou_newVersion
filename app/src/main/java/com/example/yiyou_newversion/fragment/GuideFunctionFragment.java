package com.example.yiyou_newversion.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.model.Data;
import com.example.yiyou_newversion.ui.GuideRouteActivity;
import com.example.yiyou_newversion.ui.Guide_TeamateActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.yiyou_newversion.utils.Utils.byte2bitmap;


/**
 * Created by 龙世治 on 2019/3/14.
 */

public class GuideFunctionFragment extends Fragment {

    private View view;
    private Data data = new Data();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_TEAMINTRO_SUCCESSFUL:
                    String teamIntro = (String) msg.obj;
                    tv_teamIntro.setText(teamIntro);
                    break;
                case UPDATE_TEAMINTRO_FAIL:
                    Toast toast = Toast.makeText(getContext(), "更新失败！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    break;
                case CHECK_TEAMINTRO:
                    String Intro = (String) msg.obj;
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
                    dialog1.setTitle("查看队伍简介");
                    if (Intro == null || Intro.equals("")) {
                        dialog1.setMessage("暂无简介");
                    }
                    dialog1.setMessage(Intro);
                    dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog1.show();
                    break;
                case UPDATE_UI:
                    User CurrentGuide = (User) CurrentList.get(0);
                    Team CurrentTeam = (Team) CurrentList.get(1);

                    String travelDate = CurrentTeam.getTravelDate();
                    String Info= CurrentTeam.getTeamIntro();
                    String guideName = CurrentGuide.getUsername();

                    if (travelDate == null || travelDate.equals("")) {
                        tv_travelDate.setText("暂无时间");
                    } else {
                        tv_travelDate.setText(travelDate);
                    }

                    if (Info == null || Info.equals("")) {
                        tv_teamIntro.setText("暂无简介");
                    } else {
                        tv_teamIntro.setText(Info);
                    }

                    iv_guideAvatar.setImageBitmap(byte2bitmap(CurrentGuide.getAvatar()));
                    tv_guideName.setText(guideName);
                    break;
            }
        }
    };

    /**
     * 上方显示区域模块
     */
    //点击编辑队伍简介
    private TextView click2edit;
    //点击查看队伍简介
    private TextView click2check;
    //显示队伍简介
    private TextView tv_teamIntro;
    //显示队伍名称
    private TextView tv_TeamName;
    //显示队伍旅游时间
    private TextView tv_travelDate;
    //显示队伍导游名称
    private TextView tv_guideName;
    //显示导游头像
    private ImageView iv_guideAvatar;
    private final static int UPDATE_TEAMINTRO_SUCCESSFUL = 0;
    private final static int UPDATE_TEAMINTRO_FAIL = 1;
    private final static int CHECK_TEAMINTRO = 2;
    private final static int UPDATE_UI = 3;
    //当前队伍和导游
    private List<Object> CurrentList = new ArrayList<>();

    /**
     * 功能区域模块
     */
    //队伍成员功能
    private ImageView fun_teamate;
    //旅游行踪功能
    private ImageView fun_guide_route;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_function, container, false);
        /**
         * 上方显示模块
         */
        click2edit = view.findViewById(R.id.click2edit);
        click2check = view.findViewById(R.id.click2check);
        tv_teamIntro = view.findViewById(R.id.teamIntro);
        tv_TeamName = view.findViewById(R.id.TeamName);
        tv_travelDate = view.findViewById(R.id.travelDate);
        tv_guideName = view.findViewById(R.id.guideName);
        iv_guideAvatar = view.findViewById(R.id.guideAvatar);

        /**
         * 功能模块
         */
        fun_teamate = view.findViewById(R.id.teamate);
        fun_guide_route = view.findViewById(R.id.guide_route);

        //点击跳转至队伍成员功能界面
        fun_teamate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Guide_TeamateActivity.class);
                startActivity(intent);
            }
        });

        //点击跳转至旅游行踪功能界面
        fun_guide_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GuideRouteActivity.class);
                startActivity(intent);
            }
        });

        //点击编辑队伍简介
        click2edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                final EditText editText_teamIntro = new EditText(getActivity());
                dialog.setTitle("在下方编辑简介").setView(editText_teamIntro);
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                String teamIntro = editText_teamIntro.getText().toString();
                                if (TextUtils.isEmpty(teamIntro)){
                                    Toast toast = Toast.makeText(getContext(), "请输入相关内容", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER,0,0);
                                    toast.show();
                                }else {
                                    if (data.isUpdateTeamIntroSuccessful(teamIntro)){
                                        Message msg = Message.obtain();
                                        msg.obj = teamIntro;
                                        msg.what = UPDATE_TEAMINTRO_SUCCESSFUL;
                                        handler.sendMessage(msg);
                                    }else {
                                        handler.sendEmptyMessage(UPDATE_TEAMINTRO_FAIL);
                                    }
                                }
                                Looper.loop();
                            }
                        });
                        thread.start();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //点击查看队伍简介
        click2check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Team CurrentTeam = data.getCurrentTeamInGuideFun();

                        Message msg = Message.obtain();
                        String intro = CurrentTeam.getTeamIntro();
                        msg.obj = intro;
                        msg.what = CHECK_TEAMINTRO;
                        handler.sendMessage(msg);
                    }
                });
                thread.start();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //更新导游头像，导游名称，队伍名称，旅游时间，队伍简介
    public void updateUI() {
        tv_TeamName.setText(Data.CurrentTeamName);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //获取当前的导游和队伍
                User CurrentGuide = data.getCurGuideInGuideFun();
                Team CurrentTeam = data.getCurrentTeamInGuideFun();
                CurrentList.clear();
                CurrentList.add(CurrentGuide);
                CurrentList.add(CurrentTeam);

                Message msg = Message.obtain();
                msg.what = UPDATE_UI;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }
}
