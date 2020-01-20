package com.example.push_lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Util extends Config{

    private Bundle bundle;
    private SharedPreferences spref;
    private SharedPreferences.Editor editor;
    private String paramValues;

    public Util(){
        setAccess("req", null);
        setAccess("url", null);
        setAccess("title", null);
        setAccess("id", "");
        setAccess("groupId", "");
        setAccess("type", "");
        setAccess("cgk", ""); //conversion goal key
        setAccess("cgv", ""); //conversion goal value
        setAccess("cgc", ""); //conversion goal custom
    }

    public Util(Context context, Bundle bundle){
        this();
        this.bundle = bundle;

        spref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = spref.edit();
    }

    protected void setR(String cname, String val, long expire){
        String c = getSpref(cname);

        if(c != ""){
            String[] cs = c.split("\\.");
            String[] newcs = new String[cs.length + 1];

            int i = 1;
            if(cs.length > 10){
                while(i < cs.length){
                    newcs[i - 1] = cs[i];
                    i++;
                }
            }
            newcs[i] = val;

            c = join(cs ,".");

        }
        else{
            c = val;
        }
        this.setSpref(cname, c, expire);
    }

    private String today(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    private int getAge(String birthday){
        Date today = new Date();
        int resultday = (today.getYear() + 1900)*10000+today.getMonth()*100+100+today.getDate();

        return (int) Math.floor((resultday-Integer.parseInt(birthday))/10000);
    }

    protected int getAgeGroupKey(String birthday){
        int age = getAge(birthday);

        if(age > 90) return 9;

        return age / 10 < 1 ? 2 : age / 10 + 1;
    }

    //setCookie와 같은 기능
    //입력받은 name 그대로 SharedPreference에 저장해주고.
    // 이름에 _를 넣어 현재 시간을 밀리세컨드로 변경하여 입력된 만료시간과 더해 저장
    protected void setSpref(String name, String value, long expire){
        editor.putString(name, value);
        editor.putLong(name + "_", System.currentTimeMillis() + expire);
        editor.commit();
        //입력된 변수와 해당변수의 만료시간이 잘 들어 왔는지 체크
        Log.d(name + " data is => ", value);
        Log.d(name + " expire is => ", String.valueOf(System.currentTimeMillis() + expire));
    }

    //저장되어있는 값의 만료시간이 아직 유효하다면 지정된 변수를 리턴
    //그렇지 않다면 빈 값 리턴
    protected String getSpref(String name){
        long expires = spref.getLong(name + "_", 0L);

        return System.currentTimeMillis() < expires ? spref.getString(name, "") : "";
    }

    //자바스크림트와 동일하게 구현
    private String createUUID(){
        return String.valueOf(Math.round(2147483647 * Math.random()));
    }

    protected String setCU(String uid, String cday, String freq){

        String cu = uid + "." + cday + "." + freq;

        this.setSpref("___cu", cu, cuExpire);

        return cu;
    }

    protected String[] getCU(){
        boolean needSet = false;
        String[] cuArr = new String[3];
        String cu = this.getSpref("___cu");

        if(cu != null){
            cuArr = cu.split("\\.");
            if(cuArr.length != 3){
                needSet = true;
                cuArr = new String[3];
                cuArr[0] = this.createUUID();
                cuArr[1] = this.today();
                cuArr[2] = "1";
            }
        }
        else{
            needSet = true;
            cuArr[0] = this.createUUID();
            cuArr[1] = this.today();
            cuArr[2] = "0";
        }

        if(needSet){
            this.setCU(cuArr[0], cuArr[1], cuArr[2]);
        }

        return cuArr;
    }

    protected String getUid(){
        if(uid == null){
            String[] cuArr = getCU();
            this.uid = cuArr[0];
            this.preVisitDate = cuArr[1];
            this.frequency = cuArr[2];
        }

        return this.uid;
    }

    protected String getVid() {
        if(vid != null){
            return vid;
        }
        else{
            this.getUid();

            String cv = getSpref("___cv");
            String[] cvArr = new String[6];
            String cookieCampaign = "";
            String cookieChannel = ""; // 추후 변수명도 변경예정

            if(cv != ""){
                cvArr = cv.split("\\.");

                if(cvArr.length > 2){
                    //캠페인정보
                    cookieCampaign = cvArr[2];
                }
                if(cvArr.length > 5){
                    //채널정보
                    cookieChannel = cvArr[5] ;
                }
            }

            String sciCampaign = "";

            if(!getConfig("campaign").equals("")){
                sciCampaign = getConfig("campaign");
            }
            else{
                if(bundle != null) {
                    sciCampaign = bundle.getString("eciCampaign") == null ? "empty" : bundle.getString("eciCampaign");
                    Log.d("sciCampaign is => ", sciCampaign);
                    if (sciCampaign == "" && bundle.getString("campaign") != null) {
                        sciCampaign = bundle.getString("campaign");
                    }
                }
                else if(bundle == null){
                    Log.d("sciCampaign is => ", "null");
                }
            }

            if(sciCampaign != ""){
                if(sciCampaign != cookieCampaign){
                    cv = null;
                }
                setConfig("campaign", sciCampaign);
            }
            else{
                if(cookieCampaign != ""){
                    setConfig("campaign", cookieCampaign);
                }
            }

            String sciChannel = "";
            if(getConfig("channel") != ""){
                sciChannel = getConfig("channel");
            }
            else{
                if(bundle != null) {
                    sciChannel = bundle.getString("channel") == null ? "empty" : bundle.getString("channel");;
                    Log.d("sciChannel is => ", sciChannel);
                }
                else if(bundle == null){
                    Log.d("sciChannel is => ", "null");
                }
            }

            if(sciChannel != ""){
                if(sciChannel != cookieChannel){
                    cv = null;
                }
                setConfig("channel", sciChannel);
            }
            else{
                if(cookieChannel != ""){
                    setConfig("channel", cookieChannel);
                }
            }
            //////////////////
            //new visit
            //////////////////

            if(cv == ""){
                this.frequency = String.valueOf(Integer.parseInt(frequency) + 1);
                this.setCU(uid, this.today(), this.frequency);

                Date d = new Date();
                //create cv
                vid = this.createUUID();
                setConfig("visitTime", String.valueOf(d.getTime()));
                setConfig("pageView", "1");
                setConfig("newVisit", "1");

                cv = vid + "." + preVisitDate + "." + sciCampaign + "." + getConfig("visitTime") + ".1." + sciChannel;
            }
            else{
                vid = cvArr[0];

                if(cvArr.length < 5){
                    Date d = new Date();
                    setConfig("visitTime", String.valueOf(d.getTime()));
                    setConfig("pageView", "1");
                }
                else{
                    setConfig("visitTime", cvArr[3]);
                    setConfig("pageView", String.valueOf(Integer.parseInt(cvArr[4]) + 1));
                }

                cv = vid + "." + preVisitDate + "." + sciCampaign + "." + getConfig("visitTime") + "." + getConfig("pageView") + "." + sciChannel;
            }

            setSpref("___cv", cv, cvExpire);
            return vid;
        }
    }

    public String join(String[] args, String point){

        String result = args[0];

        for(int i = 1; i < args.length; i++){
            result += (point + args[i]);
        }
        return result;
    }
}
