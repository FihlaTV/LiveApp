package com.hnust.liveapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hnust.liveapp.R;
import com.hnust.liveapp.bean.Message;

import java.util.List;

/**
 * 聊天列表的数据适配器
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private int mUsernameColor;

    public MessageAdapter(Context context, List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Message.TYPE_MESSAGE:
                layout = R.layout.item_message;
                break;
            case Message.TYPE_ZHIBO:
                layout = R.layout.item_message;
                break;
            case Message.TYPE_LOG:
                layout = R.layout.item_log;
                break;
            case Message.TYPE_ACTION:
                layout = R.layout.item_action;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.setMessage(message.getMessage(),message.getType());
        viewHolder.setUsername(message.getUsername()+":",message.getType());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mMessageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
        }

        public void setUsername(String username, int type) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            if (type == 100) {
                mUsernameView.setTextColor(Color.WHITE);
            }
        }

        public void setMessage(String message, int type) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
            if (type == 100) {
                mMessageView.setTextColor(Color.WHITE);
            }
        }

//        private int getUsernameColor(String username) {
//            int hash = 7;
//            for (int i = 0, len = username.length(); i < len; i++) {
//                hash = username.codePointAt(i) + (hash << 5) - hash;
//            }
//            int index = Math.abs(hash % mUsernameColors.length);
//            return mUsernameColors[index];
//        }
    }
}