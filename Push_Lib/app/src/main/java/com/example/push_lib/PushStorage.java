package com.example.push_lib;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushStorage extends AppCompatActivity {
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false, isChecked = false;
    RecyclerView mRecyclerView;
    RecyclerAdapter mAdapter;
    ArrayList<RecyclerItem> mList = new ArrayList<RecyclerItem>();
    ArrayList<RecyclerItem> copyList = new ArrayList<RecyclerItem>();
    ImageView dialogImg;
    TextView title, body, receivedDate, userUrl;
    FloatingActionButton fabBtn, isChkBtn;
    Dialog dialog;
    Context context = this;
    private int count = 0;

    @SuppressLint({"RestrictedApi", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_storage);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        mRecyclerView = findViewById(R.id.listView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        isChkBtn = (FloatingActionButton) findViewById(R.id.isChkBtn);

        fabBtn.setOnClickListener(new fabClickListener());
        isChkBtn.setOnClickListener(new fabClickListener());

        new getPushStorage().execute();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final RecyclerItem items = mList.get(position);


                SharedPreferences chkList = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = chkList.edit();
                //사용자가 누른 항목중에서 처음 읽게되는 항목에 대해서만 sharedpreference 적용
                if(items.getChkread().equals("0")) {
                    editor.putString(String.valueOf(count++), items.getPushNum());
                    editor.commit();
                }
                title = (TextView) dialog.findViewById(R.id.title);
                body = (TextView) dialog.findViewById(R.id.body);
                dialogImg = (ImageView) dialog.findViewById(R.id.dialogimg);
                receivedDate = (TextView) dialog.findViewById(R.id.date);
                userUrl = (TextView)dialog.findViewById(R.id.userUrl);

                title.setText(items.getTitle());
                body.setText(items.getBody());
                receivedDate.setText(items.getDate());
                new getBitmapImage().execute(position);
                if(items.getUserUrl() != ""){
                    userUrl.setText(items.getUserUrl());
                }
                userUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent webIntent = new Intent(getApplicationContext(), MyWeb.class);
                        webIntent.putExtra("userUrl", items.getUserUrl());
                        startActivity(webIntent);
                    }
                });

                dialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

    }

    @Override
    public void onStop() {
        super.onStop();

        new setUpdateRead().execute();
        count = 0;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    class fabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.fabBtn) {
                anim();
            } else if (id == R.id.isChkBtn) {
                chkmsg();
            }
        }
        private void chkmsg() {
            if (isChecked) {
                isChkBtn.setImageResource(R.drawable.ic_check);
                try {
                    mList.clear();
                    mList.addAll(copyList);
                    mAdapter = new RecyclerAdapter(mList);
                    mRecyclerView.setAdapter(mAdapter);

                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }

            }
            else {
                isChkBtn.setImageResource(R.drawable.ic_refresh);
                copyList.clear();
                copyList.addAll(mList);
                for (int i = mAdapter.getItemCount() - 1; i >= 0; i--) {
                    RecyclerItem items = mList.get(i);
                    if (items.getChkread().equals("1")) {
                        try {
                            mList.remove(i);
                            mAdapter = new RecyclerAdapter(mList);
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                    }
                }
            }
            isChecked = !isChecked;
        }

        private void anim() {
            View screen_lock = findViewById(R.id.background_dimmer);

            if (isFabOpen) {
                screen_lock.setVisibility(View.GONE);
                mRecyclerView.setLayoutFrozen(false);
                fabBtn.setImageResource(R.drawable.ic_add_circle);
                isChkBtn.startAnimation(fab_close);
                isChkBtn.setClickable(false);
            }
            else {
                screen_lock.setVisibility(View.VISIBLE);
                mRecyclerView.setLayoutFrozen(true);
                fabBtn.setImageResource(R.drawable.ic_cancel);
                isChkBtn.startAnimation(fab_open);
                isChkBtn.setClickable(true);
            }
            isFabOpen = !isFabOpen;
        }
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PushStorage.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PushStorage.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private class getPushStorage extends AsyncTask<String, Void, RecyclerItem[]> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected RecyclerItem[] doInBackground(String... URL) {

            String url = "http://nobles1030.cafe24.com/RequestPushStorage.php";
            RequestBody body = new FormBody.Builder()
                    .add("userID", "Hiro")
                    .build();

            Request request = new Request.Builder().url(url).post(body).build();

            try {
                Response response = client.newCall(request).execute();
                //Json data를 Gson형식으로 파싱해 리스트 생성
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();

                JsonElement rootObject = parser.parse(response.body().charStream());

                RecyclerItem[] posts = gson.fromJson(rootObject, RecyclerItem[].class);

                return posts;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(RecyclerItem[] result) {
            if(result.length > 0){
                for (RecyclerItem post: result){
                    mList.add(post);
                }
            }
            //Adapter setting
            mAdapter = new RecyclerAdapter(mList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class getBitmapImage extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... POS) {

            //OkHttpClient client = new OkHttpClient();

            RecyclerItem items = mList.get(POS[0]);
            String imageURL = items.getImgUrl();
            /*
            //읽은 아이템에 대해 읽었음을 업데이트
            RequestBody body = new FormBody.Builder()
                    .add("pushNum", items.getPushNum())
                    .build();

            Request request = new Request.Builder()
                    .url("http://nobles1030.cafe24.com/UpdateRead.php")   //DB에 토큰값을 올려준다.
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


             */
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            dialogImg.setImageBitmap(result);
        }
    }


    private class setUpdateRead  extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

            String chkList = new String();

            //읽은 아이템에 대해 읽었음을 업데이트

            Log.d("CAU count is ", String.valueOf(count));

            for(int i = 0; i < count; i++){
                Log.d("cau is ",pref.getString(String.valueOf(i),""));
                chkList += (pref.getString(String.valueOf(i), "") + " ");
            }

            Log.d("Cau chkList is ", chkList);
            RequestBody body = new FormBody.Builder()
                .add("pushNumList", chkList)
                .build();

            Request request = new Request.Builder()
                    .url("https://nobles1030.cafe24.com/UpdateRead.php")
                    .post(body)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
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