package me.matoosh.undernet.file;

import android.os.Build;

import java.io.File;

import me.matoosh.undernet.MainActivity;

/**
 * File manager implementation for Android.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public class AndroidFileManager extends FileManager {
    @Override
    public File getAppFolder() {
        return MainActivity.instance.getFilesDir();
    }

    @Override
    public File getContentFolder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return MainActivity.instance.getExternalMediaDirs()[0];
        } else {
            File contentDir = new File(MainActivity.instance.getExternalFilesDir(null).getAbsoluteFile() + "/content");
            if(!contentDir.exists()) {
                contentDir.mkdir();
            }
            return contentDir;
        }

    }

    @Override
    public File getCacheFolder() {
        return MainActivity.instance.getCacheDir();
    }
}
