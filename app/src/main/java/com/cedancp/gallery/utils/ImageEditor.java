package com.cedancp.gallery.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

public class ImageEditor {

    public static Bitmap rotateImage(Bitmap bitmap) {
        Matrix mat = new Matrix();
        mat.postRotate(90);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), mat, true);

        return newBitmap;
    }
}
