package com.android.AshenAndroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.AshenAndroid.server.impl.AshenHTTPServer;
import com.android.AshenAndroid.utils.AshenConst;
import com.android.AshenAndroid.utils.Init;

import java.io.File;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.android.AshenAndroid.utils.AshenConst.built_in_interface;
import static com.android.AshenAndroid.utils.AshenConst.fileSavePath;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private static int REQUEST_PERMISSION_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File dataFile = this.getExternalFilesDir("data");
        fileSavePath = dataFile.getPath();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        TextView ipStr = findViewById(R.id.ip_str);
        ipStr.setText("host:  " + getHostIP() + ":" + AshenConst.PORT_DEFAULT);

        ArrayAdapter<String> get_adapter = new ArrayAdapter<String>(
                MainActivity.this, android.R.layout.simple_list_item_1, built_in_interface);
        ListView get_listView = (ListView) findViewById(R.id.get_list);
        get_listView.setAdapter(get_adapter);

        final ListView get_list = findViewById(R.id.get_list);
        final Button get_btn = (Button) findViewById(R.id.get_btn);//获取按钮资源
        get_btn.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
                if (get_list.getVisibility() == View.VISIBLE) {
                    get_list.setVisibility(View.GONE);
                    get_list.smoothScrollToPosition(0);
                } else {
                    get_list.setVisibility(View.VISIBLE);
                }
            }

        });
        new Init(this);
    }

    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}