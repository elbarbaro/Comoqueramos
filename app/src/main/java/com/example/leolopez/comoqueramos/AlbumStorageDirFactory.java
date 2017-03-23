package com.example.leolopez.comoqueramos;

import java.io.File;

/**
 * Created by leolopez94 on 23/03/17.
 *
 */

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
