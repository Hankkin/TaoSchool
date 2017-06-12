package com.hankkin.compustrading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hankkin.compustrading.activity.MainShowActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_login)
    Button btnLogin;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
//        Product product = new Product(0,"九成新Iphone6",4000,"")
    }

    @OnClick(R.id.btn_login)
    public void btnLoginClick() {
        Intent intent = new Intent(MainActivity.this, MainShowActivity.class);
        startActivity(intent);
        finish();
    }

}
