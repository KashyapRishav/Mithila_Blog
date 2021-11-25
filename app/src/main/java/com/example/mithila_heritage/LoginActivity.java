package com.example.mithila_heritage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout  inputEmail,inputPassword;
    Button btnLogin;
    ProgressDialog mLoadingBar;
    FirebaseAuth mAuth;
    TextView forgotPassword,createNewAccout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoadingBar=new ProgressDialog(this);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        btnLogin=findViewById(R.id.btnLogin);
        forgotPassword=findViewById(R.id.forgotPassword);
        createNewAccout=findViewById(R.id.createNewAccount);

        mAuth= FirebaseAuth.getInstance();

        createNewAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptLogin();
            }
        });

    }
    public void showError(TextInputLayout field, String errmsg) {
        field.setError(errmsg);
        field.requestFocus();
    }

    private void AttemptLogin() {
        String email=inputEmail.getEditText().getText().toString();
        String password=inputPassword.getEditText().getText().toString();
        if(email.isEmpty() || !(email.contains("@gmail"))){
            showError(inputEmail,"Email is not Valid");
        }else if(password.isEmpty()||password.length()<5) {
            showError(inputPassword, "Password must be greater than 5 letters.");
        }else{
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait, we are verifying...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"Login is Successful!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LoginActivity.this,SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                         finish();
                    }else{
                        mLoadingBar.dismiss();
                        Toast.makeText(LoginActivity.this,"Login Fail!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}