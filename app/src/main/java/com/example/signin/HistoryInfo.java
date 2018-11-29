package com.example.signin;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.signin.Util.DBUtil;
import com.example.signin.Util.StudentDAO;

import java.sql.Connection;
import java.sql.ResultSet;

public class HistoryInfo extends AppCompatActivity {
    private TextView absent;
    private TextView no_absent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_info);

        absent = (TextView)findViewById(R.id.absent);
        no_absent = (TextView)findViewById(R.id.no_absent);

        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        int position = mBundle.getInt("position");

        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Connection con = null;
            con = DBUtil.getCon();
            ResultSet rs = StudentDAO.query_date(con);
            while (position-- > -1){
                rs.next();
            }
            String table_name = rs.getString("Signtable");
            Log.d("table_name", table_name);
            ResultSet count = StudentDAO.query_count(con,table_name);
            absent.setText("出席人数："+String.valueOf(count.getInt(1)));
            no_absent.setText("缺席人数："+String.valueOf(82 - count.getInt(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
