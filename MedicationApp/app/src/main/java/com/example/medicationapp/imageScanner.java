package com.example.medicationapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.CameraX;
//import androidx.camera.core.Preview;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class imageScanner extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_CROP = 3;

    private TextureView mTextureView;
    private Button mButton;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mTextureView = findViewById(R.id.scanner);
        mButton = findViewById(R.id.takePhoto);

        // Check Camera Permission
        if (ContextCompat.checkSelfPermission(imageScanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }

        // Set-Up Camera Preview
//        final PreviewConfig previewConfig = new PreviewConfig.Builder().build();
        final Preview preview = new Preview.Builder().build();
//        final Preview preview = new Preview(previewConfig);


        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) mTextureView.getParent();
            parent.removeView(mTextureView);
            parent.addView(mTextureView, 0);

            mTextureView.setSurfaceTexture(output.getSurfaceTexture());
        });
        CameraX.bindToLifecycle(this, preview);

        // Set-Up Click Listener For The Taken Picture Button
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    // Request Camera Permission
//    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else {
                Toast.makeText(this, "Camera Permission Is Required.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Start Camera Activity To Take Picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                Log.e("imageScanner", "Error Creating Image File", ex);
            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Create A Unique File Name For The Captured Image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    // Handle The Captured Image And Start Image Cropping Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Crop.of(Uri.fromFile(new File(mCurrentPhotoPath)), Uri.fromFile(new File(mCurrentPhotoPath)))
                    .asSquare()
                    .start(this, REQUEST_IMAGE_CROP);
        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            // Handle The Cropped Image
            Uri resultUri = Crop.getOutput(data);
            // Upload The Image To FireBase Storage
            UploadImageToFirebaseStorage(resultUri);
        }
    }

    // Upload The Cropped Image To FireBase Storage
    private void UploadImageToFirebaseStorage(Uri imageUri) {
        // TODO: Add Code To Upload Image To Firebase Storage

        // Get A Reference To The Firebase Storage Instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String UserId = firebaseAuth.getCurrentUser().getUid();


        /**
         * Check If Any Logged-In user. If Yes, then save the images to there specific UID folder.
         */
        if (UserId != null){
            // Create A Reference To The Image File In Firebase Storage
            String fileName = UUID.randomUUID().toString() + ".png";
            StorageReference storageReference = storage.getReference().child("Scans/" + UserId + fileName);

            // Create A File Input Stream For The Image File
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                Log.e("imageScanner", "Error Opening File: " + e.getMessage());
                return;
            }

            // Upload The Image File To Firebase Storage
            UploadTask uploadTask = storageReference.putStream(stream);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get The Download URL of the Uploaded Image
                    storageReference.getDownloadUrl().addOnCompleteListener(downloadTask ->{
                        if (downloadTask.isSuccessful()) {
                            Uri downloadUri = downloadTask.getResult();

                            // Do Something with the download URI, Such a saving it to a database Or Displaying the images in an ImageView
                            Log.d("imageScanner", "Download URL: " + downloadUri.toString());
                        } else {
                            Log.e("imageScanner", "Error Getting Download URL: " + downloadTask.getException().getMessage());
                        }
                    });
                } else {
                    Log.e("imageScanner", "Error Uploading File: " + task.getException().getMessage());
                }
            });
        }

        /**
         * If None, Then Save in Guest Folder
         */
        else {
            // Create A Reference To The Image File In Firebase Storage
            String fileName = UUID.randomUUID().toString() + ".png";
            StorageReference storageReference = storage.getReference().child("Scans/guests" + fileName);

            // Create A File Input Stream For The Image File
            InputStream stream = null;
            try {
                stream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                Log.e("imageScanner", "Error Opening File: " + e.getMessage());
                return;
            }

            // Upload The Image File To Firebase Storage
            UploadTask uploadTask = storageReference.putStream(stream);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get The Download URL of the Uploaded Image
                    storageReference.getDownloadUrl().addOnCompleteListener(downloadTask ->{
                        if (downloadTask.isSuccessful()) {
                            Uri downloadUri = downloadTask.getResult();

                            // Do Something with the download URI, Such a saving it to a database Or Displaying the images in an ImageView
                            Log.d("imageScanner", "Download URL: " + downloadUri.toString());
                        } else {
                            Log.e("imageScanner", "Error Getting Download URL: " + downloadTask.getException().getMessage());
                        }
                    });
                } else {
                    Log.e("imageScanner", "Error Uploading File: " + task.getException().getMessage());
                }
            });
        }


    }

}
