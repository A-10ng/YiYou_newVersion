package com.example.yiyou_newversion.bean;

/**
 * Created by 龙世治 on 2019/3/24.
 */

public class Route{
    private int routeId;
    private String guidePhoneNum;
    private String teamName;
    private String route;

    public int getRouteId(){
        return routeId;
    }

    public void setRouteId(int routeId){
        this.routeId = routeId;
    }

    public String getGuidePhoneNum() {
        return guidePhoneNum;
    }

    public void setGuidePhoneNum(String guidePhoneNum) {
        this.guidePhoneNum = guidePhoneNum;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
