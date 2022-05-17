package com.example.firbasedb;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firbasedb.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    ImageView imageView;
    TextView button;
    EditText name, email, phonNo;
    Button save, sting;
    Uri uri;
    int request_code;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button healthInfo;

    ActivityResultLauncher<Intent> someActivityResultLauncher1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.editprofile);
        name = (EditText) findViewById(R.id.EditText1);
        email = (EditText) findViewById(R.id.EditText2);
        phonNo = (EditText) findViewById(R.id.EditText3);
        sting = (Button) findViewById(R.id.button4);
        imageView = (ImageView) findViewById(R.id.imageView3);
        button = (TextView) findViewById(R.id.textView11);
        save = (Button) findViewById(R.id.save);
        email.setEnabled(false);
        healthInfo=(Button) findViewById(R.id.healthInfo);
        View view = this.getCurrentFocus();
        getSomeActivityResultLauncher();
        healthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfile.this,healthinfo.class));
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkuser();
    }

    private void checkuser() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), SignIn.class));
            finish();
        } else {
            lodinfo();
        }
    }

    private void lodinfo() {
        progressDialog.setMessage("Lod info...");
        progressDialog.show();
        FirebaseDatabase profile_uri = FirebaseDatabase.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name_1 = "" + ds.child("name").getValue();
                            String email_1 = "" + ds.child("email").getValue();
                            String phone_1 = "" + ds.child("PhoneNo").getValue();
                            String image_1  ="" + ds.child("image_uri").getValue();

                            if(image_1 != "") {
                                Picasso.get()
                                        .load(image_1)
                                        .placeholder(R.drawable.person)
                                        .error(R.drawable.person)
                                        .into(imageView);
                                name.setText(name_1);
                                email.setText(email_1);
                                phonNo.setText(phone_1);
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Updating profile", Toast.LENGTH_SHORT).show();
                                name.setText(name_1);
                                email.setText(email_1);
                                phonNo.setText(phone_1);
                                progressDialog.dismiss();


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void gallery(View view) {

        showImagePicDialog();
    }

    public ActivityResultLauncher<Intent> getSomeActivityResultLauncher() {


        someActivityResultLauncher1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && data != null) {
                        if (request_code == 100) {
                            uri = data.getData();
                            imageView.setImageURI(uri);
                            Toast.makeText(EditProfile.this, "uri gallery is " + uri.getPath(), Toast.LENGTH_LONG).show();

                        } else if (request_code == 200) {
//                          bitmap= (Bitmap)  data.getExtras().get("data");
//                          imageView.setImageBitmap(bitmap);
                            try {
                                ContentResolver cr = getContentResolver();
                                try {
                                    // Creating a Bitmap with the image Captured
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                                    // Setting the bitmap as the image of the
                                    imageView.setImageBitmap(bitmap);
                                    Toast.makeText(EditProfile.this, "uri is camera" + uri.getPath(), Toast.LENGTH_LONG).show();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (IllegalArgumentException e) {
                                if (e.getMessage() != null)
                                    Log.e("Exception", e.getMessage());
                                else
                                    Log.e("Exception", "Exception");
                                e.printStackTrace();

                            }
                        }
                    }
                }
        );

        return someActivityResultLauncher1;
    }


    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0) {

                            if (!checkCameraPermission()) {
                                requestCameraPermission();
                                Toast.makeText(EditProfile.this, "successful Image", Toast.LENGTH_LONG).show();
                            }
                        } else if (which == 1) {

                            if (!checkStoragePermission()) {
                                requestStoragePermission();
                                Toast.makeText(EditProfile.this, "successful add Image", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }
        );
        builder.create().show();
    }

    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestStoragePermission() {
        request_code = 100;
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher1.launch(intent);

    }

    private void requestCameraPermission() {
        request_code = 200;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new-photo-name.jpg");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        someActivityResultLauncher1.launch(intent);
    }

    ////////database firebase////////
    String nameupdate, emailupate, phoneupadte;

    public void Save(View view) {
        nameupdate = name.getText().toString();
        emailupate = email.getText().toString();
        phoneupadte = phonNo.getText().toString();
        updateprofile();


    }
    private String getfileextiontio(Uri u){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(u));
    }
    private void updateprofile() {
        progressDialog.setMessage("Updating profile");
        progressDialog.show();

        Toast.makeText(getApplicationContext(), "Updating profile", Toast.LENGTH_SHORT).show();
        if (uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", nameupdate);
            hashMap.put("email", emailupate);
            hashMap.put("PhoneNo", phoneupadte);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
            reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "profile update...", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            String file_pathname = "profile_image/" + "" + firebaseAuth.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Account");
            storageReference = FirebaseStorage.getInstance().getReference(file_pathname);
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            UploadTask uploadTask = storageReference.putBytes(data);
            StorageReference storageReference1 = storageReference.child(System.currentTimeMillis() + "." + getfileextiontio(uri));

            storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uploadTask.isSuccessful()) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("image_uri",String.valueOf(uri));
                                hashMap.put("name", nameupdate);
                                hashMap.put("email", emailupate);
                                hashMap.put("PhoneNo", phoneupadte);
                                reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Snackbar.make(findViewById(android.R.id.content), "upload profile", Snackbar.LENGTH_LONG).show();

                                                progressDialog.dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
        startActivity(new Intent(EditProfile.this,Account.class));
    }
    public void logout (View view){
        if (view.getId() == R.id.logout) {
            firebaseAuth.getInstance().signOut();
            startActivity(new Intent(EditProfile.this, SignIn.class));

        }
    }
}
























