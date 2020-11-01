package com.shuai.musicplayer2.control;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.shuai.musicplayer2.R;


public class Login extends Activity {

    private EditText user,psw;
    private Button login;
    private CheckBox cb;
    private SharedPreferences preferences;
    private Button show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitView();
        preferences = this.getPreferences(MODE_PRIVATE);
        if(preferences.getBoolean("is",false)){
            user.setText(preferences.getString("user",""));
            psw.setText(preferences.getString("psw",""));
            cb.setChecked(true);
        }
    }

    private void InitView(){
        user = findViewById(R.id.user);
        psw = findViewById(R.id.psw);
        login= findViewById(R.id.login);
        show = findViewById(R.id.show);
        cb = findViewById(R.id.cb);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(JudegUser()){
                    saveInfo();
                    Intent intent = new Intent(Login.this, Main.class);
                    //intent.putExtra("name","");
                    startActivity(intent);
                }else {
                    Toast.makeText(Login.this, "账号和密码不匹配", Toast.LENGTH_SHORT).show();
                }
            }
        });
        show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    psw.setSelection(psw.getText().length());
                }else {
                    psw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    psw.setSelection(psw.getText().length());
                }
                return false;
            }
        });
    }

    private  boolean JudegUser(){
        return true;
    }


    public void goRegister(View v){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
    public void goRetrieve(View v){
        Intent intent = new Intent(Login.this, Retrieve.class);
        startActivity(intent);
    }
    private void saveInfo(){
        SharedPreferences.Editor editor = preferences.edit();
        if(cb.isChecked()){
            editor.putBoolean("is",true);
            editor.putString("user",user.getText().toString());
            editor.putString("psw",psw.getText().toString());
        }else {
            editor.clear();
        }
        editor.commit();
    }

}