package com.example.signin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupAdapter extends BaseAdapter {
        private Context context;//上下文
        private ArrayList<String> list;
        //控制CheckBox选中情况
        private static HashMap<Integer,Boolean> isSelected;
        private LayoutInflater inflater=null;//导入布局


        public GroupAdapter(Context context, ArrayList<String> list) {
            this.context = context;
            this.list = list;
            inflater=LayoutInflater.from(context);
            isSelected=new HashMap<Integer, Boolean>();
            initData();
        }
        private void initData(){//初始化isSelected的数据
            for(int i=0;i<list.size();i++){
                getIsSelected().put(i,false);

            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        //listview每显示一行数据,该函数就执行一次
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if (convertView==null) {//当第一次加载ListView控件时  convertView为空
                convertView=inflater.inflate(R.layout.name_item, null);//所以当ListView控件没有滑动时都会执行这条语句
                holder=new ViewHolder();
                holder.tv=(TextView)convertView.findViewById(R.id.name);
                holder.cb=(CheckBox)convertView.findViewById(R.id.is_signed);
                convertView.setTag(holder);//为view设置标签

            }
            else{//取出holder
                holder=(ViewHolder) convertView.getTag();//the Object stored in this view as a tag
            }
            //设置list的textview显示
            holder.tv.setTextColor(Color.BLACK);
            holder.tv.setText(list.get(position));
            // 根据isSelected来设置checkbox的选中状况
            holder.cb.setChecked(getIsSelected().get(position));
            return convertView;
        }
        static class ViewHolder {
            TextView tv;
            CheckBox cb;
        }
        public static HashMap<Integer, Boolean>getIsSelected(){
            return isSelected;
        }
        public static void setIsSelected(HashMap<Integer, Boolean> isSelected){
            GroupAdapter.isSelected=isSelected;
        }
}
