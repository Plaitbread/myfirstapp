package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    ImageButton mCaptureBtn;
    ImageButton mChooseBtn;
    ImageView mImageView;
    boolean Capture1 = false;
    boolean Capture2 = false;
    boolean Gallery1 = false;

    boolean ImageWasCaptured;
    boolean ImageSelectedFromGallery;

    Uri image_uri;
    private int IMAGE_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Setting Texts

// Using this method of writing strings is not recommended as the text will only display in English.
// Here shows how to do that: https://www.youtube.com/watch?v=q_gujMR2ggE&ab_channel=BrianFraser

        TextView WelcomeText = (TextView) findViewById(R.id.textView2);
        WelcomeText.setTextSize(18);
        WelcomeText.setText("Welcome to Seer");

        TextView Instructions = (TextView) findViewById(R.id.textView3);
        Instructions.setText("Please fill the spaces with the appropriate answers");

        TextView NameInst = (TextView) findViewById(R.id.NameID);
        NameInst.setText("Enter your full name: ");

        Button SubmitButton = (Button) findViewById(R.id.SubmitButton);
        SubmitButton.setText("Submit");

        //Initializing new variables for easier access
        mImageView = findViewById(R.id.image_view);
        mCaptureBtn = findViewById(R.id.CameraButton);
        mChooseBtn = findViewById(R.id.ChooseButton);

        //Accessing Files via Image Button
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                //checking permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, request it
                        String[] GalleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for permission
                        requestPermissions(GalleryPermissions, PERMISSION_CODE);

                    }
                    else {
                        Gallery1 = true;
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    Gallery1 = true;
                    //system os is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });

        //Button Click for Accessing Camera
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if system os is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permission not enabled, request it
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}; //Array of the various permissions
                        //showing popup to request permissions
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        Capture1 = true;
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    Capture2 = true;
                    //system os < marshmallow
                    openCamera();
                }
            }
        }); {

        }
    }

    private void pickImageFromGallery() {
        Gallery1 = false;
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent, IMAGE_PICK_CODE);
        ImageSelectedFromGallery = true;
    }

    private void openCamera() {
        Capture1 = false;
        Capture2 = false;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        ImageWasCaptured = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if (Gallery1 == true) {
                        pickImageFromGallery();
                    }
                    else if(Capture1 == true || Capture2 == true) {
                            openCamera();
                        }
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);
        if (ImageWasCaptured == true) {
            if (resultCode == RESULT_OK) {
                //set image captured to out ImageView
                mImageView.setImageURI(image_uri);
                ImageWasCaptured = false;
            }
        }
        else {
            if (ImageSelectedFromGallery == true) {
                if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                    mImageView.setImageURI(data.getData());
                    ImageSelectedFromGallery = false;
                }
            }
        }
    }

    public void handleText(View v) {
        TextView NameInput = findViewById(R.id.NameScanner);
        String NameStringInput = NameInput.getText().toString();

        System.out.println(NameStringInput);

        TextView NameScanner = (TextView) findViewById(R.id.NameScanner);
        NameScanner.setText("");
    }
}