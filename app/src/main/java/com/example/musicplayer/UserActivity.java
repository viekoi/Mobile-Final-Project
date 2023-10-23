package com.example.musicplayer;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserActivity extends AppCompatActivity {

    Button btnLogOut;
    TextView txtUser;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    GoogleSignInClient googleSignInClient;

    GoogleSignInOptions googleSignInOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        txtUser = (TextView) findViewById(R.id.txtUser);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("682531873901-skm0to3drv2gadsbtqju3h60k7javlr5.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        user = firebaseAuth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            txtUser.setText(user.getEmail());
        }

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut();
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
}