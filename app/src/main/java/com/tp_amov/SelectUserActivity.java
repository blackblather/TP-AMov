package com.tp_amov;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
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

public class SelectUserActivity extends AppCompatActivity {
    public static final String GAME_MODE_1 = "M1";
    public static final String GAME_MODE_2 = "M2";
    public static final String GAME_MODE_3 = "M3";
    //Intent constants
    static final String EXTRA_USERS = "users";
    static final String EXTRA_ENCODED_IMAGES = "encodedImages";
    static final String EXTRA_USE_WEBSERVICE = "useWebservice";
    static final String EXTRA_GAME_MODE = "gameMode";
    //Camera constants
    private static final int CAMERA_PERMISSION_CODE_P2 = 8;
    private static final int CAMERA_REQUEST_P2 = 7;
    private static final int CAMERA_PERMISSION_CODE_P1 = 6;
    private static final int CAMERA_REQUEST_P1 = 5;
    //File Picker constants
    private static final int IMAGE_FILE_PICKER_PERMISSION_CODE_P2 = 4;
    private static final int IMAGE_FILE_PICKER_REQUEST_P2 = 3;
    private static final int IMAGE_FILE_PICKER_PERMISSION_CODE_P1 = 2;
    private static final int IMAGE_FILE_PICKER_REQUEST_P1 = 1;
    //Control vars
    private String selectedMode;
    private SelectUserFragment selectUserFragment;
    private Bundle savedInstanceState;
    private MenuItem useWebservice;
    private MenuItem overwriteDBPicture;

    private void LoadFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (selectedMode) {
            case "btn_m1": selectUserFragment = new SelectUserFragmentM1(); break;
            case "btn_m2": selectUserFragment = new SelectUserFragmentM2(); break;
            case "btn_m3": selectUserFragment = new SelectUserFragmentM1(); break;  //TODO
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
        else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(selectUserFragment.game_mode.equals(GAME_MODE_1)) {
            getMenuInflater().inflate(R.menu.game_settings_menu, menu);
        }else if(selectUserFragment.game_mode.equals(GAME_MODE_2)){
            getMenuInflater().inflate(R.menu.game_settings_menu_m2, menu);
        }else if(selectUserFragment.game_mode.equals(GAME_MODE_3)){
            //TODO
            getMenuInflater().inflate(R.menu.game_settings_menu, menu);
        }
            //Opcoes comuns a todos os menus
            useWebservice = menu.findItem(R.id.usar_webservice);
            overwriteDBPicture = menu.findItem(R.id.overwrite_db_profile_pio);
            if (savedInstanceState != null) { //Isto está aqui porque os menus são criados depois do onRestoreInstanceState
                useWebservice.setChecked(savedInstanceState.getBoolean("useWebservice", false));
                overwriteDBPicture.setChecked(savedInstanceState.getBoolean("useDBPicture", true));
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
            case R.id.overwrite_db_profile_pio:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);
                return true;
            case R.id.take_picture:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE_P1);
                return true;
            case R.id.browse_picture:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_FILE_PICKER_PERMISSION_CODE_P1);
                return true;
            case R.id.take_picture_p1:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE_P1);
                return true;
            case R.id.browse_picture_p1:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_FILE_PICKER_PERMISSION_CODE_P1);
                return true;
            case R.id.take_picture_p2:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE_P2);
                return true;
            case R.id.browse_picture_p2:
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_FILE_PICKER_PERMISSION_CODE_P2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("useWebservice", useWebservice.isChecked());
        outState.putBoolean("useDBPicture", overwriteDBPicture.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (((requestCode == CAMERA_PERMISSION_CODE_P1) || (requestCode == CAMERA_PERMISSION_CODE_P2)) && grantResults.length != 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                takePicture(requestCode);
            }
            else
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
        else if (((requestCode == IMAGE_FILE_PICKER_PERMISSION_CODE_P1) || (requestCode == IMAGE_FILE_PICKER_PERMISSION_CODE_P2)) && grantResults.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read access granted", Toast.LENGTH_SHORT).show();
                pickFile(requestCode);
            }
            else
                Toast.makeText(this, "Read access denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePicture(int permissionCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionCode);   //CAMERA PERMISSION NOT GRANTED
        else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if(permissionCode == CAMERA_PERMISSION_CODE_P1)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_P1);
            else if(permissionCode == CAMERA_PERMISSION_CODE_P2)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_P2);
        }
    }

    private void pickFile(int permissionCode){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        if(permissionCode == IMAGE_FILE_PICKER_PERMISSION_CODE_P1)
            startActivityForResult(chooserIntent, IMAGE_FILE_PICKER_REQUEST_P1);
        else if(permissionCode == IMAGE_FILE_PICKER_PERMISSION_CODE_P2)
            startActivityForResult(chooserIntent, IMAGE_FILE_PICKER_REQUEST_P2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (((requestCode == CAMERA_REQUEST_P1) || (requestCode == CAMERA_REQUEST_P2)) && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ImageView img = GetImageViewForRequest(requestCode);
                if(img != null)
                    img.setImageBitmap(photo);
            } else if (((requestCode == IMAGE_FILE_PICKER_REQUEST_P1) || (requestCode == IMAGE_FILE_PICKER_REQUEST_P2)) && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap photo = BitmapFactory.decodeFile(picturePath);
                ImageView img = GetImageViewForRequest(requestCode);
                if(img != null)
                    img.setImageBitmap(photo);
            }
        }catch (OutOfMemoryError outOfMemoryError){
            Toast toast = Toast.makeText(getApplicationContext(), "Image size is too big, Aborting..."+ outOfMemoryError.getLocalizedMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private ImageView GetImageViewForRequest(int requestCode){
        if((requestCode == CAMERA_REQUEST_P1) || (requestCode == IMAGE_FILE_PICKER_REQUEST_P1))
             return (ImageView)findViewById(R.id.profile_image);
        else if((requestCode == CAMERA_REQUEST_P2) || (requestCode == IMAGE_FILE_PICKER_REQUEST_P2))
            return (ImageView)findViewById(R.id.profile_image2);
        return null;
    }

    boolean isOverwriteDBPictureChecked(){
        return overwriteDBPicture.isChecked();
    }
}
