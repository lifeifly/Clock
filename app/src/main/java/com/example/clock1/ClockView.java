package com.example.clock1;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class ClockView extends LinearLayout {
    private TextView tv_clock;
    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //初始化完成后执行
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_clock = (TextView) findViewById(R.id.tv_clock);
        handler.sendEmptyMessage(0);
    }

    //可见性改变时执行
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility==View.VISIBLE){
            handler.sendEmptyMessage(0);
        }else {
            handler.removeMessages(0);
        }
    }

    //实现刷新时间
    private void refresh() {
        Calendar c=Calendar.getInstance();//获取日期实例
        tv_clock.setText(c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (getVisibility() == View.VISIBLE) {
                refresh();
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
}
