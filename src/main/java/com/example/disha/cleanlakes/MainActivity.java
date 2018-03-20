package com.example.disha.cleanlakes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    CardView mcardview1, mcardview2, logincard;
    Uri file;
    File localFile;
    private StorageReference mStorageRef;
    private FirebaseStorage mfirebasestorage;

    private static final int RC_PHOTO_PICKER = 1;
    private static final int CAMERA_REQUEST = 2;

    private File output; // file variable for saving images to local device
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfirebasestorage = FirebaseStorage.getInstance();
        mStorageRef = mfirebasestorage.getReference().child("images");
        mcardview1 = findViewById(R.id.reportCard);
        mcardview2 = findViewById(R.id.uploadCard);
        logincard = findViewById(R.id.loginCard);

        mcardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

                output=new File(dir,String.valueOf(System.currentTimeMillis())+".jpeg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                /*code for saving images separately on local directory*/
                startActivityForResult(intent,CAMERA_REQUEST);

            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        logincard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = Intent(MainActivity.this,login.class);
              //  startActivity(i);
            }
        });

        mcardview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),RC_PHOTO_PICKER );
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == CAMERA_REQUEST && data != null && data.getData() != null ) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(output), "image/jpeg");
            startActivity(i);

            file = Uri.fromFile(output);
            Uri selectedimage = data.getData();
            StorageReference Ref = mStorageRef.child("images/"+selectedimage.getLastPathSegment());

    //            StorageReference photoref = mStorageRef.child(selectedimage.getLastPathSegment());
    //            Ref.putFile(selectedimage);

            Ref.putFile(selectedimage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity.this,"success",Toast.LENGTH_SHORT).show();

                        //uploading image to firebase
//                        Uri selectedimage = data.getData();
//                        StorageReference photoref = mStorageRef.child(selectedimage.getLastPathSegment());
//                        Ref.putFile(selectedimage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

        }

        if(requestCode== RC_PHOTO_PICKER )
        {
            if(data == null)
            {
                Uri selectedimage = data.getData();
                StorageReference Ref = mStorageRef.child("images/"+selectedimage.getLastPathSegment());
                Ref.putFile(selectedimage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Toast.makeText(getApplicationContext(),"your photo has been uploaded",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}


