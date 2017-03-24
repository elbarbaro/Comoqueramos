package com.example.leolopez.comoqueramos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leolopez94 on 23/03/17.
 */

public class FTPUploadImageTask extends AsyncTask<String, Integer, String> implements CopyStreamListener {

    private static final String TAG = FTPUploadImageTask.class.getSimpleName();

    private File file;
    private String result;
    private OnUploadImageListener listener;
    private Context context;

    @Override
    protected void onPreExecute() {
        listener.onShowProgress();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String host = params[0];
        String directory = params[1];
        String username = params[2];
        String pass = params[3];
        String filePath = params[4];

        Log.d(TAG, filePath);

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(host);

            if(ftpClient.login(username, pass)){


                ftpClient.changeWorkingDirectory(directory);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                ftpClient.enterLocalPassiveMode();

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, arrayOutputStream);

                file = new File(context.getCacheDir(), "filetemp.jpg");
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(arrayOutputStream.toByteArray());
                outputStream.flush();
                outputStream.close();

                ftpClient.setCopyStreamListener(this);
                String remoteFilename = "IMAGEN_POZO" + CameraActivity.JPEG_FILE_SUFFIX;
                InputStream inputStream = new FileInputStream(file);
                boolean done = ftpClient.storeFile(remoteFilename, inputStream);
                inputStream.close();
                if (done) {
                    Log.d(TAG, "The first file is uploaded successfully.");
                } else{
                    Log.d(TAG, "Error upload file");
                }
                result = "Successfully";
                return result;
            }else {
                return "Login Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "error";
            return result;
        }
        finally {
            try {
                ftpClient.isConnected();
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        listener.onSetProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onHideProgress();
        listener.onUploadImageFinish(s);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(OnUploadImageListener listener) {
        this.listener = listener;
    }

    @Override
    public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        int percent = (int)(totalBytesTransferred*100/file.length());
        publishProgress(percent);
    }

    @Override
    public void bytesTransferred(CopyStreamEvent event) {

    }
}
