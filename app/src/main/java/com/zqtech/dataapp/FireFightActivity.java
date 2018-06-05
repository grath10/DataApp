package com.zqtech.dataapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zqtech.dataapp.common.FireControl;
import com.zqtech.dataapp.common.ResponseMessage;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FireFightActivity extends AppCompatActivity {
    private static final String TAG = "FireFightActivity";
    public static final int SUCCESS_TOKEN = 0;
    public static final String REQUEST_PATH = "http://wyq9995.asuscomm.com:10000/zqdj/vehicleIdentify.json";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_TOKEN:
                    Toast.makeText(getBaseContext(), "请求成功", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getBaseContext(), "请求出错", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private Spinner spinner_device;
    private Spinner spinner_trigger;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_fight);
        spinner_device = (Spinner)findViewById(R.id.fire_device);
        spinner_trigger = (Spinner)findViewById(R.id.fire_trigger);
        btn = (Button)findViewById(R.id.fire_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRequest();
            }
        });
    }

    private void submitRequest() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String name = spinner_device.getSelectedItem().toString();
                FireControl fireControl = null;
                if("鸿泰苑三区1单元地库".equals(name)) {
                    fireControl = new FireControl("120.498144", "31.528947", name);
                }else if("鸿泰苑三区21单元地库".equals(name)) {
                    fireControl = new FireControl("120.498482", "31.529334", name);
                }else {
                    fireControl = new FireControl("120.498924", "31.529399", name);
                }
                // 构建FormBody，传入要提交的参数
                FormBody formBody = new FormBody.Builder().add("lng", fireControl.getLongitude())
                        .add("lat", fireControl.getLatitude())
                        .add("location", fireControl.getLocation())
                        .add("happenTime", System.currentTimeMillis() + "")
                        .add("trigger", spinner_trigger.getSelectedItemPosition() + "")
                        .add("type", "0")
                        .build();

                final Request request = new Request.Builder()
                        .url(REQUEST_PATH).post(formBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "消防报警POST请求失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();
                        String responseData = response.body().string();
                        ResponseMessage responseMsg = gson.fromJson(responseData, ResponseMessage.class);
                        Message msg = Message.obtain();
                        msg.obj = responseMsg.getData();
                        msg.what = Integer.parseInt(responseMsg.getSuccess());
                        handler.sendMessage(msg);
                    }
                });
            }
        }.start();
    }

}
