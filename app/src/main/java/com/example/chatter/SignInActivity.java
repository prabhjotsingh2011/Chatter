package com.example.chatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatter.Models.Users;
import com.example.chatter.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {


    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        progressShow();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        binding.txtDoNotHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = binding.txtEmailSignIn.getText().toString();
                String pass= binding.txtPasswordSignIn.getText().toString();

                if(!mail.isEmpty() && !pass.isEmpty()){
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()) startActivity(new Intent(SignInActivity.this,MainActivity.class));
                            else Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{

                    Toast.makeText(SignInActivity.this, "Enter credentials", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
        binding.btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(SignInActivity.this,MainActivity.class));
        }
    }

    final int RC_SIGN_IN=65;
    private void signIn(){
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i,65);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                try {
                   Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                   GoogleSignInAccount account = task.getResult(ApiException.class);
                   firebaseAuthWithGoogle(account.getIdToken());

                } catch (ApiException e) {
                    // ...
                }
                break;
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setMail(user.getEmail());
                            users.setProfilePic(user.getPhotoUrl().toString());
                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).setValue(users);

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            Toast.makeText(SignInActivity.this, "Signed In with Google ", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.d("TAG", "SignwithCredential: faillure ");
                        }
                    }
                });
    }

    private void progressShow() {
        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait\n validation in progress");
    }
}