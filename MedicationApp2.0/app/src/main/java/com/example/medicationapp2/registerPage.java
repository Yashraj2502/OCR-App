package com.example.medicationapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class registerPage extends AppCompatActivity {
    // Declaring Variables
    public static final String TAG = "TAG";
    EditText mGetName, mGetEmail, mGetPassword;
    TextView mGoToLogin;
    Button mContinue;

    // Firebase Connection
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String UserID;

    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mGetName = findViewById(R.id.getName);
        mGetEmail = findViewById(R.id.getEmail);
        mGetPassword = findViewById(R.id.getPassword);
        mContinue = findViewById(R.id.registerbutton);
        mGoToLogin = findViewById(R.id.toLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Check If There Is An Existing User
        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mGetEmail.getText().toString().trim();
                String Password = mGetPassword.getText().toString().trim();
                final String fullName = mGetName.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mGetEmail.setError("Email ID is required!");
                    return;
                }

                if (TextUtils.isEmpty(Password)){
                    mGetPassword.setError("Password is required!");
                    return;
                }

                if (Password.length() < 6){
                    mGetPassword.setError("Strong Password is required!");
                    return;
                }

                // Registering User In The FireBase
                firebaseAuth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(registerPage.this, "User Created", Toast.LENGTH_SHORT).show();

                            // Setting Up Email Verification
                            FirebaseUser newUser = firebaseAuth.getCurrentUser();
                            newUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(registerPage.this, "Email Verification Has Been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "OnFailure: Email Verification Wasn't send" + e.getMessage());
                                }
                            });

                            // Saving User Details
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullName", fullName);
                            user.put("email", email);
                            UserID = firebaseAuth.getCurrentUser().getUid();
                            firebaseDatabase.getReference()
                                    .child(UserID)
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(registerPage.this, MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(registerPage.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(registerPage.this, "Error: Unable To Create Account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(registerPage.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Re-direct to login page
        mGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registerPage.this, loginPage.class);
                startActivity(intent);
            }
        });
    }
}
