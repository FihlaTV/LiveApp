package com.hnust.liveapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.RoomInfo;
import com.hnust.liveapp.db.DBService;
import com.hnust.liveapp.ui.activitys.live.IjkPlayerActivity;
import com.hnust.liveapp.util.GlobalConfig;

import java.util.List;

/**
 * Created by yonglong on 2017/3/30.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<RoomInfo> roomInfos;
    private int flag;
    private int type = 0;
    DBService dbService;


    public RecyclerViewAdapter(Context mContext, List<RoomInfo> roomInfos, int flag) {
        this.mContext = mContext;
        this.flag = flag;
        this.roomInfos = roomInfos;
        dbService = new DBService(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.e("RecyclerViewAdapter", roomInfos.get(position).getJturl());
        Glide.with(mContext)
                .load(roomInfos.get(position).getJturl())
                .fitCenter()
                .error(R.mipmap.icon)
                .into(holder.iv_bg); // Show the thumb before play
        holder.host_title.setText(roomInfos.get(position).getFj_name());
        holder.host_user.setText(roomInfos.get(position).getName());
        holder.host_number.setText(roomInfos.get(position).getNumber() + "");

        if (flag == GlobalConfig.ROOMS_MAIN) {
            holder.host_category.setVisibility(View.VISIBLE);
            holder.host_category.setText(roomInfos.get(position).getType());
        }
        if (flag == GlobalConfig.ROOMS_FOCUS || flag == GlobalConfig.ROOMS_HISTORY) {
            holder.host_category.setVisibility(View.VISIBLE);
            String[] status = new String[]{"休息", "直播中", "禁播中"};
            holder.host_category.setText(status[roomInfos.get(position).getNow()]);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, IjkPlayerActivity.class);
                intent.putExtra("room_id", roomInfos.get(position).getRoom_id());
                intent.putExtra("jt_url", roomInfos.get(position).getJturl());
                intent.putExtra("play_url", roomInfos.get(position).getUrl());
                intent.putExtra("zhubo_name", roomInfos.get(position).getName());
                intent.putExtra("fj_name", roomInfos.get(position).getFj_name());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView host_title, host_user, host_status, host_category, host_number;
        public ImageView iv_bg;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_bg = (ImageView) itemView.findViewById(R.id.iv_bg);
            host_status = (TextView) itemView.findViewById(R.id.host_status);
            host_category = (TextView) itemView.findViewById(R.id.host_category);
            host_title = (TextView) itemView.findViewById(R.id.host_title);
            host_user = (TextView) itemView.findViewById(R.id.host_user);
            host_number = (TextView) itemView.findViewById(R.id.host_number);

        }
    }

    public void setLiveInfos(List<RoomInfo> roomInfos) {
        this.roomInfos = roomInfos;
    }
}