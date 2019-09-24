package com.tct.musicplayer.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tct.musicplayer.R;


public class TimerDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView timerNo,timer1,timer2,timer3,timer4,timer5,timerCustomer;

    public TimerDialog(Context context) {
        super(context,R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);

        //setCanceledOnTouchOutside(true);//点击外部关闭
        timerNo = findViewById(R.id.timer_no);
        timer1 = findViewById(R.id.timer_1);
        timer2 = findViewById(R.id.timer_2);
        timer3 = findViewById(R.id.timer_3);
        timer4 = findViewById(R.id.timer_4);
        timer5 = findViewById(R.id.timer_5);
        timerCustomer = findViewById(R.id.timer_customer);

        timerNo.setOnClickListener(this);
        timer1.setOnClickListener(this);
        timer2.setOnClickListener(this);
        timer3.setOnClickListener(this);
        timer4.setOnClickListener(this);
        timer5.setOnClickListener(this);
        timerCustomer.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timer_no:
                Toast.makeText(context,"timer_no",Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timer_1:
                Toast.makeText(context,"timer_1",Toast.LENGTH_SHORT).show();
                break;
            case R.id.timer_2:
                Toast.makeText(context,"timer_2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.timer_3:
                Toast.makeText(context,"timer_3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.timer_4:
                Toast.makeText(context,"timer_4",Toast.LENGTH_SHORT).show();
                break;
            case R.id.timer_5:
                Toast.makeText(context,"timer_5",Toast.LENGTH_SHORT).show();
                break;
            case R.id.timer_customer:
                Toast.makeText(context,"timer_customer",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
