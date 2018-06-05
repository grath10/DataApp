package com.zqtech.dataapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zqtech.dataapp.common.Device;
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

public class VehicleActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int SUCCESS_TOKEN = 0;
    public static final String REQUEST_PATH = "http://wyq9995.asuscomm.com:10000/zqdj/vehicleIdentify.json";
    private static final String TAG = "VehicleActivity";
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
    private List<String> list;
    private ArrayAdapter<String> adapter;
    private Spinner spinner_device;
    private Spinner spinner_plate;
    private Spinner spinner_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        list = new ArrayList<>();
        list.add("白名单车辆");
        list.add("黑名单车辆");
        list.add("其他(非白名单非黑名单车辆)");

        spinner_event = (Spinner) findViewById(R.id.vehicle_event);
        spinner_device = (Spinner) findViewById(R.id.vehicle_device_code);
        spinner_plate = (Spinner) findViewById(R.id.vehicle_plate);

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_event.setAdapter(adapter);
        spinner_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // String event = adapter.getItem(i);
                // Toast.makeText(VehicleActivity.this, "点击了" + event, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vehicle_btn:
                submitRequest();
                break;
        }
    }

    private void submitRequest() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String deviceCode = spinner_device.getSelectedItem().toString();
                Device device = null;
                //  longitude, latitude, location, deviceCode
                if ("F1810001".equals(deviceCode)) {
                    device = new Device("120.495805", "31.529276", "南门", deviceCode);
                } else {
                    device = new Device("120.498735", "31.529156", "东门", deviceCode);
                }
                // 构建FormBody，传入要提交的参数
                FormBody formBody = new FormBody.Builder().add("lng", device.getLongitude())
                        .add("lat", device.getLatitude())
                        .add("location", device.getLocation())
                        .add("happenTime", System.currentTimeMillis() + "")
                        .add("licensePlateNumber", spinner_plate.getSelectedItem().toString())
                        .add("devCode", device.getDeviceCode())
                        .add("type", spinner_event.getSelectedItemPosition() + "")
                        .build();

                final Request request = new Request.Builder()
                        .url(REQUEST_PATH).post(formBody).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "车辆识别报警POST请求失败");
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
