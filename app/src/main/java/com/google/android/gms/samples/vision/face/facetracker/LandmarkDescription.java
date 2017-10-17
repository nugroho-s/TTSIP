package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Color;

/**
 * Created by nugsky on 17/10/17.
 */

public class LandmarkDescription {
    public String name;
    public int color;

    public LandmarkDescription(String name) {
        this.name = name;
        this.color = Color.BLACK;
    }

    public LandmarkDescription(String name, int color) {

        this.name = name;
        this.color = color;
    }
}
