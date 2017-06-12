package com.hankkin.compustrading.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.adapter.CategoryFragmentAdapter;
import com.hankkin.compustrading.fragment.CateDetailFragment;
import com.hankkin.compustrading.model.Category;
import com.hankkin.compustrading.model.Person;
import com.hankkin.compustrading.model.Product;
import com.hankkin.compustrading.view.PagerSlidingTabStrip;
import com.hankkin.compustrading.view.RippleView;
import com.hankkin.compustrading.view.RoundedImageView;
import com.hankkin.compustrading.view.floatbutton.FloatingActionButton;
import com.hankkin.compustrading.view.floatbutton.FloatingActionsMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.update.BmobUpdateAgent;


public class MainShowActivity extends BaseActivity {


    /**
     * 分类滑动选项卡
     */
    private PagerSlidingTabStrip pagerTab;
    /**
     * 滑动组件
     */
    private ViewPager pager;
    /**
     * 选项fragment界面
     */
    private ArrayList<CateDetailFragment> fragments;

    /**
     * 分类数组
     */
    private ArrayList<Category> categories = new ArrayList<>();
    /**
     * 分类界面适配器
     */
    private CategoryFragmentAdapter adapter;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    @Bind(R.id.rv_usericon)
    RoundedImageView rvUser;
    @Bind(R.id.tv_person)
    TextView tvPerson;
    @Bind(R.id.rv_logreg)
    RippleView rvLogReg;
    @Bind(R.id.tv_username)
    TextView tvName;
    @Bind(R.id.rv_buy)
    RippleView rvBuy;
    @Bind(R.id.rv_sale)
    RippleView rvSale;
    @Bind(R.id.rv_sina)
    RippleView rvSina;
    @Bind(R.id.rv_qq)
    RippleView rvQQ;
    @Bind(R.id.tv_qq)
    TextView tvQQ;
    @Bind(R.id.tv_sina)
    TextView tvSina;
    @Bind(R.id.tv_buy)
    TextView tvBuy;
    @Bind(R.id.tv_sale)
    TextView tvSale;
    @Bind(R.id.tv_show)
    TextView tvShow;

    private Handler handler;
    private Person person;

