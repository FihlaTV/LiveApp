package com.hnust.liveapp.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.CateInfo;

import java.util.List;

/**
 * Created by yonglong on 2017/5/22.
 */

public class CatetoryAdapter extends BaseAdapter implements SpinnerAdapter {
    private Context context;
    private List<CateInfo> list;

    public CatetoryAdapter(Context context, List<CateInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CateInfo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, null);
        TextView tvgetView = (TextView) convertView.findViewById(android.R.id.text1);
        tvgetView.setGravity(Gravity.CENTER);
        tvgetView.setText(getItem(position).getType());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner, null);
        TextView tvdropdowview = (TextView) convertView.findViewById(R.id.text);
        tvdropdowview.setText(getItem(position).getType());
        return convertView;
    }
}
