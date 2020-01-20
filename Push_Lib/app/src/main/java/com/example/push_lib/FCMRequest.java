package com.example.push_lib;

import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;

public class FCMRequest {

    public FCMRequest(){
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        //이미 서비스중인 어플에 push 서비스 추가시 토큰생성
        String token = FirebaseInstanceId.getInstance().getToken();

        if(token == null){
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FCMRequest(String Package, String Activity, int Logo, Context context){
        MyFirebaseMessagingService mfms = new MyFirebaseMessagingService(context);

        mfms.setLogo(Logo);
        mfms.setActivity(Package, Activity);
    }

}