    @Bind(R.id.multiple_actions)
    FloatingActionsMenu floatingActionsMenu;
    public static MainShowActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main_show);
        BmobUpdateAgent.update(this);
        ButterKnife.bind(this);
        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    for (int i = 0; i < categories.size(); i++) {
                        CateDetailFragment fragment = new CateDetailFragment();
                        fragment.setFab(floatingActionsMenu);
                        Bundle bundle = new Bundle();
                        bundle.putInt("cid", categories.get(i).getId());
                        bundle.putSerializable("products", (Serializable) msg.obj);
                        fragment.setArguments(bundle);
                        fragments.add(fragment);
                    }

                    adapter = new CategoryFragmentAdapter(getSupportFragmentManager(), fragments, categories);
                    pager.setAdapter(adapter);
                    pagerTab.setViewPager(pager);
                    dimissDialog();
                }
            }
        };

    }

    /**
     * 初始化数据
     * by Hankkin at:2015-11-29 19:29:52
     */
    private void init() {


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(MainShowActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        //设定左上角突变可点击
        getSupportActionBar().setHomeButtonEnabled(true);
        // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置标题
        getSupportActionBar().setTitle(getResources().getString(R.string.action_title));

        pager = (ViewPager) findViewById(R.id.pager);
        pagerTab = (PagerSlidingTabStrip) findViewById(R.id.tab);
        pager.setOffscreenPageLimit(4);


        fragments = new ArrayList<>();


        /**
         * 更新按钮点击事件
         * by Hankkin at:2015-12-23 17:29:52
         */
        FloatingActionButton fbUpdate = (FloatingActionButton) findViewById(R.id.fb_update);
        fbUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragments.get(pager.getCurrentItem()).hideFab();
                fragments.get(pager.getCurrentItem()).updatePeo();

            }
        });
        /**
         * 新建按钮点击事件
         * by Hankkin at:2015-12-23 17:30:17
         */
        FloatingActionButton fbWrite = (FloatingActionButton) findViewById(R.id.fb_new);
        fbWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = BmobUser.getCurrentUser(Person.class);
                if (person != null) {
                    fragments.get(pager.getCurrentItem()).hideFab();
                    Intent intent = new Intent(MainShowActivity.this, NewProductActivity.class);
                    startActivity(intent);
                } else {
                    fragments.get(pager.getCurrentItem()).hideFab();
                    Intent intent = new Intent(MainShowActivity.this, LoginActivity.class);
                    startActivity(intent);
                    HankkinUtils.showToast(MainShowActivity.this, "请先登录");
                }
            }
        });

        FloatingActionButton fbMy = (FloatingActionButton) findViewById(R.id.fb_person);
        fbMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragments.get(pager.getCurrentItem()).hideFab();
                Intent intent = new Intent(MainShowActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        person = getCurrentPerson(MainShowActivity.this);
        if (person != null) {
            rvBuy.setVisibility(View.VISIBLE);
            rvSale.setVisibility(View.VISIBLE);
            rvQQ.setVisibility(View.GONE);
            rvSina.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(person.getUser_icon())) {
                ImageLoader.getInstance().displayImage(person.getUser_icon(), rvUser);
            }
            tvPerson.setText("个人中心");
            if (!TextUtils.isEmpty(person.getNickname())) {
                tvName.setText(person.getNickname());
            } else {
                tvName.setText("用户" + person.getUsername().substring(0, 3));
            }
            tvShow.setText("我的");
        } else {
            rvUser.setBackground(getResources().getDrawable(R.drawable.defaut));
            tvPerson.setText("登录或注册");
            tvName.setText("");
            rvBuy.setVisibility(View.GONE);
            rvSale.setVisibility(View.GONE);
            rvQQ.setVisibility(View.VISIBLE);
            rvSina.setVisibility(View.VISIBLE);
            tvShow.setText("其他登录方式");
        }


        rvLogReg.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (person != null) {
                    Intent intent = new Intent(MainShowActivity.this, PersonActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainShowActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        rvBuy.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                drawerLayout.closeDrawers();
            }
        });
        rvSale.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(MainShowActivity.this, NewProductActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });


        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList = (ArrayList<Category>) getIntent().getSerializableExtra("categories");
        if (categoryList!=null){
            if (categoryList.size()>0){
                categories.addAll(categoryList);
                queryProductsHttp();
            }
            else {
                queryCategory();
            }
        }
        else {
            queryCategory();
        }
    }




    private void queryProductsHttp() {
        BmobQuery<Product> productBmobQuery = new BmobQuery<>();
        productBmobQuery.order("-createdAt");
        productBmobQuery.findObjects(new FindListener<Product>() {
            @Override
            public void done(List<Product> list, BmobException e) {
                if (e == null){
                    if (list != null && list.size() > 0) {
                        List<Product> data = new ArrayList<Product>();
                        for (Product p : list) {
                            data.add(p);
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    }
                }
            }
        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标
        drawerToggle.syncState();
    }

    /** 设备配置改变时 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_person) {
            Intent intent = new Intent(MainShowActivity.this, PersonActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (floatingActionsMenu.isExpanded()) {
                floatingActionsMenu.collapse();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Bmob查询分类数据显示选项卡
     * by Hankkin at:2015-11-29 19:39:45
     */
    private void queryCategory() {
        showLoadingDialog();
        BmobQuery<Category> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.order("createdAt");// 按照时间降序
        categoryBmobQuery.findObjects(new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null){
                    if (list != null && list.size() > 0) {
                        categories = new ArrayList<Category>();
                        categories.addAll(list);
                        queryProductsHttp();
                    }
                }
                else {
                    HankkinUtils.showToast(MainShowActivity.this, e.getMessage());
                }
            }
        });
    }

}
