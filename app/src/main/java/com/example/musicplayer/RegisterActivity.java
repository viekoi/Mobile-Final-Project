package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {
    public EditText signupEmail, signupPassword;
    Button btnSignUp,btnGoogleLogin;
    TextView loginRedirectText,resetRedirectText;
    FirebaseAuth firebaseAuth;

    GoogleSignInClient googleSignInClient;

    GoogleSignInOptions googleSignInOptions;

    private static final String TAG = "GoogleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnGoogleLogin = findViewById(R.id.signInGoogle);
        signupEmail = findViewById(R.id.registerEmail);
        signupPassword = findViewById(R.id.registerPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        loginRedirectText = findViewById(R.id.txtLogin);
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

        // handle khi bấm nút xác nhận đăng ký
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = signupEmail.getText().toString();
                String paswd = signupPassword.getText().toString();
                if (emailID.isEmpty()) {
                    signupEmail.setError("Provide your Email first!");
                    signupEmail.requestFocus();
                } else if (paswd.isEmpty()) {
                    signupPassword.setError("Set your password");
                    signupPassword.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(RegisterActivity.this, UserActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // hanlde sự kiện bấm text trở lại activity login
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(I);
            }
        });

        // hanlde sự kiện bấm text quên mật khaẩu
        resetRedirectText.setOnClickListener(view -> {
            Intent I = new Intent(RegisterActivity.this, ResetPasswordActivity.class);
            RegisterActivity.this.startActivity(I);
        });

        btnGoogleLogin.setOnClickListener((View.OnClickListener) view -> {
            // Initialize sign in intent
            Intent intent = googleSignInClient.getSignInIntent();

            // Start activity for result
            startActivityForResult(intent, 100);
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

}
