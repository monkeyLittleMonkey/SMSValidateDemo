package com.hbh.cl.smsvalidatedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by hbh on 2017/2/7.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button validateNum_btn;
    private Button landing_btn;
    private EditText userName;
    private EditText validateNum;
    public EventHandler eh; //事件接收器
    private TimeCount mTimeCount;//计时器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initEvent();
        init();
    }

    private void initEvent(){
        userName = (EditText) findViewById(R.id.userName);
        validateNum = (EditText) findViewById(R.id.validateNum);
        validateNum_btn = (Button) findViewById(R.id.validateNum_btn);
        landing_btn = (Button) findViewById(R.id.landing_btn);
        validateNum_btn.setOnClickListener(this);
        landing_btn.setOnClickListener(this);
        mTimeCount = new TimeCount(60000, 1000);
    }

    /**
     * 初始化事件接收器
     */
    private void init(){
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码成功

                        startActivity(new Intent(LoginActivity.this, MainActivity.class)); //页面跳转

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){ //获取验证码成功

                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){ //返回支持发送验证码的国家列表

                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.validateNum_btn:
//                SMSSDK.getSupportedCountries();//获取短信目前支持的国家列表
                if(!userName.getText().toString().trim().equals("")){
                    if (checkTel(userName.getText().toString().trim())) {
                        SMSSDK.getVerificationCode("+86",userName.getText().toString());//获取验证码
                        mTimeCount.start();
                    }else{
                        Toast.makeText(LoginActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.landing_btn:
                if (!userName.getText().toString().trim().equals("")) {
                    if (checkTel(userName.getText().toString().trim())) {
                        if (!validateNum.getText().toString().trim().equals("")) {
                            SMSSDK.submitVerificationCode("+86",userName.getText().toString().trim(),validateNum.getText().toString().trim());//提交验证
                        }else{
                            Toast.makeText(LoginActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 正则匹配手机号码
     * @param tel
     * @return
     */
    public boolean checkTel(String tel){
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher matcher = p.matcher(tel);
        return matcher.matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            validateNum_btn.setClickable(false);
            validateNum_btn.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            validateNum_btn.setClickable(true);
            validateNum_btn.setText("获取验证码");
        }
    }

}
