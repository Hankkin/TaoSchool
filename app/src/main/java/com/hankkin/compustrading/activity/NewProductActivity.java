package com.hankkin.compustrading.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hankkin.compustrading.FileUploadListener;
import com.hankkin.compustrading.R;
import com.hankkin.compustrading.Utils.BitmapUtils;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.model.Person;
import com.hankkin.compustrading.model.Product;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;
import me.drakeet.materialdialog.MaterialDialog;

public class NewProductActivity extends BaseActivity {

    @Bind(R.id.et_tel)
    EditText etTel;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.spinner_school)
    BetterSpinner spinnerSchool;
    @Bind(R.id.tv_cate)
    TextView tvCate;
    @Bind(R.id.btn_fabu)
    Button btnFabu;
    @Bind(R.id.iv_add_pro)
    ImageView ivAddPro;
    @Bind(R.id.tv_back)
    TextView tvBack;

    private String[] schools;
    private String filePath = "";
    private int cid=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        ButterKnife.bind(this);

        init();

    }

    private void init(){
        schools = getResources().getStringArray(R.array.school);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, schools);
        spinnerSchool.setAdapter(adapter);
        String tel = HankkinUtils.getPhoneNumber(NewProductActivity.this);
        if (!TextUtils.isEmpty(tel)){
            etTel.setText(tel);
        }

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 选择分类对话框
     * by Hankkin at:2015-12-24 17:39:17
     */
    @OnClick(R.id.tv_cate)
    void showCate(){
        final ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        final String[] cates = getResources().getStringArray(R.array.cate);
        for (int j = 0; j < cates.length; j++) {
            arrayAdapter.add(cates[j]);
        }

        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = getResources().getDisplayMetrics().density;
        listView.setDividerHeight(1);
        listView.setAdapter(arrayAdapter);
        final MaterialDialog alert = new MaterialDialog(this).setTitle(
                "请选择分类").setContentView(listView);

        alert.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cate = cates[position];
                tvCate.setText(cate);
                cid = position;
                alert.dismiss();
            }
        });
    }


    /**
     * 选择图片对话框
     * by Hankkin at:2015-12-20 23:25:37
     */
    @OnClick(R.id.iv_add_pro)
    void showMDdialog() {
        View view = LayoutInflater.from(NewProductActivity.this).inflate(R.layout.view_select_img, null, false);
        final MaterialDialog dialog = new MaterialDialog(this).setView(view);
        dialog.show();
        TextView tvGallery = (TextView) view.findViewById(R.id.tv_gallery);
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getImageFromGallery(NewProductActivity.this);
            }
        });
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_camera);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                filePath = iniFilePath(NewProductActivity.this);
                goCamera(NewProductActivity.this, filePath);
            }
        });
    }

    /**
     * 接受选择照片的结果显示
     * by Hankkin at:2015-12-24 17:41:49
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY) {
            filePath = getPath(NewProductActivity.this, data.getData());
            if (!TextUtils.isEmpty(filePath)) {
                ivAddPro.setImageBitmap(BitmapUtils.getCompressedBitmap(NewProductActivity.this, filePath));
            }
        } else if (requestCode == REQUST_CODE_CAMERA) {
            if (!TextUtils.isEmpty(filePath)) {
                ivAddPro.setImageBitmap(BitmapUtils.getCompressedBitmap(NewProductActivity.this, filePath));

            }
        }
    }

    /**
     * 发布按钮点击事件
     * 上传图片， 创建新商品
     * by Hankkin at:2015-12-24 18:33:15
     */
    @OnClick(R.id.btn_fabu)
    void addNewPro(){
        if (!HankkinUtils.isMobileNO(etTel.getText().toString().trim())){
            HankkinUtils.showToast(NewProductActivity.this,"请输入正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(etPrice.getText().toString().trim())||
                TextUtils.isEmpty(etTel.getText().toString().trim())||
                TextUtils.isEmpty(etName.getText().toString().trim())){
            HankkinUtils.showToast(NewProductActivity.this,"请完善信息");
            return;
        }
        if (TextUtils.isEmpty(tvCate.getText())||TextUtils.isEmpty(spinnerSchool.getText().toString())){
            HankkinUtils.showToast(NewProductActivity.this,"请完善信息");
            return;
        }
        if (!TextUtils.isEmpty(filePath)){
            showLoadingDialog();
            Bitmap tempBitmap = BitmapUtils.getCompressedBitmap(NewProductActivity.this, filePath);
            if (BitmapUtils.readPictureDegree(filePath) == 90) {
                tempBitmap = BitmapUtils.toturn(tempBitmap);
            }
            filePath = BitmapUtils.saveBitmap(tempBitmap,new Date().getTime() + "");

            uploadImg(filePath, NewProductActivity.this, new FileUploadListener() {
                @Override
                public void success(String url) {
                    Product product = new Product();
                    product.setName(etName.getText().toString());
                    product.setCid(cid);
                    product.setUsername(getCurrentPerson(NewProductActivity.this).getNickname());
                    product.setPrice(etPrice.getText().toString());
                    product.setSchool(spinnerSchool.getText().toString());
                    product.setProduct_url(url);
                    product.setUser_tel(etTel.getText().toString());
                    product.setUser_icon_url(getCurrentPerson(NewProductActivity.this).getUser_icon());
                    addProHttp(product);
                }

                @Override
                public void fail() {
                    dimissDialog();
                    HankkinUtils.showToast(NewProductActivity.this, "发布失败");
                }
            });
        }
    }

    /**
     * 保存新商品
     * by Hankkin at:2015-12-24 18:32:38
     * @param product
     */
    private void addProHttp(final Product product){
        product.save(NewProductActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                dimissDialog();
                HankkinUtils.showToast(NewProductActivity.this, "发布成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                dimissDialog();
                HankkinUtils.showToast(NewProductActivity.this,"发布失败");
            }
        });
    }
}
