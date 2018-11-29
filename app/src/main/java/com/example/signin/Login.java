package com.example.signin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.signin.Util.FingerPrintUtils;

public class Login extends AppCompatActivity {
    private FingerprintManagerCompat mFingerManger;
    private KeyguardManager mKeyManger;
    private Button login;

    private int mCount=5;
    private FingerPrintUtils mFingerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFingerManger=FingerprintManagerCompat.from(this);
        mKeyManger= (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    //判断权限
                    if (judgePermission()){
                        mFingerUtils=new FingerPrintUtils(Login.this);
                        mFingerUtils.setFingerPrintListener(new FingerCallBack());
                    }
                }else{
                    Toast.makeText(Login.this,"设备不支持",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //判断权限
    public boolean judgePermission(){
        //权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请开启指纹识别权限", Toast.LENGTH_LONG).show();
            //可以进行动态申请
            return false;
        }
        //硬件是否支持指纹识别
        if (!mFingerManger.isHardwareDetected()){
            Toast.makeText(this, "您手机不支持指纹识别功能", Toast.LENGTH_LONG).show();
            return false;
        }
        //是否已经录入指纹
        if (!mFingerManger.hasEnrolledFingerprints()){
            Toast.makeText(this, "您还未录入指纹", Toast.LENGTH_LONG).show();
            return false;
        }
        //手机是否开启锁屏密码
        if (!mKeyManger.isKeyguardSecure()){
            Toast.makeText(this, "请开启开启锁屏密码，并录入指纹后再尝试", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private class FingerCallBack extends FingerprintManagerCompat.AuthenticationCallback{
        //多次识别失败,并且，不能短时间内调用指纹验证
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            if (mCount>1){
                mCount--;
            }else {
                Toast.makeText(Login.this,"尝试失败，1分钟后尝试",Toast.LENGTH_SHORT).show();
            }
            mHandler.sendMessageDelayed(new Message(), 1000 * 60);
        }

        //出错可恢复
        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
        }

        //识别成功
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            mFingerUtils.stopsFingerPrintListener();
            Toast.makeText(Login.this, "识别成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Login.this,MainActivity.class));
            finish();
        }

        //识别失败
        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            if (mCount>0){
                mCount--;
            }
            Toast.makeText(Login.this,"登陆失败，还可以尝试"+String.valueOf(mCount)+"次",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFingerUtils!=null){
                mFingerUtils.reSetFingerPrintListener(new FingerCallBack());
            }
            mCount=5;
        }
    };
}
