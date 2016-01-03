package com.hankkin.compustrading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hankkin.compustrading.R;
import com.hankkin.compustrading.model.PersonShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hankkin on 15/12/6.
 */
public class PersonInfoAdapter extends BaseAdapter {
    private List<PersonShow> data = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public PersonInfoAdapter(List<PersonShow> data, Context context) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_personinfo,null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(data.get(position).getTitle());
        holder.tvContent.setText(data.get(position).getContent());
        return convertView;
    }

    class ViewHolder{
        TextView tvTitle,tvContent;
    }
}
