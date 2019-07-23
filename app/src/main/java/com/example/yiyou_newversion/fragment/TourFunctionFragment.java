package com.example.yiyou_newversion.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.model.Data;
import com.example.yiyou_newversion.ui.TouristRouteActivity;
import com.example.yiyou_newversion.ui.Tourist_TeamateActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.yiyou_newversion.utils.Utils.byte2bitmap;


/**
 * Created by 龙世治 on 2019/3/23.
 */

public class TourFunctionFragment extends Fragment {

    private View view;
    private Data data = new Data();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHECK_TEAMINTRO:
                    String Intro = (String) msg.obj;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("查看队伍简介");
                    if (Intro == null || Intro.equals("")) {
                        dialog.setMessage("暂无简介");
                    }
                    dialog.setMessage(Intro);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case UPDATE_UI:
                    Team CurrentTeam = (Team) list.get(0);
                    User CurrentGuide = (User) list.get(1);

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
    private TextView click2check;
    private TextView tv_TeamName;
    private TextView tv_travelDate;
    private TextView tv_teamIntro;
    private TextView tv_guideName;
    private ImageView iv_guideAvatar;
    private final static int CHECK_TEAMINTRO = 0;
    private final static int UPDATE_UI = 1;
    private List<Object> list = new ArrayList<>();

    /**
     * 功能区域模块
     */
    private ImageView teamate;
    private ImageView route;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stufunction,container,false);

        /**
         * 上方显示区域模块初始化
         */
        click2check = view.findViewById(R.id.click2check);
        tv_TeamName = view.findViewById(R.id.TeamName);
        tv_travelDate = view.findViewById(R.id.travelDate);
        tv_teamIntro = view.findViewById(R.id.teamIntro);
        tv_guideName = view.findViewById(R.id.guideName);
        iv_guideAvatar = view.findViewById(R.id.guideAvatar);

        /**
         * 功能区域模块初始化
         */
        teamate = view.findViewById(R.id.teamate);
        route = view.findViewById(R.id.route);

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

        //点击跳转至队伍成员功能界面
        teamate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Tourist_TeamateActivity.class);
                startActivity(intent);
            }
        });

        //点击跳转至旅游行踪功能界面
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TouristRouteActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void updateUI() {
        tv_TeamName.setText(Data.CurrentTeamName);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //第一个元素是Team类型的CurrentTeam
                //第二个元素是User类型的CurrentUser
                list = data.getCurTeamAndGuideInTourFun();

                Message msg = Message.obtain();
                msg.what = UPDATE_UI;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }
}
