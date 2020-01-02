package com.tp_amov;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.tp_amov.models.SelectUserFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

public class SelectUserActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 4;
    public static final int CAMERA_REQUEST = 3;
    public static final int IMAGE_FILE_PICKER_PERMISSION_CODE = 2;
    public static final int IMAGE_FILE_PICKER_REQUEST = 1;
    public static final String profilePictureFolder = "ProfilePictures";
    static final String EXTRA_USERNAMES = "usernames";
    static final String EXTRA_IMG_PATHS = "imgPaths";
    static final String EXTRA_USE_WEBSERVICE = "useWebservice";
    private String selectedMode;
    private SelectUserFragment selectUserFragment;
    private Bundle savedInstanceState;
    private MenuItem useWebservice;
    private MenuItem useDBPicture;

    private void LoadFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (selectedMode){
            case "btn_m1": selectUserFragment = new SelectUserFragmentM1M3(); break;
            case "btn_m2": selectUserFragment = new SelectUserFragmentM2(); break;
            case "btn_m3": selectUserFragment = new SelectUserFragmentM1M3(); break;  //TODO
            default: return;
        }

        fragmentTransaction.add(R.id.fragment_layout, selectUserFragment);
        fragmentTransaction.commitNow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        //Set toolbar info
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Sudoku - Configuração do Jogo");
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        selectedMode = intent.getStringExtra(MainActivity.EXTRA_SELECTED_MODE);

        if(selectedMode != null)
            LoadFragment();
        else{
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_settings_menu, menu);
        useWebservice = menu.findItem(R.id.usar_webservice);
        useDBPicture = menu.findItem(R.id.use_db_profile_pio);
        if(savedInstanceState != null) { //Isto está aqui porque os menus são criados depois do onRestoreInstanceState porque os menus são criados depois!!
            useWebservice.setChecked(savedInstanceState.getBoolean("useWebservice", false));
            useDBPicture.setChecked(savedInstanceState.getBoolean("useDBPicture", true));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.usar_webservice:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                selectUserFragment.SetUseWebservice(item.isChecked());
                return true;
            case R.id.use_db_profile_pio:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                return true;
            case R.id.take_picture:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                return true;
            case R.id.browse_picture:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_FILE_PICKER_PERMISSION_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("useWebservice", useWebservice.isChecked());
        outState.putBoolean("useDBPicture", useDBPicture.isChecked());
        super.onSaveInstanceState(outState);
    }

    private void takePicture(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //CAMERA PERMISSION NOT GRANTED
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private void pickFile(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, IMAGE_FILE_PICKER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length != 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                takePicture();
            }
            else
            {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == IMAGE_FILE_PICKER_PERMISSION_CODE && grantResults.length != 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Read access granted", Toast.LENGTH_SHORT).show();
                pickFile();
            }
            else
            {
                Toast.makeText(this, "Read access denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String saveImageOnAppFileDir(Bitmap bitmap){
        final String randomFileName = UUID.randomUUID().toString().replace("-", "");
        File path = new File(getFilesDir(),profilePictureFolder);
        if(!path.exists()){
            if(!path.mkdirs()){
                Toast toast = Toast.makeText(getApplicationContext(), "Error creating profile image directory path!", Toast.LENGTH_LONG);
                toast.show();
                //DO SOMETHING ELSE IF NEEDED
                return null;
            }
        }
        File mypath = new File(path, randomFileName+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE_EXCEPTION", e.getMessage(), e);
            return null;
        }
        return mypath.getName();
    }

    public Bitmap LoadImage(String filename){
        File directory = new File(getFilesDir(),profilePictureFolder);
        File file = new File(directory, filename);
        if(file.exists()) {
            Log.d("File Loaded", "FileName:" + file.getName());
            String filePath = file.getPath();
            return BitmapFactory.decodeFile(filePath);
        }
        else {
            return null;
        }
    }

    public File[] getAllImagesOnProfilePicturesDir(){
        //For testing purposes
        File directory = new File(getFilesDir(),profilePictureFolder);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (File file : files) {
            Log.d("Files", "FileName:" + file.getName());
            String filePath = file.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            int j = 0;
        }
        return files;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ImageView img = findViewById(R.id.profile_image);
                img.setImageBitmap(photo);
            } else if (requestCode == IMAGE_FILE_PICKER_REQUEST && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap photo = BitmapFactory.decodeFile(picturePath);
                ImageView img = findViewById(R.id.profile_image);
                img.setImageBitmap(photo);
            }
        }catch (OutOfMemoryError outOfMemoryError){
            Toast toast = Toast.makeText(getApplicationContext(), "Image size is too big, Aborting..."+ outOfMemoryError.getLocalizedMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public boolean isUseDBPictureChecked(){
        return useDBPicture.isChecked();
    }
}
