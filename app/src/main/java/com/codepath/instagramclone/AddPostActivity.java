package com.codepath.instagramclone;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCapture.OnImageCapturedCallback;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.codepath.instagramclone.models.Post;
import com.google.common.util.concurrent.ListenableFuture;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class AddPostActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    EditText etDesc;
    TextureView tvPhoto;
    Button btnPost;
    Button btnShutter;
    PreviewView pvCameraPreview;
    //Used to set and manage photo taking
    ImageCapture imageCapture;
    CameraSelector cameraSelector;
    String photoUri;
    public static final String TAG = "AddPostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        etDesc = findViewById(R.id.etDesc);
        btnPost = findViewById(R.id.btnPost);
        btnShutter = findViewById(R.id.btnShutter);
        pvCameraPreview = findViewById(R.id.pvCameraPreview);
        cameraSelector = new CameraSelector.Builder()
                //TODO: Add camera selection for selfies
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        photoUri = "";
        //Camera setup + Listener
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //Offload process to a new thread managed by the ListenableFuture class
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(this.getDisplay().getRotation())
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindToPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                //this isnt supposed to ever happen
                Log.e(TAG, "Error during CameraX bind to preview: " + e.toString());
            }
        }, ContextCompat.getMainExecutor(this));



        btnShutter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Declare filename as current time in seconds and .jpg in current directory
                    photoUri = String.valueOf(new Date().getTime() / 1000) + ".jpg";
                    ImageCapture.OutputFileOptions outputFileOptions =
                            new ImageCapture.OutputFileOptions.Builder(new File(getFilesDir()
                                    ,photoUri)).build();

                    imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(AddPostActivity.this),
                            new ImageCapture.OnImageSavedCallback() {
                                @Override
                                public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                    // insert your code here.
                                    Log.d(TAG, "image saved: " + getFilesDir()+photoUri);
                                }
                                @Override
                                public void onError(ImageCaptureException error) {
                                    // insert your code here.
                                }
                            }
                    );
                }
            });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDesc.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser user = ParseUser.getCurrentUser();
                savePost(description, user);
            }
        });
    }

    void bindToPreview(@NonNull ProcessCameraProvider cameraProvider) {

        CameraSelector cameraSelector = new CameraSelector.Builder()
                //TODO: Add camera selection for selfies
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(pvCameraPreview.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, preview);
    }

    private void savePost(String description, ParseUser user) {
        // Create new post object
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error submitting post: " + e.toString());
                    Toast.makeText(AddPostActivity.this, "Error submitting post",Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Post submitted!");
                etDesc.setText("");
            }
        });
    }
}