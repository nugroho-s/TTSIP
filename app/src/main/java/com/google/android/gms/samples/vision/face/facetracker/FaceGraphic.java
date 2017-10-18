/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private HashMap<Integer,LandmarkDescription> landmarkMap;

    private CameraSource mCamera;
    private boolean photoTaken;

    FaceGraphic(GraphicOverlay overlay, CameraSource camera) {
        super(overlay);

        mCamera = camera;

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        landmarkMap = new HashMap<>();
        landmarkMap.put(Landmark.BOTTOM_MOUTH,new LandmarkDescription("Bottom mouth",Color.GREEN));
        landmarkMap.put(Landmark.LEFT_CHEEK,new LandmarkDescription("Left Cheek"));
        landmarkMap.put(Landmark.LEFT_EAR,new LandmarkDescription("Left Ear"));
        landmarkMap.put(Landmark.LEFT_EAR_TIP,new LandmarkDescription("Left ear tip"));
        landmarkMap.put(Landmark.LEFT_EYE,new LandmarkDescription("left eye",Color.RED));
        landmarkMap.put(Landmark.LEFT_MOUTH,new LandmarkDescription("left mouth",Color.GREEN));
        landmarkMap.put(Landmark.NOSE_BASE,new LandmarkDescription("nose base",Color.BLUE));
        landmarkMap.put(Landmark.RIGHT_CHEEK,new LandmarkDescription("right cheek"));
        landmarkMap.put(Landmark.RIGHT_EAR,new LandmarkDescription("right ear"));
        landmarkMap.put(Landmark.LEFT_CHEEK,new LandmarkDescription("left cheek"));
        landmarkMap.put(Landmark.RIGHT_EAR_TIP,new LandmarkDescription("right ear tip"));
        landmarkMap.put(Landmark.RIGHT_EYE,new LandmarkDescription("right eye",Color.RED));
        landmarkMap.put(Landmark.RIGHT_MOUTH,new LandmarkDescription("right mouth",Color.GREEN));
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
        List<Landmark> komponenWajah = face.getLandmarks();
        canvas.drawCircle(0,0,10,mFacePositionPaint);
        for(Landmark landmark: komponenWajah){
            PointF point = landmark.getPosition();
            Paint mLandmark = new Paint();
            mLandmark.setColor(landmarkMap.get(landmark.getType()).color);
            canvas.drawCircle(translateX(point.x),translateY(point.y),5,mLandmark);
            Log.d("facegraphic",landmarkMap.get(landmark.getType()).name);
        }

        canvas.drawText((isEligibleImage(komponenWajah)?"y":"n"), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        if((isEligibleImage(komponenWajah))&&!photoTaken){
            FacePictureCallback callback = new FacePictureCallback(x-xOffset,y-yOffset,
                    face.getWidth()+(2*xOffset),face.getHeight()+(2*yOffset));
            mCamera.takePicture(new CameraSource.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, callback);
            photoTaken = true;
        }
    }

    private boolean isEligibleImage(List<Landmark> landmarks){
        boolean eyePresent, nosePresent, mouthPresent;
        eyePresent = nosePresent = mouthPresent = false;
        for(Landmark landmark:landmarks){
            int landmarkType = landmark.getType();
            if ((landmarkType==Landmark.LEFT_EYE)||(landmarkType==Landmark.RIGHT_EYE))
                eyePresent = true;
            else if ((landmarkType==Landmark.BOTTOM_MOUTH)||(landmarkType==Landmark.LEFT_MOUTH)||
                    (landmarkType==Landmark.RIGHT_MOUTH))
                mouthPresent = true;
            else if ((landmarkType==Landmark.NOSE_BASE))
                nosePresent = true;
        }
        return (eyePresent&&mouthPresent&&nosePresent);
    }
}
