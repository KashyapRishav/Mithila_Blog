package com.example.mithila_heritage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE =101 ;
    CircleImageView profileImageView;
    EditText inputUsername,inputCity,inputProfession,inputMob;
    Button btnSave;
    Uri imageUri;

    ProgressDialog mLoadingBar;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;


    //Toolbar
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        profileImageView=findViewById(R.id.profile_image);
        inputUsername =findViewById(R.id.inputUsername);
        inputCity=findViewById(R.id.inputProfession);
        inputMob=findViewById(R.id.inputPhone);
        inputProfession=findViewById(R.id.inputProfession);
        btnSave=findViewById(R.id.btnSave);

        mLoadingBar=new ProgressDialog(this);

        //Toolbar
        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setup Profile");


        //Initialize for firebase
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef= FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef= FirebaseStorage.getInstance().getReference().child("ProfileImages");

        // Profile pic set start
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        // Profile pic set end

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });

    }

    private void SaveData() {
        String username=inputUsername.getText().toString();
        String city=inputCity.getText().toString();
        String mob=inputMob.getText().toString();
        String profession=inputProfession.getText().toString();

        if(username.isEmpty() || username.length()<3)
            showError(inputUsername,"UserName is not valid!");
        else if(city.isEmpty())
            showError(inputCity,"City can not be Empty!");
        else if(profession.isEmpty())
            showError(inputProfession,"Profession can not be Empty!");
        else if(mob.isEmpty())
            showError(inputMob,"Mobile number can not be Empty!");
        else if(imageUri == null)
            Toast.makeText(SetupActivity.this,"Please select an Image.",Toast.LENGTH_SHORT).show();
        else{
            mLoadingBar.setTitle("Profile Setup..");
            mLoadingBar.setMessage("Please wait, we are updating...");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(false);
            StorageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){
                        StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap=new HashMap();
                                hashMap.put("username",username);
                                hashMap.put("city",city);
                                hashMap.put("Mob",mob);
                                hashMap.put("Profession",profession);
                                hashMap.put("ProfileImage",uri.toString());
                                hashMap.put("Status","Ofline");


                                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this,"Profile Updated !",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(SetupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this,"Profile Updation Fail !",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                }
            });
        }


    }
    public void showError(EditText field, String errmsg) {
        field.setError(errmsg);
        field.requestFocus();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}