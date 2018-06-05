package com.zqtech.dataapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zqtech.dataapp.common.Device;
import com.zqtech.dataapp.common.Person;
import com.zqtech.dataapp.common.ResponseMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int SUCCESS_TOKEN = 0;
    public static final String REQUEST_PATH = "http://wyq9995.asuscomm.com:10000/zqdj/faceIdentify.json";
    private static final String TAG = "FaceActivity";
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
    private Spinner spinner_event;
    private Spinner spinner_person;
    private Spinner spinner_device;
    private Button btn_from_album;
    private EditText et_face_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        list = new ArrayList<>();
        list.add("白名单人员");
        list.add("黑名单人员");
        list.add("其他(非白名单非黑名单人员)");

        spinner_device = (Spinner) findViewById(R.id.face_device_code);
        spinner_event = (Spinner) findViewById(R.id.face_event);
        spinner_person = (Spinner) findViewById(R.id.face_person_id);
        btn_from_album = (Button) findViewById(R.id.face_from_album);
        et_face_url = (EditText) findViewById(R.id.face_file_url);

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_event.setAdapter(adapter);
        /*spinner_event.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // String event = adapter.getItem(i);
                // Toast.makeText(VehicleActivity.this, "点击了" + event, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        btn_from_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(FaceActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FaceActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "拒绝申请", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if(resultCode == RESULT_OK) {
                    if(Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        et_face_url.setText(imagePath);
//            displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
//        displayImage(imagePath);
        et_face_url.setText(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /*private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.face_btn:
                submitRequest();
                break;
        }
    }

    private void submitRequest() {
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String personId = (String) spinner_person.getSelectedItem();
                Person person = null;
                if ("320283199001011512".equals(personId)) {
                    person = new Person("张三", personId, "鸿运苑三区1栋102室");
                } else if ("320283199001031513".equals(personId)) {
                    person = new Person("李四", personId, "鸿运苑三区21栋103室");
                } else if ("320283199004241524".equals(personId)) {
                    person = new Person("张五", personId, "鸿运苑三区22栋104室");
                }
                String deviceCode = (String) spinner_device.getSelectedItem();
                Device device = null;
                //  longitude, latitude, location, deviceCode
                if ("F1810001".equals(deviceCode)) {
                    device = new Device("120.495805", "31.529276", "南门", deviceCode);
                } else {
                    device = new Device("120.498735", "31.529156", "东门", deviceCode);
                }
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                builder.addFormDataPart("lng", device.getLongitude())
                        .addFormDataPart("lat", device.getLatitude())
                        .addFormDataPart("location", device.getLocation())
                        .addFormDataPart("happen_time", System.currentTimeMillis() + "")
                        .addFormDataPart("name", person.getName())
                        .addFormDataPart("identityId", person.getPersonId())
                        .addFormDataPart("familyAddress", person.getHomeAddress())
                        .addFormDataPart("devCode", device.getDeviceCode())
                        .addFormDataPart("type", spinner_device.getSelectedItemPosition() + "");
                File file = new File(et_face_url.getText().toString());
                String fileName = file.getName();
                builder.addPart(Headers.of("Content-Disposition", "form-data;name=\"file\";filename=\"" + fileName + "\""), RequestBody.create(MediaType.parse("image/jpg"),file));

                final Request request = new Request.Builder()
                        .url(REQUEST_PATH).post(builder.build()).build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "人脸识别报警POST请求失败");
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
