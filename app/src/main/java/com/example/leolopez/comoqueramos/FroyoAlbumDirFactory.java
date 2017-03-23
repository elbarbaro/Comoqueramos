package com.example.leolopez.comoqueramos;

import android.os.Environment;

import java.io.File;

/**
 * Created by leolopez94 on 23/03/17.
 *
 */

public class FroyoAlbumDirFactory extends AlbumStorageDirFactory {

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );
    }
}
