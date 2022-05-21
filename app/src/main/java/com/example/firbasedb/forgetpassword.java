package com.example.firbasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class forgetpassword extends AppCompatActivity {
    private EditText Email;

    private Button Reset, Back;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);


            Email = (EditText) findViewById(R.id.email);

            Reset = (Button) findViewById(R.id.btn_reset_password);

            Back = (Button) findViewById(R.id.btn_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);

            auth = FirebaseAuth.getInstance();
            Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    finish();
                }
            });

            Reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                    String email = Email.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        progressDialog.dismiss();
                        Email.setError( "Enter your registered email");
                        return;
                    }
                    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Fields email pattern", Toast.LENGTH_LONG).show();
                        return;


                    }


                    auth.sendPasswordResetEmail(email)

                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                            Toast.makeText(forgetpassword.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(forgetpassword.this,SignIn.class));
                                        }
                                    progressDialog.dismiss();
                                }
                            });
                }
            });
        }

    }