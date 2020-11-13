package com.example.navtry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import android.util.Log;
import android.util.SparseArray;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.TreeMap;


/**
 * App's Main Activity showing a simple usage of the picture taking service.
 */

public class FirstActivity extends BaseActivity implements PictureCapturingListener {


    TextView txtSampleDesc;
    private FaceDetector detector;
    Bitmap editedBitmap;
    int currentIndex = 0;
    int[] imageArray;
    private Uri imageUri;

    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";

    private static final String TAG = FirstActivity.class.getSimpleName();





    //The capture service
    private APictureCapturingService pictureService;

    private static final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    AlertDialog.Builder builder;
    AlertDialog dialog;

    private long backpressedtime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        @SuppressLint("InflateParams")
        View contentView = inflater.inflate(R.layout.activity_first, null, false);
        drawer.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.nav_activity1);

        // Checking/Requesting Permission at Runtime
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED))
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FirstActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(FirstActivity.this,
                        permissions, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(FirstActivity.this,
                        permissions, 1);
            }

        }
        else
        {
            /* do nothing */
            /* permission is granted */
        }


        //Alert Box for informing the user the importance of the app
        builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        ActivityCompat.requestPermissions(FirstActivity.this, permissions, 1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
        dialog = builder.create();


        Button musicbtn=(Button) contentView.findViewById(R.id.musicbtn);
        ImageView image = (ImageView) contentView.findViewById(R.id.imageView);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade);
        image.startAnimation(animation1);

        //Builds A Face Detector for detecting faces and classifications such as smiling faces.
        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        // getting instance of the Service from PictureCapturingServiceImpl
        pictureService = PictureCapturingServiceImpl.getInstance(this);
        musicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.this.showToast("Starting capture!");
                pictureService.startCapturing(FirstActivity.this);
            }
        });

    }


    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(FirstActivity.this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                          }
                      }
        );
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            //showToast("Done!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
//        if (pictureData != null && pictureUrl != null) {
//            runOnUiThread(() -> {
//               // final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
//                final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
//                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
//
//                if (pictureUrl.contains("1_pic.jpg")) {
//                    uploadFrontPhoto.setImageBitmap(scaled);
//                }
//            });

        //Creaetes a bitmap of the stored image for processing i.e. identifying faces
        final Bitmap bitm = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
        final int nh = (int) (bitm.getHeight() * (512.0 / bitm.getWidth()));
        final Bitmap bitmap = Bitmap.createScaledBitmap(bitm, 512, nh, true);
        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);
            //When a face is detected draw a rectangle on the face
            for (int index = 0; index < faces.size(); ++index) {
                Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);


                canvas.drawText("Face " + (index + 1), face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight(), paint);
                //If the Smiling Probability of the face is above 0.5 it is considered happy
                Log.d(TAG,"SP-->"+face.getIsSmilingProbability());
                if(face.getIsSmilingProbability() <= 0.5)
                {
                    showToast("Sad");
                    Intent sadintent = new Intent(FirstActivity.this, SadSongs.class);
                    startActivity(sadintent);
                }
                else
                {
                    showToast("Happy");
                    Intent happyintent = new Intent(FirstActivity.this, HappySongs.class);
                    startActivity(happyintent);
                }
                //txtSampleDesc.setText(txtSampleDesc.getText() + "FACE " + (index + 1) + "\n");
                //txtSampleDesc.setText(txtSampleDesc.getText() + "Smile probability:" + " " + face.getIsSmilingProbability() + "\n");


                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    canvas.drawCircle(cx, cy, 8, paint);
                }


            }

            if (faces.size() == 0) {
                showToast("Please Try Again");
            }
        } else {

        }
        //showToast("Picture saved to " + pictureUrl);
    }


    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                finishAffinity();
//            }
            if (backpressedtime + 2000 > System.currentTimeMillis()){
                //backToast.cancel();
                super.onBackPressed();
                return;
            }
            else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }
            backpressedtime = System.currentTimeMillis();
        }

    }

    // Override this method to decide what to do based on the user's response to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialog.show();
                }
                break;
        }

    }

}
