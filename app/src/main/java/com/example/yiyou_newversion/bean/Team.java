package com.example.yiyou_newversion.bean;

/**
 * Created by 龙世治 on 2019/3/15.
 */

public class Team {

    private int teamId;
    private String guidePhoneNum;
    private String travelDate;
    private String teamName;
    private String teamIntro;
    private String teamatePhoneNum;
    private String teamCode;
    private byte[] qrCode;

    public int getTeamId(){
        return teamId;
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

    public String getGuidePhoneNum() {
        return guidePhoneNum;
    }

    public void setGuidePhoneNum(String guidePhoneNum) {
        this.guidePhoneNum = guidePhoneNum;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public String getTeamatePhoneNum() {
        return teamatePhoneNum;
    }

    public void setTeamatePhoneNum(String teamatePhoneNum) {
        this.teamatePhoneNum = teamatePhoneNum;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getTeamIntro() {
        return teamIntro;
    }

    public void setTeamIntro(String teamIntro) {
        this.teamIntro = teamIntro;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }
}
