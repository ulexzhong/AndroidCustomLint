package com.example.ulexzhong.androidtest;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ulexzhong on 2018/3/15.
 */
public class TestFragment extends Fragment {

    @SuppressLint("ActivityFragmentLayoutNameUse")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_main,container,false);
    }
}
