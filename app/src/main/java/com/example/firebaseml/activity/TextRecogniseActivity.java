package com.example.firebaseml.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebaseml.Helper.GraphicOverlay;
import com.example.firebaseml.Helper.TextGraphic;
import com.example.firebaseml.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.OnCameraKitEvent;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class TextRecogniseActivity extends AppCompatActivity {

    CameraView cameraView;
    GraphicOverlay graphicOverlay;
    Button btnCapture;
    AlertDialog waitingDialog;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognise);

        //Init Views
        cameraView = (CameraView) findViewById(R.id.camera_view);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.graphic_overlay);
        btnCapture = (Button) findViewById(R.id.btn_capture);

        waitingDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Please Wait...")
                .setCancelable(false)
                .build();

        //event
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });

        //Event Camera View
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                recognizeText(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }

    private void recognizeText(Bitmap bitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en"))
                .build();

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getCloudTextRecognizer(options);

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                        drawTextResult(firebaseVisionText);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MLKitError",e.getMessage());

                    }
                });
    }

    private void drawTextResult(FirebaseVisionText firebaseVisionText) {

        //Get Text block
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0){
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            return;
        }
        graphicOverlay.clear();

        for (int i = 0; i<blocks.size(); i++){
            //Get lines
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

            for (int j = 0; j < lines.size(); j++){
                List<FirebaseVisionText.Element> elements= lines.get(i).getElements();

                for (int k = 0; k < elements.size(); k++){
                    //Draw Elements
                    TextGraphic textGraphic = new TextGraphic(graphicOverlay,elements.get(k));
                    graphicOverlay.add(textGraphic);
                }
            }
        }
        //Dismiss Dialog
        waitingDialog.dismiss();
    }
}
