package com.example.yiyou_newversion.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.model.Data;
import com.example.yiyou_newversion.ui.JoinTeamActivity;
import com.example.yiyou_newversion.ui.TouristMainActivity;

import java.util.List;


/**
 * Created by 龙世治 on 2019/3/23.
 */

public class TourHomeFragment extends Fragment{

    private final static int DISPLAY_TEAM = 0;
    private View view;
    private LinearLayout linearLayout;
    private Button joinTeam;
    private Data data = new Data();

    //当前用户所加入的队伍
    private List<Team> teams;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stuhome,container,false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DISPLAY_TEAM:
                        //如果用户已加入过队伍，则动态新增按钮
                        if (teams.size() != 0){
                            Button btn[] = new Button[teams.size()];
                            for (int i = 0; i < teams.size(); i++) {
                                btn[i] = new Button(getActivity());
                                btn[i].setId(1000 + i);
                                btn[i].setBackgroundResource(R.drawable.btn_team);
                                btn[i].setAllCaps(false);
                                btn[i].setText(teams.get(i).getTeamName());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(700, 120);
                                layoutParams.setMargins(0, 65, 0, 0);
                                linearLayout.addView(btn[i], layoutParams);
                            }
                            for (int j = 0; j < teams.size(); j++) {
                                btn[j].setTag(j);
                                btn[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //将当前选择的队伍作为功能界面的队伍
                                        int i = (Integer) v.getTag();
                                        Data.CurrentTeamName = teams.get(i).getTeamName();

                                        //更新功能页面的信息
                                        TouristMainActivity activity = (TouristMainActivity) getActivity();
                                        NoScrollViewPager viewPager = activity.findViewById(R.id.mViewPager);
                                        TourFunctionFragment tourFunctionFragment = (TourFunctionFragment)getActivity().getSupportFragmentManager()
                                                .findFragmentByTag("android:switcher:"+R.id.mViewPager+":1");
                                        tourFunctionFragment.updateUI();
                                        viewPager.setCurrentItem(1);
                                    }
                                });
                            }
                        }
                        break;
                }
            }
        };

        joinTeam = view.findViewById(R.id.btn_joinTeam);
        joinTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data.CurrentTeamName = "none";
                Intent intent = new Intent(getActivity(), JoinTeamActivity.class);
                startActivity(intent);
            }
        });

        linearLayout = view.findViewById(R.id.linearLayout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取当前用户所加入的队伍
                teams = data.getCurTeamsInTourist();

                //转至主线程更新UI
                handler.sendEmptyMessage(DISPLAY_TEAM);
            }
        }).start();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
