package com.auzware.mlkit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    ImageView imageView;
    TextView textView;
    Button scanBtn;

    Bitmap imageBitmap;
    Uri imageUri;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int NETWORK_PERMISSION_CODE=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);
        scanBtn=findViewById(R.id.button);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE,NETWORK_PERMISSION_CODE);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for image labeling
                //processImage(imageUri);

                //for geting textfrom images
                //getTextFromImage(imageUri);

                //detect faces from image
                //detectFacesFromImage(imageUri);

                //geting mac address of android device
                String add=getMacAddress();
                textView.setText(add);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open camera take picture
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                someActivityResultLauncher.launch(cameraIntent);


                //open galary take picture
                openGallery();
            }
        });

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK) {

                        imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(imageBitmap);


                    }
                    if ( result.getResultCode()== Activity.RESULT_OK){

                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);


                    }
                }
            });


    private void detectPoseFromImage(Uri uri){
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();

        PoseDetector poseDetector = PoseDetection.getClient(options);

        InputImage image;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);

            Task<Pose> result =
                    poseDetector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Pose>() {
                                        @Override
                                        public void onSuccess(Pose pose) {

                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Failed To Detect Pose", Toast.LENGTH_SHORT).show();
                                        }
                                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void detectFacesFromImage(Uri uri){
        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        InputImage image;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);
            Task<List<Face>> result =
                    detector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            Toast.makeText(MainActivity.this, faces.size()+"Faces Found.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "No Faces Found.", Toast.LENGTH_SHORT).show();

                                        }
                                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getTextFromImage(Uri uri){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        InputImage image;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);

            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    textView.setText(visionText.getText());
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            textView.setText("No Text Found");

                                        }
                                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 30);
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                textView.setText(visionText.getText());
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        textView.setText("No Text Found");

                                    }
                                });
    }


    private void processImage(Uri uri) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(MainActivity.this,uri);

            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {

                            textView.setText(labels.get(0).getText());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           textView.setText("No Text Found!");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void processImage(Bitmap imgBitmap) {
        InputImage image = InputImage.fromBitmap(imgBitmap, 30);


        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        String value="";
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            value.concat(text+" : ");
                            float confidence = label.getConfidence();
                            int index = label.getIndex();
                        }

                        textView.setText(value);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText("no text found!");

                    }
                });

    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(gallery);
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                checkPermission(Manifest.permission.CAMERA,CAMERA_PERMISSION_CODE);
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);

            }
        }
        else if (requestCode == NETWORK_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                checkPermission(Manifest.permission.ACCESS_NETWORK_STATE,NETWORK_PERMISSION_CODE);

            }
        }
    }


    public String getMacAddress(){
        try{
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());

            String stringMac = "";

            for(NetworkInterface networkInterface : networkInterfaceList)
            {

                Toast.makeText(this, "Mac: "+networkInterface.getHardwareAddress(), Toast.LENGTH_SHORT).show();

//                if(networkInterface.getName().equals("wlan0"));
//                {
//
//                    for(int i = 0 ;i <networkInterface.getInterfaceAddresses().size(); i++){
//                        String stringMacByte = networkInterface.getInterfaceAddresses().get(i).toString();
//
//                        if(stringMacByte.length() == 1)
//                        {
//                            stringMacByte = "0" +stringMacByte;
//                        }
//
//                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
//                    }
//                    break;
//                }

            }
            return stringMac;
        }catch (SocketException e)
        {
            e.printStackTrace();
        }

        return  "0";
    }



}