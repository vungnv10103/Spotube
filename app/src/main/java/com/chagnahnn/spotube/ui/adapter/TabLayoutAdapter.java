package com.chagnahnn.spotube.ui.adapter;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabLayoutAdapter extends FragmentStateAdapter {
    private final SparseArray<Fragment> mFragmentList;

    public TabLayoutAdapter(@NonNull FragmentActivity fragmentActivity, SparseArray<Fragment> fragmentList) {
        super(fragmentActivity);
        this.mFragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }
    

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}