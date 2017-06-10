package com.hnust.liveapp.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hnust.liveapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonglong on 2017/3/30.
 */

public class SignCardAdapter extends RecyclerView.Adapter<SignCardAdapter.ViewHolder> {

    private Context mContext;

    private AnimatorSet mRightOutSet; // 右出动画
    private AnimatorSet mLeftInSet; // 左入动画

    public static MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        public void onItemClick(View view, int postion);
    }

    private boolean mIsShowBack;
    int[] nums;

    public SignCardAdapter(Context mContext, int[] nums) {
        this.mContext = mContext;
        this.nums = nums;
    }

    public void setmIsShowBack(boolean mIsShowBack) {
        this.mIsShowBack = mIsShowBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.tv_num.setText(nums[position] + "");

        setAnimators(holder); // 设置动画
        setCameraDistance(holder); // 设置镜头距离

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(view, position);
                if (!mIsShowBack)
                    flipCard(holder);
            }
        });
    }


    // 翻转卡片
    public void flipCard(ViewHolder holder) {
        // 正面朝上
        if (!mIsShowBack) {
            mRightOutSet.setTarget(holder.mFlCardFront);
            mLeftInSet.setTarget(holder.mFlCardBack);
            mRightOutSet.start();
            mLeftInSet.start();
            mIsShowBack = true;
        }
//        else { // 背面朝上
//            mRightOutSet.setTarget(holder.mFlCardBack);
//            mLeftInSet.setTarget(holder.mFlCardFront);
//            mRightOutSet.start();
//            mLeftInSet.start();
//            mIsShowBack = false;
//        }
    }

    /**
     * 设置Item点击监听
     *
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    // 改变视角距离, 贴近屏幕
    private void setCameraDistance(ViewHolder holder) {
        int distance = 1600;
        float scale = mContext.getResources().getDisplayMetrics().density * distance;
        holder.mFlCardFront.setCameraDistance(scale);
        holder.mFlCardBack.setCameraDistance(scale);
    }

    // 设置动画
    private void setAnimators(final ViewHolder holder) {
        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.rightout);
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.leftin);

        // 设置点击事件
        mRightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                holder.mFlContainer.setClickable(true);
            }
        });
        mLeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                holder.mFlContainer.setClickable(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private FrameLayout mFlContainer;
        private FrameLayout mFlCardBack;
        private FrameLayout mFlCardFront;
        private TextView tv_num;

        public ViewHolder(View itemView) {
            super(itemView);
            mFlContainer = (FrameLayout) itemView.findViewById(R.id.main_fl_container);
            mFlCardBack = (FrameLayout) itemView.findViewById(R.id.main_fl_card_back);
            mFlCardFront = (FrameLayout) itemView.findViewById(R.id.main_fl_card_front);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);

        }
    }

}