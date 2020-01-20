package com.example.listviewtest;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false, isChecked = false;
    RecyclerView mRecyclerView;
    RecyclerImageTextAdapter mAdapter;
    ArrayList<RecyclerItem> mList = new ArrayList<RecyclerItem>();
    ArrayList<RecyclerItem> copyList = new ArrayList<RecyclerItem>();
    ImageView dialogImg;
    TextView title, body, receivedDate, userUrl;
    FloatingActionButton fabBtn, isChkBtn;
    Dialog dialog;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                // TODO : process the click event for action_search item.
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class fabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.fabBtn:
                    anim();
                    break;
                case R.id.isChkBtn:
                    chkmsg();
                    break;
            }
        }

        private void chkmsg() {
            if (isChecked) {
                isChkBtn.setImageResource(R.drawable.ic_check_black_24dp);
                try {
                    mList.clear();
                    mList.addAll(copyList);
                    mAdapter = new RecyclerImageTextAdapter(mList);
                    mRecyclerView.setAdapter(mAdapter);

                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }

            } else {
                isChkBtn.setImageResource(R.drawable.ic_refresh);
                copyList.clear();
                copyList.addAll(mList);
                for (int i = mAdapter.getItemCount() - 1; i >= 0; i--) {
                    RecyclerItem items = mList.get(i);
                    if (items.getChkread().equals("1")) {
                        try {
                            mList.remove(i);
                            mAdapter = new RecyclerImageTextAdapter(mList);
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
            } else {
                screen_lock.setVisibility(View.VISIBLE);
                mRecyclerView.setLayoutFrozen(true);
                fabBtn.setImageResource(R.drawable.ic_cancel);
                isChkBtn.startAnimation(fab_open);
                isChkBtn.setClickable(true);
            }
            isFabOpen = !isFabOpen;
        }
    }

    @SuppressLint({"RestrictedApi", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        mRecyclerView = findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog = new Dialog(this);
        dialog.setContentView(R.xml.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        isChkBtn = (FloatingActionButton) findViewById(R.id.isChkBtn);

        fabBtn.setOnClickListener(new fabClickListener());
        isChkBtn.setOnClickListener(new fabClickListener());

        new getPushStorage().execute();

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final RecyclerItem items = mList.get(position);

                title = (TextView) dialog.findViewById(R.id.title);
                body = (TextView) dialog.findViewById(R.id.body);
                dialogImg = (ImageView) dialog.findViewById(R.id.dialogimg);
                receivedDate = (TextView) dialog.findViewById(R.id.date);
                userUrl = (TextView) dialog.findViewById(R.id.userUrl);

                title.setText(items.getTitle());
                body.setText(items.getBody());
                receivedDate.setText(items.getDate());
                new getBitmapImage().execute(position);

                if (items.getUserUrl() != "") {
                    userUrl.setText(items.getUserUrl());
                }
                userUrl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent webIntent = new Intent(getApplicationContext(), MyWebView.class);
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
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
            if (result.length > 0) {
                for (RecyclerItem post : result) {
                    mList.add(post);
                }
            }
            //Adapter setting
            mAdapter = new RecyclerImageTextAdapter(mList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class getBitmapImage extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... POS) {
            OkHttpClient client = new OkHttpClient();

            RecyclerItem items = mList.get(POS[0]);
            String imageURL = items.getImgUrl();

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
}