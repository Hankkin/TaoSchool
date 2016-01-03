package com.hankkin.compustrading.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.model.Person;
import com.hankkin.compustrading.sharepreference.MySP;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.et_login_name)
    EditText etName;
    @Bind(R.id.et_login_pwd)
    EditText etPwd;
    @Bind(R.id.tv_back)
    TextView tvBack;
    @Bind(R.id.pw_loading)
    ProgressWheel wheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews(){
        wheel.stopSpinning();
    }

    /**
     * 注册用户
     * by Hankkin at:2015-12-20 21:13:02
     */
    @OnClick(R.id.btn_register)
    public void register(View view){
        wheel.spin();
        final String name = etName.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            HankkinUtils.showToast(RegisterActivity.this,"用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)){
            HankkinUtils.showToast(RegisterActivity.this,"密码不能为空");
            return;
        }
        if (!HankkinUtils.isMobileNO(name)){
            HankkinUtils.showToast(RegisterActivity.this,"请输入正确的手机号");
            return;
        }
        Person person = new Person();
        person.setUsername(name);
        person.setPassword(pwd);
        person.signUp(RegisterActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                MySP.setPASSWoRD(RegisterActivity.this,pwd);
                MySP.setUSERNAME(RegisterActivity.this, name);
                wheel.stopSpinning();
                HankkinUtils.showToast(RegisterActivity.this, "注册成功");
                BmobUser.loginByAccount(RegisterActivity.this, "username", "用户密码", new LogInListener<Person>() {

                    @Override
                    public void done(Person user, BmobException e) {
                        // TODO Auto-generated method stub
                        if (user != null) {
                            Log.i("smile", "用户登陆成功");
                        }
                    }
                });
                Intent intent = new Intent(RegisterActivity.this,MainShowActivity.class);
                startActivity(intent);
                finish();
                if (LoginActivity.instance!=null){
                    LoginActivity.instance.finish();
                }
                if (PersonActivity.instance!=null){
                    PersonActivity.instance.finish();
                }
                if (MainShowActivity.instance!=null){
                    MainShowActivity.instance.finish();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                wheel.stopSpinning();
                HankkinUtils.showToast(RegisterActivity.this, "注册失败");
            }
        });
    }

    @OnClick(R.id.tv_back)
    void back(){
        finish();
    }


}
