package com.example.leolopez.comoqueramos;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by leolopez94 on 23/03/17.
 */

public class FTPUploadImageTask extends AsyncTask<String, Void, String> {

    private static final String TAG = FTPUploadImageTask.class.getSimpleName();

    private String result;
    private OnUploadImageListener listener;

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

                File file = new File(filePath);
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.onUploadImageFinish(s);
    }

    public void setListener(OnUploadImageListener listener) {
        this.listener = listener;
    }
}
