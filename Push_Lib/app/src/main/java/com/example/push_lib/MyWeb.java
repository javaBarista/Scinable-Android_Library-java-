package com.example.push_lib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWeb extends AppCompatActivity {

    private WebView mWebView; // 웹뷰 선언
    private WebSettings mWebSettings; //웹뷰세팅
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent webIntent = getIntent();
        url = webIntent.getStringExtra("userUrl");

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings(); //세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        mWebSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        mWebSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(false); // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        mWebView.loadUrl(url); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
    }

    @Override
    public void onBackPressed(){
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true ;

            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
