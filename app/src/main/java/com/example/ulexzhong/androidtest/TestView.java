package com.example.ulexzhong.androidtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ulexzhong on 2018/3/15.
 */

public class TestView extends android.support.v7.widget.AppCompatTextView {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void test(){
        for(int i=0;i<5;i++){
            View view=new View(getContext());
        }
    }
}
