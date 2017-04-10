package com.jad.pholoc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.DisplayMetrics;

/**
 * Operations with Bitmaps
 *
 * @author Jorge Alvarado
 */
public class BitmapOperations {

    /**
     * Generate a thumbnail of an image of a location and save it in the ".thumbnails"
     * folder inside the folder where the image is located
     *
     * @param path      Directory where the image is located
     * @param namePhoto Name of the image (with the extension)
     * @param dm        DisplayMetrics
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void generateThumbnail(String path, String namePhoto,
                                  DisplayMetrics dm) throws FileNotFoundException, IOException {
        int d_width = dm.widthPixels;
        // Get the photo
        Bitmap bitmap = BitmapFactory.decodeFile(path + namePhoto);
        // Get the EXIF data
        ExifInterface exif = new ExifInterface(path + namePhoto);
        int exifOrientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

        // Depending on the orientation of the photo we set the generation parameters of the thumbnail
        int rotate = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        // Get the size of the photo (in pixels)
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // The width of the thumbnail will be the width of the screen divided by 3
        int newWidth = d_width / 3;
        float scale = 1;
        // Calculate the scaling of the thumbnail
        if (width > height) {
            // Horizontal image: scaled by width
            scale = ((float) newWidth) / width;
        } else {
            // Vertical image: scaled by height
            scale = ((float) newWidth) / height;
        }

        Matrix matrix = new Matrix();
        // Rotate the image
        matrix.preRotate(rotate);
        // Reduce the image
        matrix.postScale(scale, scale);
        // Create bitmap for thumbnail
        Bitmap miniaturaBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);

        // Save the thumbnail
        File folder = new File(path + ".thumbnails/");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File myPath = new File(folder, namePhoto);
        FileOutputStream fos = null;

        fos = new FileOutputStream(myPath);
        miniaturaBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.flush();
    }

    /**
     * Delete thumbnail if exists
     *
     * @param path      Path where the image (directory)
     * @param namePhoto Name of the image
     */
    public void deleteMiniatura(String path, String namePhoto) {
        File file = new File(path + ".thumbnails/" + namePhoto);
        // If the existing thumbnail is deleted
        if (file.exists()) {
            file.delete();
        }
    }
}
