package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;

/**
 * Created by nugsky on 17/10/17.
 */

public class FacePictureCallback implements CameraSource.PictureCallback {
    static String logId = "FacePictureCallback";
    static int count=0;
    private int x,y,width,height;

    public FacePictureCallback(float x, float y, float width, float height) {
        this.x = (int) ((x>=0)?x:0);
        this.y = (int) ((y>=0)?y:0);
        this.width = (int) width;
        this.height = (int) height;
    }

    @Override
    public void onPictureTaken(byte[] bytes) {
        Log.d(logId,"called callback "+count++);
        Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        temp = flip(temp);
        LastPhotoWrapper.bitmap = temp;

//        Log.d(logId, String.format("%d,%d - %d %d",x,y,width,height));
        Intent intent = new Intent(FaceTrackerActivity.sInstance, PhotoActivity.class);
        FaceTrackerActivity.sInstance.startActivity(intent);
    }

    public static Bitmap drawRectangle(Bitmap source, int x, int y, int width, int height){
        Bitmap result = Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight());
        for(int i=x;i<(x+width);i++){
            result.setPixel(i,y, Color.RED);
            result.setPixel(i,y+height,Color.RED);
        }
        for(int j=y;j<(y+height);j++){
            result.setPixel(x,j,Color.RED);
            result.setPixel(x+width,j,Color.RED);
        }
        return result;
    }

    public static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();

        matrix.preScale(-1.0f, 1.0f);

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
