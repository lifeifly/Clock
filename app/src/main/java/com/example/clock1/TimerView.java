package com.example.clock1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class TimerView extends LinearLayout {
    private static final int MESSAGE_TICK = 0X222;
    private Button btnStart, btnResum, btnPause, btnReset;
    private EditText hour, minute, second;


    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnStart = (Button) findViewById(R.id.btnStart);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnResum = (Button) findViewById(R.id.btnResume);
        btnReset = (Button) findViewById(R.id.btnReset);
        hour = (EditText) findViewById(R.id.hour);
        minute = (EditText) findViewById(R.id.minute);
        second = (EditText) findViewById(R.id.second);
        hour.setText("00");
        minute.setText("00");
        second.setText("00");
        btnStart.setVisibility(View.VISIBLE);
        btnStart.setEnabled(false);
        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTiemr();
                btnStart.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        btnPause.setVisibility(View.GONE);
        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                btnPause.setVisibility(View.GONE);
                btnResum.setVisibility(View.VISIBLE);
            }
        });
        btnResum.setVisibility(View.GONE);
        btnResum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startTiemr();
                btnResum.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });
        btnReset.setVisibility(View.GONE);
        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                hour.setText("0");
                minute.setText("0");
                second.setText("0");
                btnReset.setVisibility(View.GONE);
                btnResum.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });
        hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int inputHour = Integer.parseInt(s.toString());
                    if (inputHour > 59) {
                        hour.setText("59");
                    } else if (inputHour < 0) {
                        hour.setText("0");
                    }
                }
                checkToEnableStartBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        minute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int inputMinute = Integer.parseInt(s.toString());
                    if (inputMinute > 59) {
                        minute.setText("59");
                    } else if (inputMinute < 0) {
                        minute.setText("0");
                    }
                }
                checkToEnableStartBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        second.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int inputSecond = Integer.parseInt(s.toString());
                    if (inputSecond > 59) {
                        second.setText("59");
                    } else if (inputSecond < 0) {
                        second.setText("0");
                    }
                }
                checkToEnableStartBtn();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    //启用start按钮
    private void checkToEnableStartBtn() {
        btnStart.setEnabled(!TextUtils.isEmpty(hour.getText())
                || !TextUtils.isEmpty(minute.getText())
                || !TextUtils.isEmpty(hour.getText()));
    }

    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private int allTimerCount = 0;//计时器秒数
    private static final int MESSAGE_WHAT = 0X111;

    //开始计时
    private void startTiemr() {
        allTimerCount = Integer.parseInt(hour.getText().toString()) * 60 * 60 +
                Integer.parseInt(minute.getText().toString()) * 60 +
                Integer.parseInt(second.getText().toString());
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    allTimerCount--;
                    //每个一秒刷新一次编辑框
                    handler.sendEmptyMessage(MESSAGE_TICK);
                    if (allTimerCount ==0) {
                        handler.sendEmptyMessage(MESSAGE_WHAT);
                        stopTimer();
                    }
                }
            };
            timer.schedule(timerTask, 1000, 1000);//延迟一秒执行run方法，以后也是延迟一秒

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x111:
                    new AlertDialog.Builder(getContext())
                            .setTitle("Time is up")
                            .setMessage("Time is up")
                            .setNegativeButton("Cancel", null)
                            .show();
                    btnReset.setVisibility(View.GONE);
                    btnResum.setVisibility(View.GONE);
                    btnPause.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);
                    break;
                case 0x222:
                    String rHour = String.valueOf(allTimerCount / 60 / 60);
                    String rMin = String.valueOf((allTimerCount / 60) % 60);
                    String rSec = String.valueOf(allTimerCount % 60);
                    hour.setText(rHour);
                    minute.setText(rMin);
                    second.setText(rSec);
                    break;
                default:
                    break;

            }

        }
    };


    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
