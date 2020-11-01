package com.shuai.musicplayer2.control;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shuai.musicplayer2.R;

public class Register extends AppCompatActivity {

    private TextView user,psw;
    private CheckBox cb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
