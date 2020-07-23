package com.example.clock1;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class AlarmView extends LinearLayout {
    private Button btnAddAlarm;
    private ListView lvAlarm;
    private ArrayAdapter<AlarmData> adapter;
    private static final String KEY_ALARM_LIST = "al";
    private AlarmManager alarmManager;


    public AlarmView(Context context) {
        super(context);
        init();
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化闹钟管理器
    private void init() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

    }

    //闹钟数据
    private static class AlarmData {
        private long time = 0;
        private String timeLabel = "";
        private Calendar date;

        AlarmData(long time) {
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            timeLabel = String.format("%d月%d日 %d:%d", date.get(Calendar.MONTH)+1,
                    date.get(Calendar.DAY_OF_MONTH) ,
                    date.get(Calendar.HOUR_OF_DAY),
                    date.get(Calendar.MINUTE));
        }

        long getTime() {
            return time;
        }

        String getTimeLabel() {
            return timeLabel;
        }

        public String toString() {
            return getTimeLabel();
        }

        public int getId() {
            return (int) (getTime() / 1000 / 60);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnAddAlarm = (Button) findViewById(R.id.btnAddAlarm);
        lvAlarm = (ListView) findViewById(R.id.lvalarm);
        adapter = new ArrayAdapter<AlarmView.AlarmData>(getContext(), android.R.layout.simple_list_item_1);
        lvAlarm.setAdapter(adapter);
        readSaveAlarmList();
        btnAddAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });
        lvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setTitle("操作选项")
                        .setItems(new CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        deleteAlarm(position);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                //通知系统成功执行
                return true;
            }
        });
    }

    private void addAlarm() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.MILLISECOND, 0);
                Calendar currentTime = Calendar.getInstance();
                if (calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                }
                AlarmData ad=new AlarmData(calendar.getTimeInMillis());
                adapter.add(ad);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        ad.getTime(), 5 * 60,//间隔5分钟
                        PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
                saveAlarmList();
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    //存储闹钟数据
    private void saveAlarmList() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE).edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < adapter.getCount(); i++) {
            sb.append(adapter.getItem(i).getTime()).append("，");
        }
        if (sb.length() > 0) {
            String alarm = sb.toString().substring(0, sb.length() - 1);
            editor.putString(KEY_ALARM_LIST, alarm);
        } else {
            editor.putString(KEY_ALARM_LIST, null);
        }

        editor.commit();
    }

    //读取存储的闹钟数据
    private void readSaveAlarmList() {
        SharedPreferences sp = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ALARM_LIST, null);
        if (content != null) {
            String[] timeStrings = content.split("，");
            for (String s : timeStrings) {
                adapter.add(new AlarmData(Long.parseLong(s)));
            }
        }
    }

    //删除闹钟数据
    private void deleteAlarm(int position) {
        AlarmData ad=adapter.getItem(position);
        adapter.remove(ad);
        saveAlarmList();
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(),
                ad.getId(),new Intent(getContext(),AlarmReceiver.class),0));
    }


}
