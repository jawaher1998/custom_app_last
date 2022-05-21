package com.example.firbasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.util.Locale;

public class SignIn extends AppCompatActivity {

    private Connection connection = null;

    Button signIn, signUp , forgetPassButton;
    EditText Email,pass;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUptButton);
        Email = findViewById(R.id.userName);
        pass = findViewById(R.id.pass);
        forgetPassButton = (Button)findViewById(R.id.forgetPassButton);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, forgetpassword.class));
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();

                //startActivity(new Intent(SignIn.this, HomePage.class));

            }


        });
    }


    private void loginuser() {
        email = Email.getText().toString();
        password = pass.getText().toString();
        if (TextUtils.isEmpty(email) &&TextUtils.isEmpty(password)) {
            Email.setError("Please enter your email");
            pass.setError("Please enter your password");
            return;
        }
        if (TextUtils.isEmpty(email) ) {
            Email.setError("Please enter your email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            pass.setError("Please enter your password");
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            Toast.makeText(SignIn.this, "Fields email pattern..", Toast.LENGTH_LONG).show();
            return;

        }

        progressDialog.setMessage("Loading");
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if (currentUser != null) {
                            if (currentUser.isEmailVerified()) {
                                progressDialog.setMessage("Sing in Account ...");
                                startActivity(new Intent(SignIn.this, healthinfo.class));
                                progressDialog.dismiss();

                            }else {
                                Toast.makeText(getApplicationContext(),"please verify your email address",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignIn.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }
}