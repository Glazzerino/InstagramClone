package com.codepath.instagramclone.applications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;

public class CameraXApplication extends Application implements CameraXConfig.Provider{
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    //Return a default configuration for our CameraX implementation
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }
}
