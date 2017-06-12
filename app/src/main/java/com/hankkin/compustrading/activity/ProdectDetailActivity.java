package com.hankkin.compustrading.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.model.Product;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProdectDetailActivity extends AppCompatActivity {

    private Product product;
    private TextView tvDesc,tvProName,tvTime,tvSchool,tvPrice;
    private ImageView ivPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prodect_detail);

        product = (Product) getIntent().getSerializableExtra("product");

        tvDesc = (TextView) findViewById(R.id.tv_pro_desc);
        tvProName = (TextView) findViewById(R.id.tv_pro_name);
        ivPro = (ImageView) findViewById(R.id.iv_product);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvSchool = (TextView) findViewById(R.id.tv_school);
        tvPrice = (TextView) findViewById(R.id.tv_price);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.menu_prodect_detail);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        HankkinUtils.showToast(ProdectDetailActivity.this, "分享");
                        break;
                    case R.id.action_settings:
                        HankkinUtils.showToast(ProdectDetailActivity.this, "举报");
                        break;
                }
                return false;
            }
        });

        CollapsingToolbarLayout collapsingAvatarToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingAvatarToolbar.setBackgroundColor(getResources().getColor(R.color.theme_color));
        collapsingAvatarToolbar.setExpandedTitleGravity(Gravity.CENTER_VERTICAL);
        collapsingAvatarToolbar.setExpandedTitleColor(getResources().getColor(R.color.theme_color));
        TextView tv = (TextView) findViewById(R.id.username);
        tv.setText(product.getUsername());


        tvProName.setText(product.getName());
        tvDesc.setText(product.getDesc());
        ImageLoader.getInstance().displayImage(product.getProduct_url(),ivPro);
        tvPrice.setText("￥" + product.getPrice());
        tvSchool.setText(product.getSchool());
        tvTime.setText(product.getCreatedAt());

    }

}
