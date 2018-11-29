package com.example.signin;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.signin.Util.HttpUtil;
import com.example.signin.Util.Loading;
import com.example.signin.Util.UpdatePicService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button sign;
    private Button history;
    private Button finish;

    private ImageView bingPicImg;

    BroadcastReceiver broadcastReceiver;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        sign = (Button)findViewById(R.id.sign);
        history = (Button)findViewById(R.id.history);
        finish = (Button)findViewById(R.id.finish);
        sign.setOnClickListener(this);
        history.setOnClickListener(this);
        finish.setOnClickListener(this);

        Intent intent = new Intent(this, UpdatePicService.class);
        startService(intent);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign:
                dialog = Loading.createLoadingDialog(MainActivity.this,"查询中。。。");
                new Thread(runnable).start();
                break;
            case R.id.history:
                Intent historyIntent = new Intent(MainActivity.this,History.class);
                startActivity(historyIntent);
                break;
            case R.id.finish:
                finish();
                break;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkConnectivity();
            Intent signIntent = new Intent(MainActivity.this,SignList.class);
            startActivity(signIntent);
            Loading.closeDialog(dialog);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    public void checkConnectivity() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()){
                    //Toast.makeText(LoginActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "没有网络", Toast.LENGTH_SHORT).show();
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver,intentFilter);
    }
}
