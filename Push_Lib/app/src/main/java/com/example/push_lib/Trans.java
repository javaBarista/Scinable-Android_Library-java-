package com.example.push_lib;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Trans {
    Util util;
    protected char type;
    protected ArrayList<String> order;
    protected ArrayList<String[]> items;
    protected ArrayList<String> member;
    protected ArrayList<String[]> claim;

    protected Trans(Context context, Bundle bundle) {
        util = new Util(context, bundle);
        type = 'C';
        order = new ArrayList<>();
        items = new ArrayList<>();
        member = new ArrayList<>();
        claim = new ArrayList<>();
    }

    protected void addItem(String[] items) {
        String[] r = new String[17]; //12개의 인자 + 가변적인 4개의인자 + groupid

        int i;
        for (i = 0; i < 11; i++) {
            r[i] = items[i];
        }

        JsonObject jsonObj = null;
        if (items.length == 11) {
            JsonElement jsonElement = new JsonParser().parse(items[10]);
            jsonObj = jsonElement.getAsJsonObject();
        }
        for(int j = 1; j < 6; j++, i++){
            String jo = jsonObj.get(String.valueOf(i)) != null ? jsonObj.get(String.valueOf(i)).toString() : "";
            if(jo.contains("\"")) {
                jo.replaceAll("\"", "");
            }
            r[i] = (jo);
        }
        if (items.length == 12) {
            r[i] = items[11];
        }

        this.items.add(r);
    }

    protected void setMember(String[] member) {
        for (int i = 0; i < 9; i++) {
            this.member.add(member[i]);
        }
        this.member.add("1"); //valid_yn

        //String[] jo = new String[5];
        JsonObject jsonObj = null;
        if (member.length == 10) {
            /* 처음 스트링 형식으로 사용한 방법
            member[9] = member[9].substring(member[9].indexOf("{") + 1, member[9].lastIndexOf("}")); //9번째 입력의 처음과 마지막에 위치한 '{', '}'를 제거
            jo = member[9].split(", "); //','로 String을 나눠 배열로 만든다.
             */
            JsonElement jsonElement = new JsonParser().parse(member[9]);
            jsonObj = jsonElement.getAsJsonObject();
        }

        for(int i = 1; i < 6; i++){
            String jo = jsonObj.get(String.valueOf(i)) != null ? jsonObj.get(String.valueOf(i)).toString() : "";
            if(jo.contains("\"")) {
                jo.replaceAll("\"", "");
            }
            this.member.add(jo);
        }

        /* 스트링 형식일때
        for (int i = 0; i < jo.length; i++) {
            if(jo[i] != null) {
                this.member.add(jo[i].substring(jo[i].indexOf("'") + 1, jo[i].lastIndexOf("'")));
            }
        }
        for (int i = 0; i < 5 - jo.length; i++) {
            this.member.add("");
        }
         */
    }

    protected void cancelMember(String member) {
        this.member.add(member);
    }

    protected void addTrans(String[] order) {
        for (int i = 0; i < 8; i++) {
            this.order.add(order[i]);
        }
        this.order.add("1"); //valid_yn

        //String[] jo = new String[5];
        JsonObject jsonObj = null;

        if (order.length == 9) {
            JsonElement jsonElement = new JsonParser().parse(order[8]);
            jsonObj = jsonElement.getAsJsonObject();
            /*
            order[8] = order[8].substring(order[8].indexOf("{") + 1, order[8].lastIndexOf("}"));
            jo = order[8].split(", ");
             */
        }
        for(int i = 1; i < 6; i++){
            String jo = jsonObj.get(String.valueOf(i)) != null ? jsonObj.get(String.valueOf(i)).toString() : "";
            if(jo.contains("\"")) {
                jo.replaceAll("\"", "");
            }
            this.order.add(jo);
        }
        /*
        for (int i = 0; i < jo.length; i++) {
            if(jo[i] != null) {
                this.order.add(jo[i].substring(jo[i].indexOf("'") + 1, jo[i].lastIndexOf("'")));
            }
        }
        for (int i = 0; i < 5 - jo.length; i++) {
            this.order.add("");
        }
        */
    }

    protected void addClaim(String[] claim) {
        String[] r = new String[17]; //12개의 인자 + 가변적인 5개의인자

        int i;
        for (i = 0; i < 12; i++) {
            r[i] = claim[i];
        }

        //String[] jo = new String[5];

        JsonObject jsonObj = null;
        if (claim.length == 13) {
            JsonElement jsonElement = new JsonParser().parse(claim[12]);
            jsonObj = jsonElement.getAsJsonObject();
            /*
            claim[12] = claim[12].substring(claim[13].indexOf("{") + 1, claim[13].lastIndexOf("}"));
            jo = claim[12].split(", ");
             */
        }
        /*
        for (int j = 0; j < jo.length; i++, j++) {
            if(jo[i] != null) {
                r[i] = (jo[i].substring(jo[i].indexOf("'") + 1, jo[i].lastIndexOf("'")));
            }
        }
        while (i < 17) {
            r[i++] = "";
        }
        */
        for(int j = 1; j < 6; j++, i++){
            String jo = jsonObj.get(String.valueOf(i)) != null ? jsonObj.get(String.valueOf(i)).toString() : "";
            if(jo.contains("\"")) {
                jo.replaceAll("\"", "");
            }
            r[i] = (jo);
        }

        this.claim.add(r);
    }

    protected void setCustomVar(String[] memberInfo){
        String con = "";
        for(int i = 0; i < memberInfo.length; i++){
            if( i == 0){
                con = memberInfo[i];
            }
            else if (i == 1){
                con =  con + "." + util.getAgeGroupKey(memberInfo[i]);
            }
            else{
                con = con + "." + memberInfo[i];
            }
        }
        util.setSpref("___cc", con, util.ccExpire);
    }

    protected void setConversion(String[] p){
        if(p.length == 1){
            util.setAccess("cgk", p[0]);
            util.setAccess("cgv", "1");
        }
        else if(p.length == 2){
            util.setAccess("cgk", p[0]);
            util.setAccess("cgv", p[1]);
        }
        else if(p.length == 3){
            util.setAccess("cgk", p[0]);
            util.setAccess("cgv", p[1]);

            JsonElement jsonElement = new JsonParser().parse(p[2]);
            JsonObject jsonObj = jsonElement.getAsJsonObject();
            String[] arr = new String[5];
            for(int i = 1; i < 6; i++){
                String jo = jsonObj.get(String.valueOf(i)) != null ? jsonObj.get(String.valueOf(i)).toString() : "";
                if(jo.contains("\"")) {
                    jo.replaceAll("\"", "");
                }
                arr[i - 1] = (jo);
            }
/*
            p[2] = p[2].substring(p[2].indexOf("{") + 1, p[2].lastIndexOf("}"));
            String[] temp = p[2].split(", ");


            for(int i = 0; i < temp.length; i++){
                for(int j = i + 1; j < 6; j++) {
                    if (temp[i].charAt(0) == (char)j + '0') {
                        arr[j - 1] = temp[i].substring(temp[i].indexOf("'") + 1, temp[i].lastIndexOf("'"));
                        break;
                    } else {
                        arr[j - 1] = "";
                    }
                }
            }
 */
            util.setAccess("cgc",util.join(arr,";"));
        }
        Log.d("cgc is a : ", util.getAccess("cgc").toString());
        util.setR("___cvc", p[0], util.cvExpire);
    }

    //----------------Getter------------------------------------------------------

    protected JSONObject getOrderrData() throws JSONException {

        String cz = util.getSpref("___cz");

        if (cz == this.order.get(0)) {
            return null;
        } else {
            util.setSpref("___cz", this.order.get(0), util.czExpire);
        }

        List<String> arr = new ArrayList<>();
        for (int i = 0; i < this.items.size(); i++) {
            String rowArr = String.valueOf(this.type) ;
            arr.add(rowArr + this.order + this.items.get(i));
        }

        JSONObject jstr = new JSONObject();
        jstr.put("type", "order");
        jstr.put("data", arr);

        this.order.clear();
        this.items.clear();

        return jstr;
    }

    protected JSONObject getMemberData() throws JSONException {

        if (this.type == 'c') {
            String cz = util.getSpref("___cz");

            if (cz == this.member.get(0)) {
                return null;
            } else {
                util.setSpref("___cz", this.member.get(0), util.czExpire);
            }
        }

        List<String> arr = new ArrayList<>();
        String rowArr = String.valueOf(this.type);
        arr.add(rowArr.concat(String.valueOf(this.member)));

        JSONObject jstr = new JSONObject();
        jstr.put("type", "member");
        jstr.put("data", arr);

        this.member.clear();

        return jstr;
    }

    protected JSONObject getClaimData() throws JSONException{

        List<String> arr= new ArrayList<>();

        for(int i = 0; i < this.claim.size(); i++){
            String rowarr = String.valueOf(this.type);
            arr.add(rowarr.concat(String.valueOf(this.claim.get(i))));
        }

        JSONObject jstr = new JSONObject();
        jstr.put("type", "claim");
        jstr.put("data", arr);

        this.claim.clear();

        return jstr;
    }
}
