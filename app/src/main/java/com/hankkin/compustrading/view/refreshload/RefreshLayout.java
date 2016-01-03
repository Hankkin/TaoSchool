package com.hankkin.compustrading.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hankkin.compustrading.view.refreshload.MetaballView;

public class RefreshLayout extends SwipeRefreshLayout {

    private final int mTouchSlop;
    private ListView mListView;
    private OnLoadListener mOnLoadListener;

    private float firstTouchY;
    private float lastTouchY;

    private boolean isLoading = false;

    private MetaballView footerView;
    private LinearLayout footerHolder;
    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTouchSlop = 200;
        footerView = new MetaballView(context,attrs);
//        footerView.setPaintMode(0);
        footerHolder = new LinearLayout(context);
        footerHolder.setGravity(Gravity.CENTER);
        footerHolder.addView(footerView);


        footerView.setVisibility(GONE);

    }

    //set the child view of RefreshLayout,ListView
    public void setChildView(ListView mListView) {
        this.mListView = mListView;

        mListView.addFooterView(footerHolder);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstTouchY = event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                lastTouchY = event.getRawY();
                if (canLoadMore()) {
                    loadData();
                }


//                Log.d("movvvvvvve", firstTouchY - lastTouchY + "");
//
//                Log.d("movvve",mTouchSlop +"");
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean canLoadMore() {
        return isBottom() && !isLoading && isPullingUp();
    }

    private boolean isBottom() {
        if (mListView.getCount() > 0) {
            if (mListView.getLastVisiblePosition() == mListView.getAdapter().getCount() - 1 &&
                    mListView.getChildAt(mListView.getChildCount() - 1).getBottom() <= mListView.getHeight()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPullingUp() {
        return (firstTouchY - lastTouchY) >= mTouchSlop;
    }

    private void loadData() {
        if (mOnLoadListener != null) {
            setLoading(true);
        }
    }

    public void setLoading(boolean loading) {
        if (mListView == null) return;
        isLoading = loading;
        if (loading) {
            if (isRefreshing()) {
                setRefreshing(false);
            }

            footerView.setVisibility(VISIBLE);

            mListView.setSelection(mListView.getAdapter().getCount() - 1);
            mOnLoadListener.onLoad();
        } else {
            footerView.setVisibility(GONE);
            firstTouchY = 0;
            lastTouchY = 0;
        }
    }



    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    public interface OnLoadListener {
        public void onLoad();
    }
}