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
import com.example.yiyou_newversion.ui.CreateTeamActivity;
import com.example.yiyou_newversion.ui.GuideMainActivity;
import com.example.yiyou_newversion.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 龙世治 on 2019/3/14.
 */

public class GuideHomeFragment extends Fragment {

    //创建队伍按钮
    private Button btn_crtTeam;
    private LinearLayout linearLayout;
    private View view;
    private Data data = new Data();

    private final static int DISPLAY_TEAM = 0;
    private List<Team> teams = new ArrayList<>();
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DISPLAY_TEAM:
                        if (teams.size() != 0) {
                            Button btn[] = new Button[teams.size()];
                            for (int i = 0; i < teams.size(); i++) {
                                btn[i] = new Button(getActivity());
                                btn[i].setId(1000 + i);
                                btn[i].setBackgroundResource(R.drawable.btn_team);
                                btn[i].setText(teams.get(i).getTeamName());
                                btn[i].setAllCaps(false);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(700, 120);
                                layoutParams.setMargins(0, Utils.dp2px(getContext(),15), 0, Utils.dp2px(getContext(),15));
                                linearLayout.addView(btn[i], layoutParams);
                            }
                            for (int j = 0; j < teams.size(); j++) {
                                btn[j].setTag(j);
                                btn[j].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int i = (Integer) v.getTag();
                                        Data.CurrentTeamName = teams.get(i).getTeamName();

                                        //更新功能页面的信息
                                        GuideMainActivity activity = (GuideMainActivity) getActivity();
                                        NoScrollViewPager viewPager = activity.findViewById(R.id.mViewPager);
                                        GuideFunctionFragment guideFunctionFragment = (GuideFunctionFragment)getActivity().getSupportFragmentManager()
                                                .findFragmentByTag("android:switcher:"+R.id.mViewPager+":1");
                                        guideFunctionFragment.updateUI();
                                        viewPager.setCurrentItem(1);
                                    }
                                });
                            }
                        }
                        break;
                }
            }
        };

        btn_crtTeam = view.findViewById(R.id.btn_crtTeam);
        btn_crtTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateTeamActivity.class);
                startActivity(intent);
            }
        });

        linearLayout = view.findViewById(R.id.linearLayout);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                teams = data.getCurTeamsInGuide();

                //转至主线程更新UI
                handler.sendEmptyMessage(DISPLAY_TEAM);
            }
        });
        thread.start();
        return view;
    }
}
