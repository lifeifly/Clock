package com.example.clock1;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class StopWatch extends LinearLayout {
    private static final int MESSAGE_WHAT=1;
    private TextView tvHour, tvMinute, tvSecond, tvMill;
    private Button btnStart, btnResume, btnReset, btnPause, btnLop;
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private int tenCount=0;
    private TimerTask timerTask;
    private Timer timer=new Timer();
    private TimerTask showTimeTash;
    public StopWatch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvHour = (TextView) findViewById(R.id.timeHour);
        tvMinute = (TextView) findViewById(R.id.timeMinute);
        tvSecond = (TextView) findViewById(R.id.timeSecond);
        tvMill = (TextView) findViewById(R.id.timeMillSec);
        tvHour.setText("0");
        tvMinute.setText("0");
        tvSecond.setText("0");
        tvMill.setText("0");

        btnStart = (Button) findViewById(R.id.timeStart);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnLop.setVisibility(View.VISIBLE);
            }
        });
        btnPause = (Button) findViewById(R.id.timePause);
        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnLop.setVisibility(View.GONE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        btnResume = (Button) findViewById(R.id.timeResume);
        btnReset = (Button) findViewById(R.id.timeReset);
        btnLop = (Button) findViewById(R.id.timeLop);

        btnLop.setVisibility(View.GONE);
        btnLop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.insert(String.format("%d:%d:%d.%d",
                        tenCount/100/60/60,tenCount/100/60%60,tenCount/100%60,tenCount%100),0);
            }
        });
        btnResume.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                tenCount=0;
                adapter.clear();
                btnLop.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });
        btnResume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
                btnResume.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.GONE);
                btnLop.setVisibility(View.VISIBLE);
            }
        });
        lv = (ListView) findViewById(R.id.lvWatchTime);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        showTimeTash=new TimerTask() {
            @Override
            public void run() {
            handler.sendEmptyMessage(MESSAGE_WHAT);
            }
        };
        timer.schedule(showTimeTash,200,200);
    }
    //开始计时
    private void startTimer(){
        if (timerTask==null){

            timerTask=new TimerTask() {
                @Override
                public void run() {
                    tenCount++;
                }
            };
            timer.schedule(timerTask,10,10);
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //呈现时间
                case MESSAGE_WHAT:
                    tvHour.setText(String.valueOf(tenCount*10/1000/60/60));
                    tvMinute.setText(String.valueOf(tenCount*10/1000/60%60));
                    tvSecond.setText(String.valueOf(tenCount*10/1000%60));
                    tvMill.setText(String.valueOf(tenCount%100));
                    break;
            }
        }
    };
    public void onDestroy(){
        timer.cancel();
    }
    private void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask=null;
        }
    }
}
