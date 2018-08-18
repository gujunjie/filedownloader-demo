package com.example.abc.myapplication17;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.tv_sofar)
    TextView tvSofar;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_percent)
    TextView tvPercent;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.btn_pause)
    Button btnPause;

    FileDownloadListener listener;

    long fileSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FileDownloader.setup(MainActivity.this);


    }

    public void download() {

        listener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {


            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                int downloadId = task.getId();
                fileSize = FileDownloader.getImpl().getTotal(downloadId);
                tvTotal.setText(convertFileSize(fileSize));

                Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                int percent = (int) ((double) soFarBytes / (double) totalBytes * 100);
                tvPercent.setText(percent + "%");
                progressBar.setProgress(percent);
                int downloadId = task.getId();
                long sofar = FileDownloader.getImpl().getSoFar(downloadId);
                int speed = task.getSpeed();

                tvSpeed.setText(convertDownloadSpeed(speed));

                tvSofar.setText(convertFileSize(sofar));


            }

            @Override
            protected void completed(BaseDownloadTask task) {
                progressBar.setProgress(100);
                Toast.makeText(MainActivity.this, "下载完成!", Toast.LENGTH_SHORT).show();

                tvSofar.setText(convertFileSize(fileSize));
                tvPercent.setText("(" + "100%" + ")");
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Toast.makeText(MainActivity.this, "暂停下载", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {

            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        };


        FileDownloader.getImpl().create("http://192.168.42.16:8080/【电影家园www.idyjy.com下载】摩天营救.HD韩版中字.mkv")
                .setPath(Environment.getExternalStorageDirectory() + "/mydownload" + "/【电影家园www.idyjy.com下载】摩天营救.HD韩版中字.mkv")
                .setListener(listener).start();
    }


    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    public String convertDownloadSpeed(int speed) {
        int b = 1024;
        int kb = b * 1024;
        int mb = kb * 1024;
        DecimalFormat df = new DecimalFormat("0.0");

        if (speed <= mb) {
            return df.format(speed / (float) b) + "mb/s";
        } else if (speed <= kb) {
            return speed + "kb/s";
        } else {
            return df.format(speed * (float) b) + "b/s";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick({R.id.btn_start, R.id.btn_pause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    download();
                }
                break;
            case R.id.btn_pause:
                FileDownloader.getImpl().pause(listener);
                break;
        }
    }
}
