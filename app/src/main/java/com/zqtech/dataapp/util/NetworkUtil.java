package com.zqtech.dataapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    public static boolean isNetConnected(Context context) {
        boolean isNetConnected;
        // 获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            isNetConnected = true;
        } else {
            Log.i(TAG, "没有可用网络");
            isNetConnected = false;
        }
        return isNetConnected;
    }
}
