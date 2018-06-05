package com.zqtech.dataapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zqtech.dataapp.common.Elevator;
import com.zqtech.dataapp.common.Maintenancer;
import com.zqtech.dataapp.common.ResponseMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ElevatorActivity extends AppCompatActivity {
    public static final int SUCCESS_TOKEN = 0;
    private static final String TAG = "ElevatorActivity";
    public static final String REQUEST_PATH = "";
    private Spinner spinner_elevator;
    private Button btn_elevator;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elevator);
        spinner_elevator = (Spinner)findViewById(R.id.elevator_name);
        btn_elevator = (Button)findViewById(R.id.elevator_btn);
        btn_elevator.setOnClickListener(new View.OnClickListener() {
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
                String name = spinner_elevator.getSelectedItem().toString();
                Elevator elevator = null;
                List<Maintenancer> list = new ArrayList<>();
                String maintenanceCompany = "昆山菱宏电梯销售服务有限公司";
                list.add(new Maintenancer("唐学州", "18921508218"));
                list.add(new Maintenancer("何少华", "18014007144"));
                // longitude, latitude, name, maintenanceCompany, maintenancerList
                if("鸿泰苑三区1单元".equals(name)) {
                    elevator = new Elevator("120.498144", "31.528947", name, maintenanceCompany, list);
                }else if("鸿泰苑三区21单元".equals(name)) {
                    elevator = new Elevator("120.498482", "31.529334", name, maintenanceCompany, list);
                }else{
                    elevator = new Elevator("120.498924", "31.529399", name, maintenanceCompany, list);
                }

                // 构建FormBody，传入要提交的参数
                FormBody formBody = new FormBody.Builder().add("lng", elevator.getLongitude())
                        .add("lat", elevator.getLatitude())
                        .add("location", elevator.getName())
                        .add("happenTime", System.currentTimeMillis() + "")
                        .add("type", "0")
                        .add("maintenance", elevator.getMaintenanceCompany())
                        .add("guard", elevator.getMaintenancers())
                        .build();

                final Request request = new Request.Builder()
                        .url(REQUEST_PATH).post(formBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "电梯系统报警POST请求失败");
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
