package com.example.ulexzhong.androidtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private static Activity context = null;

    private int mNum = 0;
    private long mLong = 0;
    private char mChar = '\u0000';
    private float mFloat = 0.0f;
    private double mDouble = 0.0d;
    private boolean mBoolean = false;
    private short mShort = 0;
    private byte mByte = 0;
    private String mStr = null;

    private static final String EXTRA_KEY = "";
    private static final String sss = "dddd";
    private String n;


    HashMap<Integer, Integer> nn;
    private static String zhong = "ssss";
    private Test test = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom);

        System.out.println("llalalall");
        Log.i("ulex", "ddddd");


        Message msg = new Message();
        //        msg.arg1=1;
        //        new Handler().sendMessage(msg);
        //        Toast.makeText(this,"sss",111);
        Intent intent = new Intent();
        intent.putExtra("sss", "ddd");

        TestEnum testEnum = TestEnum.TWO;

        int n = new Integer("11");
        m();
        Test test = new Test();
        "sss".equals(test.m);
        test.m.equals("ss");

        test.map = new HashMap<>();

        nn = new HashMap<>();

        TestEntity entity = new TestEntity();
        int s = entity.num;
        Toast toast = Toast.makeText(this, "ssss", Toast.LENGTH_LONG);
        toast.show();

        testHasp(test.map);
        HashMap<String, Integer> mm = new HashMap<>();
        System.out.println("" + mNum);

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        new MyThread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    private class MyThread extends Thread {
        MyThread(Runnable runnable) {
            super(runnable);
        }
    }

    //Hash
    private void testHasp(Map<Integer, Integer> map) {
        mNum = 3;
        //        map=new HashMap<>();
        map.put(1, 1);
    }

    private void testStream() {
        int m=0;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("test.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //            try {
            //                if (inputStream != null) {
            //                    inputStream.close();
            //                }
            //            } catch (Exception e) {
            //                e.printStackTrace();
            //            }
        }
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Test {
        private static Activity mContext;
        Map<Integer, Integer> map=null;
        public static final int TIME = 1;
        int num=0;
        String m=null;
    }

    private void m() {
        n = "333";
    }
}
