package com.example.push_lib;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 각자 핸드폰 토큰값을 핸드폰으로 전송한다
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("id", "Hiro")
                .add("Token", token)
                .add("Platform", "android")
                .build();

        Request request = new Request.Builder()
                .url("http://nobles1030.cafe24.com/register.php")   //DB에 토큰값을 올려준다.
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}