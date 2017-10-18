package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Bitmap;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {
    public static PhotoActivity sInstance;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        sInstance = this;
        bitmap = LastPhotoWrapper.bitmap;
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        FaceView overlay = (FaceView) findViewById(R.id.faceView);
        overlay.setContent(bitmap, faces);
    }
}
