package com.hankkin.compustrading.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hankkin.compustrading.fragment.CateDetailFragment;
import com.hankkin.compustrading.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hankkin on 15/11/29.
 */
public class CategoryFragmentAdapter extends FragmentPagerAdapter {

    private List<CateDetailFragment> fragments;
    private ArrayList<Category> categories;

    public CategoryFragmentAdapter(FragmentManager fm, List<CateDetailFragment> fragments,ArrayList<Category> categories) {
        super(fm);
        this.fragments = fragments;
        this.categories = categories;
    }

    public CategoryFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    public String getPageTitle(int i){
        return categories.get(i).getName();
    }
}
