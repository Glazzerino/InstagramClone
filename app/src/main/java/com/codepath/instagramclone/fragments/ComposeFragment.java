package com.codepath.instagramclone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.codepath.instagramclone.R;
import com.codepath.instagramclone.models.Post;
import com.google.common.util.concurrent.ListenableFuture;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ComposeFragment extends Fragment {

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
    Context context;
    public static final String TAG = "ComposeFragment";

    public ComposeFragment(){

    }

    public static ComposeFragment newInstance(Context context){
        ComposeFragment fragment = new ComposeFragment();
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDesc = view.findViewById(R.id.etDesc);
        btnPost =  view.findViewById(R.id.btnPost);
        btnShutter =  view.findViewById(R.id.btnShutter);
        pvCameraPreview =  view.findViewById(R.id.pvCameraPreview);
        cameraSelector = new CameraSelector.Builder()
                //TODO: Add camera selection for selfies
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        photoUri = "";
        //Camera setup + Listener
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        //Offload process to a new thread managed by the ListenableFuture class
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getContext().getDisplay().getRotation())
                .setTargetResolution(new Size(720, 720))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindToPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                //this isn't supposed to ever happen
                Log.e(TAG, "Error during CameraX bind to preview: " + e.toString());
            }
        }, ContextCompat.getMainExecutor(getContext()));

        btnShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Declare filename as current time in seconds and .jpg in current directory
                photoUri =  getContext().getFilesDir() + "/" + String.valueOf(new Date().getTime() / 1000) + ".jpg";
                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(new File(photoUri)).build();

                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                //Freeze preview so it shows the taken image
                                try {
                                    cameraProviderFuture.get().unbindAll();
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Could not unbind camera provider from pvPreviewView: " + e.toString());
                                }
                                Log.d(TAG, "image saved: " + photoUri);
                            }
                            @Override
                            public void onError(ImageCaptureException error) {
                                Log.e(TAG, "Failed to save capture image: " + error.toString());
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
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser user = ParseUser.getCurrentUser();

                File photo = new File(photoUri);
                ParseFile photoParseFile = new ParseFile(photo);
                //Wait until the parsefile is done saving. Otherwise it might be null by the time we tried uploading
                photoParseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        savePost(description, user, photoParseFile);
                    }
                });
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

    private void savePost(String description, ParseUser user, ParseFile photo) {
        // Create new post object
        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);

        post.setImage(photo);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error submitting post: " + e.toString());
                    Toast.makeText(getContext(), "Error submitting post",Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Post submitted!");
                    Toast.makeText(getContext(), "Post submitted!", Toast.LENGTH_SHORT).show();
                    etDesc.setText("");
                    // Setup return data for main activity
                    Intent data = new Intent();
                    data.putExtra("post", post);

                }
            }
        });
    }
}