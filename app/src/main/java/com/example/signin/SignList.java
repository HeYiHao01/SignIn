package com.example.signin;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.signin.Util.DBUtil;
import com.example.signin.Util.Loading;
import com.example.signin.Util.StudentDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignList extends ListActivity {
    private ListView name_list;
    private ArrayList<String> name;

    private FloatingActionButton fab;

    private HashMap<Integer,Boolean> hmap = new HashMap<Integer, Boolean>();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_list);

        name = new ArrayList<>();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Connection con = DBUtil.getCon();
            ResultSet rs = StudentDAO.query_stu(con);
            while (rs.next()){
                name.add(rs.getString("Sname"));
            }
            if (rs != null){
                rs.close();
            }
            DBUtil.close(con);
        }catch (Exception e){
            e.printStackTrace();
        }
        name_list = (ListView)findViewById(android.R.id.list);
        GroupAdapter adapter = new GroupAdapter(this,name);
        name_list.setAdapter(adapter);
        name_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                GroupAdapter.ViewHolder viewHolder = (GroupAdapter.ViewHolder) view.getTag();
                viewHolder.cb.toggle();// 把CheckBox的选中状态改为当前状态的反,gridview确保是单一选中
                GroupAdapter.getIsSelected().put(position, viewHolder.cb.isChecked());//将CheckBox的选中状况记录下来
            }
        });

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = Loading.createLoadingDialog(SignList.this,"提交中。。。");
                hmap = GroupAdapter.getIsSelected();
                int i = 0;
                for (Boolean b:hmap.values()){
                    if (b){
                        i++;
                    }
                }
                Toast.makeText(SignList.this,"签到人数："+String.valueOf(i),Toast.LENGTH_SHORT).show();
                new Thread(runnable).start();
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                String table_name = format_tableName().toString();
                String date = initDateTime().toString();
                Log.d("table_name", table_name);
                Log.d("date", date);

                Connection conn = DBUtil.getCon();
                StudentDAO.create_sign(conn,table_name);
                StudentDAO.insert_sign(conn,table_name,name,hmap);
                StudentDAO.insert_date(conn,date,table_name);
                DBUtil.close(conn);

                Intent history = new Intent(SignList.this,History.class);
                startActivity(history);
                Loading.closeDialog(dialog);
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private StringBuffer format_tableName(){
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        sb.append("sign_").append(String.valueOf(calendar.get(Calendar.YEAR))).append("_")
                .append(String.valueOf(calendar.get(Calendar.MONTH)+1)).append("_")
                .append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).append("_")
                .append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))).append("_")
                .append(String.valueOf(calendar.get(Calendar.MINUTE)));
        return sb;
    }

    private StringBuffer initDateTime() {
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        sb.append(String.valueOf(calendar.get(Calendar.YEAR))).append(".")
                .append(String.valueOf(calendar.get(Calendar.MONTH)+1)).append(".")
                .append(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))).append(".")
                .append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))).append(":")
                .append(String.valueOf(calendar.get(Calendar.MINUTE)));
        return sb;
    }
}
