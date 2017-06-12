package com.hankkin.compustrading.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hankkin.compustrading.R;
import com.hankkin.compustrading.ScrollDirectionListener;
import com.hankkin.compustrading.Utils.HankkinUtils;
import com.hankkin.compustrading.activity.ProdectDetailActivity;
import com.hankkin.compustrading.adapter.ProductAdapter;
import com.hankkin.compustrading.model.Product;
import com.hankkin.compustrading.view.RefreshLayout;
import com.hankkin.compustrading.view.floatbutton.FloatingActionsMenu;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Hankkin on 15/11/29.
 */
public class CateDetailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener{
    private RefreshLayout swipeRefreshLayout;
    private ListView lvProduct;
    private List<Product> productList = new ArrayList<>();

    private int cid = 0;
    private ProductAdapter adapter;
    private FloatingActionsMenu fab;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cid = getArguments().getInt("cid");
            List<Product> data = (List<Product>) getArguments().getSerializable("products");
            productList.clear();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getCid() == cid) {
                    productList.add(data.get(i));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        initViews(view);

        return view;
    }


    private void initViews(View view) {
        lvProduct = (ListView) view.findViewById(R.id.lv_product);
        swipeRefreshLayout = (RefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setChildView(lvProduct);
        //设置卷内的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnLoadListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);



        adapter = new ProductAdapter(productList, getActivity());
        lvProduct.setAdapter(adapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProdectDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", (Serializable) adapter.getItem(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (fab!=null){
            fab.attachToListView(lvProduct, new ScrollDirectionListener() {
                @Override
                public void onScrollDown() {
                    Log.d("ListViewFragment", "onScrollDown()");
                }

                @Override
                public void onScrollUp() {
                    Log.d("ListViewFragment", "onScrollUp()");
                }
            }, new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Log.d("ListViewFragment", "onScrollStateChanged()");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    Log.d("ListViewFragment", "onScroll()");
                }
            });
        }
    }

    /**
     * 根据商品Id查询商品
     * by Hankkin at:2015-12-19 23:19:41
     * @param cid
     */
    private void queryProductsByIdHttp(int cid) {
        BmobQuery<Product> productBmobQuery = new BmobQuery<>();
        productBmobQuery.order("-createdAt");
        productBmobQuery.setLimit(10);
        productBmobQuery.addWhereEqualTo("cid", cid);
        productBmobQuery.findObjects(new FindListener<Product>() {
            @Override
            public void done(List<Product> list, BmobException e) {
                if (e == null){
                    if (list != null && list.size() > 0) {
                        productList.clear();
                        productList.addAll(list);
                        adapter = new ProductAdapter(productList, getActivity());
                        lvProduct.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                else {
                    HankkinUtils.showLToast(getActivity(),e.getMessage());
                }
            }


        });
    }

    private void queryProductsByIdHttp1(int cid) {
        BmobQuery<Product> productBmobQuery = new BmobQuery<>();
        Product product = (Product) adapter.getItem(adapter.getCount() - 1);
        String time = product.getCreatedAt();
        SimpleDateFormat sdf = new SimpleDateFormat(time);
        try {
            Date date = sdf.parse(time);
            productBmobQuery.addWhereLessThan("createdAt",new BmobDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        productBmobQuery.order("-createdAt");
        productBmobQuery.setLimit(10);
        productBmobQuery.addWhereEqualTo("cid", cid);
        productBmobQuery.findObjects(new FindListener<Product>() {
            @Override
            public void done(List<Product> list, BmobException e) {
                if (e == null){
                    if (list != null && list.size() > 0) {
                        productList.addAll(list);
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setLoading(false);
                    } else {
                        HankkinUtils.showToast(getActivity().getApplicationContext(), "暂无新数据");
                        swipeRefreshLayout.setLoading(false);
                    }
                }else {
                    HankkinUtils.showLToast(getActivity(),e.getMessage());
                }
            }
        });
    }

    public void updatePeo() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                queryProductsByIdHttp(cid);
            }
        }, 2000);
    }

    @Override
    public void onLoad() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryProductsByIdHttp1(cid);
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                queryProductsByIdHttp(cid);
            }
        }, 2000);
    }

    public ListView getLvProduct(){
        return lvProduct;
    }

    public void  setFab(FloatingActionsMenu fab){
        this.fab = fab;
    }

    public void hideFab(){
        fab.toggle();
    }

}


