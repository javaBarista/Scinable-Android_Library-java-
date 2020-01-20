package com.example.push_lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import org.json.JSONException;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.Response;
import static android.os.Looper.getMainLooper;

public class Scinable extends Tracker{

    public Scinable(Context context, Bundle bundle){ super(context, bundle); }

    //trackView를 호출전용 push
    @SuppressLint("LongLogTag")
    public void push(String trigger) {
        //사용자가 push("trackView") 명령어를 입력할 시
        if (trigger.equals("trackView")) {
            try {
                this.trackView();   //trackView메소드 실행
                HttpRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //잘못된 입력일 시 에러메시지 출력
            Log.e("Error Message: Check your command push(", trigger + ")");
        }
    }

    private void HttpRequest(){

        OkHttpClient requestValidationChk = new OkHttpClient();

        String url = "https://" + getHost();

        requestValidationChk.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        String result = response.toString();

                        Log.d("response date => ", result);

                    }
                });
            }
        });
    }
}