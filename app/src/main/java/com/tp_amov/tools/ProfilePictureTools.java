package com.tp_amov.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class ProfilePictureTools {
    private static final String profilePictureFolder = "ProfilePictures";

    private Bitmap DrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap MediumBitmapCompression(Bitmap bitmap){
        // Initialize a new ByteArrayStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Sets compression attributes
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        //Creates the new compressed image
        byte[] byteArray = stream.toByteArray();
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
    }

    private Bitmap CropImageToCenter(Bitmap bitmap){
        Bitmap centeredImg;
        if (bitmap.getWidth() >= bitmap.getHeight()){
            centeredImg = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        }else{
            centeredImg = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return centeredImg;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap GetMediumCompressedBitmapFromRawDrawable(Drawable picture){
        return MediumBitmapCompression(Bitmap.createScaledBitmap(CropImageToCenter(DrawableToBitmap(picture)), 240, 240, true));
    }

    public String saveImage(Bitmap bitmap, Context context){
        final String randomFileName = UUID.randomUUID().toString().replace("-", "");
        File path = new File(context.getFilesDir(),profilePictureFolder);

        if(!path.exists()) {
            if(!path.mkdirs()) {
                Toast toast = Toast.makeText(context, "Error creating profile image directory path!", Toast.LENGTH_LONG);
                toast.show();
                return null;
            }
        }

        File imagePath = new File(path, randomFileName + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return imagePath.getCanonicalPath();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE_EXCEPTION", e.getMessage(), e);
            return null;
        }
    }

    public Bitmap LoadImage(String imagePath){
        File image = new File(imagePath);
        if(image.exists()) {
            Log.d("File Loaded", "FileName:" + image.getName());
            return BitmapFactory.decodeFile(imagePath);
        }
        return null;
    }

//TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
//    public File[] getAllImagesOnProfilePicturesDir(Context context){
//        //For testing purposes
//        File directory = new File(context.getFilesDir(),profilePictureFolder);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (File file : files) {
//            Log.d("Files", "FileName:" + file.getName());
//            String filePath = file.getPath();
//            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//        }
//        return files;
//    }
//TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING

}
