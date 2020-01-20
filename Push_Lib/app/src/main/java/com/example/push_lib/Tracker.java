package com.example.push_lib;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

public class Tracker extends Util {

    private Trans trans;
    private Context context;
    private Bundle bundle;
    protected String url = null;

    Tracker(Context context, Bundle bundle){
        super(context, bundle);
        this.context = context;
        this.bundle = bundle;
        trans = new Trans(this.context, this.bundle);
    }

    public String getAppId() {
        return getConfig("appId");
    }

    private void setAppId(String appId) {
        setConfig("appId", appId);
    }

    public String getAccountId() {
        return getConfig("accountId");
    }

    private void setAccountId(String accountId) {
        setConfig("accountId", accountId);
    }

    public String getLanguage() { return getConfig("language"); }

    private void setLanguage(String language) {
        setConfig("language", language);
    }

    public String getActivityTitle(){
        return getConfig("activity") != null ? getConfig("activity"): context.getClass().getName();
    }

    private void setActivityTitle(String activity){ setConfig("activity", activity); }

    public String getCampaignParameter(){
        return getConfig("campaignParameter");
    }

    private void setCampaignParameter(String campaignParameter){
        setConfig("campaignParameter", campaignParameter);
    }

    private void setHost(String host){

        if(host.contains("http://") || host.contains("https://")){
            host = host.substring(host.indexOf("://"));
        }
        setConfig("host", host);
    }

    public String getHost(){ return getConfig("host"); }

    public void trackEvent() {
        //현재 구동중인 서비스목록을 100개까지 획득해온다.
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);

        for(ActivityManager.RunningServiceInfo info : list) {
            String className = info.service.getClassName();
            String packageName = info.service.getPackageName();
        }
    }

    //trackpageView에 해당하는메소드
    public void trackView() throws JSONException {

        String vid = getVid();
        String uid = getUid();

        String ck = "";
        String ak = "0";
        String gk = "0";
        String cl = "";
        String cc = getSpref("___cc");
        String[] carr = cc.split(",");

        if(carr.length == 4){
            ck = carr[0];
            ak = carr[1];
            gk = carr[2];
            cl = carr[3];
        }

        JSONObject json = new JSONObject();

        if(trans.order.size() > 0){
            json = trans.getOrderrData();
        }
        else if(trans.member.size() > 0){
            json = trans.getMemberData();
        }
        else if(trans.claim.size() > 0){
            json = trans.getClaimData();
        }

        Log.d("JSON data is : ", String.valueOf(json));

        Date date = new Date();
        long visit = Long.parseLong(getConfig("visitTime") == "" ? "0" : getConfig("visitTime"));
        long sdt = (date.getTime() - visit) / 1000;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        String[] urlarr = {
                "https://",
                getHost(),
                "/access",
                "?vid=", vid,
                "&uid=", uid,
                "&dt=", encodeURIComponent(getActivityTitle()),
                "&cgk=", getAccess("cgk"),
                "&cgv=", getAccess("cgv"),
                "&cgc=", encodeURIComponent(getAccess("cgc")),
                "&sr=", dm.widthPixels + "X" + dm.heightPixels,
                "&la=", getLanguage(),
                "&pv=", preVisitDate,
                "&cid=", getConfig("campaign"),
                "&ck=", encodeURIComponent(ck),
                "&ak=", ak,
                "&gk=", gk,
                "&cl=", encodeURIComponent(cl),
                "&nv=", getConfig("newVisit"),
                "&cc=", encodeURIComponent(cc),
                "&aid=", getAccountId(),
                "&jd=", encodeURIComponent(String.valueOf(json)),
                "&eid=", getConfig("channel"),
                "&spv=", getConfig("pageView"),
                "&sdt=", String.valueOf(sdt),
                "&vp=",".", getSpref("___cvp"),".",
                "&up=",".", getSpref("___cup"),".",
                "&vc=",".", getSpref("___cvc"),"."
        };

        url =  join(urlarr, "");
        Log.d("url data is : ", url);

        //jstr = url;
    }

    //cancelMember와 setLanguage등 1개의 인자를 받는 push명령어 메소드
    @SuppressLint("LongLogTag")
    public void push(String methodName, String argument) {

        switch (methodName) {
            case "setHost":
                this.setHost(argument);
                break;
            case "setLanguage":
                this.setLanguage(argument);
                break;
            case "setAccountId":
                this.setAccountId(argument);
                break;
            case "setActivityTitle":
                this.setActivityTitle(argument);
                break;
            case "setAppId":
                this.setAppId(argument);
                break;
            case "setCampaignParameter":
                this.setCampaignParameter(argument);
                break;
            case "cancelMember":
                trans.type = 'W';
                trans.cancelMember(argument);
                break;
            default:
                Log.e("Error Message: Check your command push(", methodName + ", value)");
                break;

        }
    }

    //가변인자를 이용해 javaScript와 같이 동적인 배열로 받는다 사용자의 메소드 명으로 switch문을 사용해 각각의 메소드 호출
    @SuppressLint("LongLogTag")
    public void push(String methodName, String... arguments) {

        switch (methodName) {
            case "addMember":
                trans.type = 'C';
                trans.setMember(arguments);
                break;
            case "updateMember":
                trans.type = 'U';
                trans.setMember(arguments);
                break;
            case "addItem":
                trans.addItem(arguments);
                break;
            case "addClaim":
                trans.type = 'C';
                trans.addClaim(arguments);
                break;
            case "addTrans":
                trans.type = 'C';
                trans.addTrans(arguments);
                break;
            case "setCustomVar":
                trans.setCustomVar(arguments);
                break;
            case "setConversion":
                trans.setConversion(arguments);
                break;
            default:
                Log.e("Error Message: Check your command push(", methodName + ", values)");
                break;
        }
    }

    //자바의 URLENCODER를 자바스크립트와 동일한 결과가 나오게 변경
    private static String encodeURIComponent(String s)
    {
        String result = new String();

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
