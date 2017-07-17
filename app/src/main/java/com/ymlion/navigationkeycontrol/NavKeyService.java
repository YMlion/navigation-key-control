package com.ymlion.navigationkeycontrol;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;

/**
 * 拦截实体导航键，交换返回和最近任务
 * <p/>
 * Created by YMlion on 2017/7/13.
 */
public class NavKeyService extends AccessibilityService {

    private static final int WHAT_BACK = 1;
    private Handler mHandler;
    private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == WHAT_BACK && Build.VERSION.SDK_INT > 23) {
                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                }
                return false;
            }
        });
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d("NKS", "onKeyEvent: " + event.toString());
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && Build.VERSION.SDK_INT > 23) {
                    mHandler.sendEmptyMessageDelayed(WHAT_BACK, LONG_PRESS_TIMEOUT);
                }
                break;
            case KeyEvent.ACTION_UP:
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (Build.VERSION.SDK_INT < 24) {
                        performGlobalAction(GLOBAL_ACTION_RECENTS);
                    } else {
                        long time = event.getEventTime() - event.getDownTime();
                        if (time < LONG_PRESS_TIMEOUT) {
                            mHandler.removeMessages(WHAT_BACK);
                            performGlobalAction(GLOBAL_ACTION_RECENTS);
                        }
                    }
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_APP_SWITCH) {
                    performGlobalAction(GLOBAL_ACTION_BACK);
                    return true;
                }
                break;
        }

        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}