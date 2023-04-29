package com.example.medicationapp2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class imageScanner extends AppCompatActivity implements CropImageView.OnGetCroppedImageCompleteListener, CropImageView.OnSetImageUriCompleteListener {
    private CropImageView mCropImageView;
    private Uri mCropImageUri, imageUri;
    private View mProgressView;
    private TextView mProgressViewText;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mProgressView = findViewById(R.id.ProgressView);
        mProgressViewText = (TextView) findViewById(R.id.ProgressViewText);
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    /**
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnGetCroppedImageCompleteListener(null);
    }

    /**
     * On load image button click, start pick image chooser activity
     */
    public void onLoadImageClick(View view){
        CropImage.startPickImageActivity(this);
    }

    /**
     * Crop the image and set it back to the cropping view
     */
    public void onCropImageClick(View view){
        mCropImageView.getCroppedImageAsync();
        mProgressViewText.setText("Cropping...");
        mProgressView.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param view The crop image view that cropping of image was complete.
     * @param bitmap the cropped image bitmap (null if failed)
     * @param error if error occurred during cropping will contain the error, otherwise null.
     */
    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (error == null) {
            if (bitmap != null) {
//                mCropImageView.setImageBitmap(bitmap);

                // Set the bitmap to the CropImageView
                mCropImageView.setImageBitmap(bitmap);

// Get the Uri of the cropped image
//                Uri croppedImageUri = mCropImageView.getImageUri();

                //                Uri imageUri = getImageUri(bitmap);
//                imageUri = mCropImageView;
//                imageUri = mCropImageView.setImageUriAsync();
                UploadImageToFirebaseStorage(mCropImageView);
            }
        } else {
            Log.e("Crop", "Failed to crop image", error);
            Toast.makeText(this, "Crop Nahi Hua", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     * @param view The crop image view that loading of image was complete.
     * @param uri the URI of the image that was loading
     * @param error if error occurred during loading will contain the error, otherwise null.
     */
    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        mProgressView.setVisibility(View.INVISIBLE);
        if (error != null){
            Log.e("Crop", "Failed to load image for cropping", error);
            Toast.makeText(this, "Something went wrong, Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For Api >= 23, we need to check specifically that we need permission to read external storage, but we don't know if we
            // need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermission = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // Request Permission and handle the result in onRequestImagePermissionResult()
                requirePermission = true;
                mCropImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }

            if (!requirePermission) {
                mCropImageView.setImageUriAsync(imageUri);
                mProgressViewText.setText("Loading...");
                mProgressView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     *
     * @param requestCode The request code passed in {@link #requestPermissions android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mCropImageView.setImageUriAsync(mCropImageUri);
            mProgressViewText.setText("Loading...");
            mProgressView.setVisibility(View.VISIBLE);
        } else{
            Toast.makeText(this, "Required Permission Not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadImageToFirebaseStorage(CropImageView cropImageView) {
        // TODO: Add Code To Upload Image To Firebase Storage

        // Get A Reference To The Firebase Storage Instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        /**
         * Check If Any Logged-In user. If Yes, then save the images to there specific UID folder.
         */
        if (firebaseUser != null){
            if (cropImageView != null) {
                // Create A Reference To The Image File In Firebase Storage
                String fileName = UUID.randomUUID().toString() + ".png";
                StorageReference storageReference = storage.getReference().child("Scans/" + firebaseUser + fileName);

                // Get the cropped bitmap from CropImageView and compress it into a ByteArrayOutputStream and convert it to InputStream
                Bitmap croppedBitmap = cropImageView.getCroppedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream stream = new ByteArrayInputStream(baos.toByteArray());

                // Upload The Image File To Firebase Storage
                UploadTask uploadTask = storageReference.putStream(stream);
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get The Download URL of the Uploaded Image
                        storageReference.getDownloadUrl().addOnCompleteListener(downloadTask -> {
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
            else {
                Toast.makeText(this, "CropView Empty", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * If None, Then Save in Guest Folder
         */
        else {
            if (cropImageView != null) {
                // Create A Reference To The Image File In Firebase Storage
                String fileName = UUID.randomUUID().toString() + ".png";
                StorageReference storageReference = storage.getReference().child("Scans/guests" + fileName);

                // Get the cropped bitmap from CropImageView and compress it into a ByteArrayOutputStream and convert it to InputStream
                Bitmap croppedBitmap = cropImageView.getCroppedImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream stream = new ByteArrayInputStream(baos.toByteArray());

                // Upload The Image File To Firebase Storage
                UploadTask uploadTask = storageReference.putStream(stream);
                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get The Download URL of the Uploaded Image
                        storageReference.getDownloadUrl().addOnCompleteListener(downloadTask -> {
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
            else {
                Toast.makeText(this, "CropView is NULL", Toast.LENGTH_SHORT).show();
            }
        }
    }



//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
}
