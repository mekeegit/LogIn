package com.miki.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button mRegister;
    private EditText mEmail;
    private EditText mPassword;
    private TextView mSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        mRegister = (Button) findViewById(R.id.registerButton);

        mEmail = (EditText) findViewById(R.id.textEmail);
        mPassword = (EditText) findViewById(R.id.textPassword);

        mSignin = (TextView)findViewById(R.id.textSignIn);

        mRegister.setOnClickListener(this);
        mSignin.setOnClickListener(this);

    }

    private void registerUser(){

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your Email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your Password", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Please wait");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v == mRegister){
            registerUser();
        }
        if(v == mSignin){
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
