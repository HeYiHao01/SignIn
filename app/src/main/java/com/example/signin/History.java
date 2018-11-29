package com.example.signin;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.util.List;

public class History extends ListActivity {
    private ListView historyList;
    private List<String> historyData = new ArrayList<String>();
    private ArrayAdapter<String> historyAdapter;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.no_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList(){
        historyData.clear();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            Connection conn = DBUtil.getCon();
            ResultSet rs = StudentDAO.query_date(conn);
            while (rs.next()){
                historyData.add(rs.getString("Date"));
            }
            setLayout();
            if (rs != null){
                rs.close();
                DBUtil.close(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLayout(){
        if (!historyData.isEmpty()){
            setContentView(R.layout.history_list);
            historyList = (ListView)findViewById(android.R.id.list);
            historyAdapter = new ArrayAdapter<String>(this,R.layout.history_list_item,historyData);
            setListAdapter(historyAdapter);
            historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog = Loading.createLoadingDialog(History.this,"查询中。。。");
                    Intent displayHistory = new Intent(History.this,HistoryInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position",position);
                    displayHistory.putExtras(bundle);
                    startActivity(displayHistory);
                    Loading.closeDialog(dialog);
                    //Toast.makeText(History.this,String.valueOf(position)+"Clicked",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            setContentView(R.layout.no_history);
        }
    }
}
