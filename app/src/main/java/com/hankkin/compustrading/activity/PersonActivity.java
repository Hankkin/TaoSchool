package com.hankkin.compustrading.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hankkin.compustrading.FileUploadListener;
import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.BitmapUtils;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.model.Person;
import com.hankkin.compustrading.model.PersonShow;
import com.hankkin.compustrading.view.PullToZoomScrollViewEx;
import com.hankkin.compustrading.view.RippleView;
import com.hankkin.compustrading.view.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.listener.UpdateListener;
import me.drakeet.materialdialog.MaterialDialog;

public class PersonActivity extends BaseActivity {
    @Bind(R.id.tv_back)
    TextView tvBack;
    private PullToZoomScrollViewEx scrollView;
    private List<PersonShow> data = new ArrayList<>();

    private TextView tvLogin, tvRegister;
    private RoundedImageView ivUserIcon;

    View headView;
    View zoomView;
    View contentView;

    public static PersonActivity instance;
    private String filePath = "";
    private RippleView rvLogout;
    private RippleView rvBuy;
    private RippleView rvSale;
    private TextView tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_person);
        initViews();
        initData();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.my_pull_scoll);
        loadViewForCode();

        scrollView.getPullRootView().findViewById(R.id.tv_test1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {

        Person person = Person.getCurrentUser(this, Person.class);
        if (person != null) {
            ImageLoader.getInstance().displayImage(person.getUser_icon(), ivUserIcon);
            tvLogin.setVisibility(View.GONE);
            tvRegister.setVisibility(View.GONE);
            tvLogout.setText("退出账号");
        } else {
            tvLogin.setVisibility(View.VISIBLE);
            tvRegister.setVisibility(View.VISIBLE);
            ivUserIcon.setImageDrawable(getResources().getDrawable(R.drawable.defaut));
            tvLogout.setText("登录");
        }

    }

    private void loadViewForCode() {
        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) findViewById(R.id.my_pull_scoll);
        headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        contentView = LayoutInflater.from(this).inflate(R.layout.profile_contect_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        tvLogin = (TextView) headView.findViewById(R.id.tv_login);
        tvRegister = (TextView) headView.findViewById(R.id.tv_register);
        ivUserIcon = (RoundedImageView) headView.findViewById(R.id.iv_user_head);
        rvLogout = (RippleView) contentView.findViewById(R.id.rv_logout);
        tvLogout = (TextView) contentView.findViewById(R.id.tv_logout);
        rvLogout.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Person.logOut(PersonActivity.this);
                HankkinUtils.showToast(PersonActivity.this, "已注销");
                initData();
                finish();
            }
        });

        rvBuy = (RippleView) contentView.findViewById(R.id.rv_buy);
        rvBuy.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        rvSale = (RippleView) contentView.findViewById(R.id.rv_sale);
        rvSale.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(PersonActivity.this,NewProductActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentPerson(PersonActivity.this)!=null){
                    showMDdialog();
                }
            }
        });
    }

    /**
     * 选择图片对话框
     * by Hankkin at:2015-12-20 23:25:37
     */
    private void showMDdialog() {
        View view = LayoutInflater.from(PersonActivity.this).inflate(R.layout.view_select_img, null, false);
        final MaterialDialog dialog = new MaterialDialog(this).setView(view);
        dialog.show();
        TextView tvGallery = (TextView) view.findViewById(R.id.tv_gallery);
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getImageFromGallery(PersonActivity.this);
            }
        });
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_camera);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                filePath = iniFilePath(PersonActivity.this);
                goCamera(PersonActivity.this, filePath);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY) {
            showLoadingDialog();
            filePath = getPath(PersonActivity.this, data.getData());
            if (!TextUtils.isEmpty(filePath)) {
                uploadImg(filePath, PersonActivity.this, new FileUploadListener() {

                    @Override
                    public void success(final String url) {
                        Person person = Person.getCurrentUser(PersonActivity.this, Person.class);
                        person.setUser_icon(url);
                        ImageLoader.getInstance().displayImage(url, ivUserIcon);
                        updateUser(person);
                    }

                    @Override
                    public void fail() {
                        dimissDialog();
                        HankkinUtils.showToast(PersonActivity.this, "上传失败");
                    }
                });
            }
        } else if (requestCode == REQUST_CODE_CAMERA) {
            showLoadingDialog();
            if (!TextUtils.isEmpty(filePath)) {
                Bitmap tempBitmap = BitmapUtils.getCompressedBitmap(PersonActivity.this, filePath);
                if (BitmapUtils.readPictureDegree(filePath) == 90) {
                    tempBitmap = BitmapUtils.toturn(tempBitmap);
                }
                filePath = BitmapUtils.saveBitmap(tempBitmap,new Date().getTime() + "");
                uploadImg(filePath, PersonActivity.this, new FileUploadListener() {

                    @Override
                    public void success(String url) {
                        Person person = Person.getCurrentUser(PersonActivity.this, Person.class);
                        person.setUser_icon(url);
                        ImageLoader.getInstance().displayImage(url, ivUserIcon);
                        updateUser(person);
                    }

                    @Override
                    public void fail() {
                        dimissDialog();
                        HankkinUtils.showToast(PersonActivity.this, "上传失败");
                    }
                });
            }
        }
    }

    /**
     * 更新个人用户信息
     * by Hankkin at:2015-12-23 19:34:17
     *
     * @param person
     */
    private void updateUser(Person person) {
        person.update(this, person.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                dimissDialog();
                HankkinUtils.showToast(PersonActivity.this, "上传成功");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

}
