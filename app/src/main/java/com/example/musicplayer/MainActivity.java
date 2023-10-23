package com.example.musicplayer;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {


    public EditText loginEmail, loginPassword;
    Button btnLogin, btnGoogleLogin;
    TextView registerRedirectText, resetRedirectText;
    FirebaseAuth firebaseAuth;

    GoogleSignInClient googleSignInClient;

    GoogleSignInOptions googleSignInOptions;

    private static final String TAG = "GoogleActivity";

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginButton);
        btnGoogleLogin = findViewById(R.id.signInGoogle);
        registerRedirectText = findViewById(R.id.txtRegister);
        resetRedirectText = findViewById(R.id.txtResetPassword);
        // tạo google signin options
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("682531873901-skm0to3drv2gadsbtqju3h60k7javlr5.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // tạo google signin client
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //tạo firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        // lắng nghe khi trạng thái đăng nhập thay đoi63
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent I = new Intent(MainActivity.this, UserActivity.class);
                startActivity(I);
            }
        };


        // hanlde sự kiện bấm nút google
        btnGoogleLogin.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();

            // Start activity for result
            startActivityForResult(intent, 100);
        });
        // hanlde sự kiện bấm text đăng ký
        registerRedirectText.setOnClickListener(view -> {
            Intent I = new Intent(MainActivity.this, RegisterActivity.class);
            MainActivity.this.startActivity(I);
        });
        // hanlde sự kiện bấm text quên mật khaẩu
        resetRedirectText.setOnClickListener(view -> {
            Intent I = new Intent(MainActivity.this, ResetPasswordActivity.class);
            MainActivity.this.startActivity(I);
        });

        // hanlde sự kiện bấm bút đăng nhập bằng tk và mk
        btnLogin.setOnClickListener(view -> {
            String userEmail = loginEmail.getText().toString();
            String userPassword = loginPassword.getText().toString();
            if (userEmail.isEmpty()) {
                loginEmail.setError("Provide your Email first!");
                loginEmail.requestFocus();
            } else if (userPassword.isEmpty()) {
                loginPassword.setError("Enter Password!");
                loginPassword.requestFocus();
            } else {
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Not sucessfull: invalid email or password ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    // hanlde đăng nhập gmail
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            System.out.println(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    // đăng nhập vào gmail sau khi xác thực thành công
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

    }


}