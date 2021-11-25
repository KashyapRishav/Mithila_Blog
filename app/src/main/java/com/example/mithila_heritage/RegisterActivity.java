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

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout inputEmail,inputPassword,inputConfirmPassword;
    Button btnRegister;
    TextView alreadyHaveAccount;
    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConfirmPassword=findViewById(R.id.inputRePassword);
        btnRegister=findViewById(R.id.btnLogin);
        alreadyHaveAccount=findViewById(R.id.createNewAccount);
        mLoadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptRegistration();
            }
        });
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    public  void AttemptRegistration(){
        String email=inputEmail.getEditText().getText().toString();
        String password=inputPassword.getEditText().getText().toString();
        String confirmPassword=inputConfirmPassword.getEditText().getText().toString();
        if(email.isEmpty() || !(email.contains("@gmail"))){
            showError(inputEmail,"Email is not Valid");
        }else if(password.isEmpty()||password.length()<5){
            showError(inputPassword,"Password must be greater than 5 letters.");
        }else if(!password.equals(confirmPassword)){
            showError(inputPassword,"Password did not Match!");

        }else{
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait, we are verifying...");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Registration is Successful!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterActivity.this,SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Registration Fail!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void showError(TextInputLayout field, String errmsg) {
        field.setError(errmsg);
        field.requestFocus();
    }
}