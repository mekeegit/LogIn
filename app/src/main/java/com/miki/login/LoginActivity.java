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

/**
 * Created by MIKI on 4/10/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mButton;
    private EditText mEmail;
    private EditText mPassword;
    private TextView mTextView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_login);

        mTextView = (TextView) findViewById(R.id.textSignUp);
        mEmail = (EditText) findViewById(R.id.textEmail);
        mPassword = (EditText) findViewById(R.id.textPassword);
        mButton = (Button) findViewById(R.id.registerButton);
        mButton.setOnClickListener(this);
        mTextView.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

    }

    private void userLogin(){

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

        

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this, "Bad Creds", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v == mButton){
            userLogin();
        }
        if(v == mTextView){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

}
