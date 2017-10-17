package com.google.android.gms.samples.vision.face.facetracker;

import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;

/**
 * Created by nugsky on 17/10/17.
 */

public class FacePictureCallback implements CameraSource.PictureCallback {
    static String logId = "FacePictureCallback";

    @Override
    public void onPictureTaken(byte[] bytes) {
        Log.d(logId,"called callback");
    }
}
