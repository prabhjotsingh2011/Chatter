package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatter.Models.Users;
import com.example.chatter.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating an Account");
        progressDialog.setMessage("Your account is being created, please be patient and wait for some moment");

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user= binding.txtUsername.getText().toString();
                String email=binding.txtEmail.getText().toString();
                String pass = binding.txtPassword.getText().toString();

                if(!email.isEmpty() && !pass.isEmpty() && !user.isEmpty() ){
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();

                                Users usermodel = new Users(user,email,pass);
                                String id = task.getResult().getUser().getUid();

                                database.getReference().child("Users").child(id).setValue(usermodel);

                                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    
                }else Toast.makeText(SignUpActivity.this, "Please enter all the details to SignUp ", Toast.LENGTH_SHORT).show();
            }
        });
        binding.txtAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });

    }
}