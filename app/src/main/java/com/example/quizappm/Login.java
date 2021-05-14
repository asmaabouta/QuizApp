package com.example.quizappm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private Button buttonSignIn;
    private Button buttonGoogle;
    private TextView textViewRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Login";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //préciser le design d'activité qu'on va traviller avec
        setContentView(R.layout.login);
        GoogleSignInConfig();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        editTextEmail=(EditText) findViewById(R.id.etLogin) ;
        editTextPassword=(EditText) findViewById(R.id.etPasswordL) ;
        buttonSignIn= (Button) findViewById(R.id.bLogin);
        //code bouton SignIn
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextEmail.getText().toString().trim();
                String password=editTextPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email))
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(password))
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_SHORT).show();
                if(password.length()<6)
                    Toast.makeText(getApplicationContext(), "Password too short", Toast.LENGTH_SHORT).show();

                signInWithEmailAndPassword(email,password);
            }
        });
        //code bouton signInWithGoogle
        buttonGoogle= (Button) findViewById(R.id.bGoogleLogin);
        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //code bouton Register
        textViewRegister= (TextView) findViewById(R.id.tvRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }
    private void GoogleSignInConfig() {
        // [START config_signin]
        // Configure Google Sign In
       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]
        // [START initialize_auth]

    }
    //SignIn avec google
    private void signIn() {
        //pour inviter l'utilisateur à sélectionner son compte i use getSignInIntent()
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //SignIn avec mail et pwd
    private void signInWithEmailAndPassword(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    //utiliser une API Task et un certain nombre de méthodes qui renvoient Task .
                    // Task est une API qui représente les appels de méthode asynchrones
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Pour être averti lorsque la tâche réussit, joignez un task.isSuccessfull
                        if (task.isSuccessful()) {
                            // Connexion réussie, mettre à jour l'interface utilisateur avec les informations de l'utilisateur connecté
                            Toast.makeText(getApplicationContext(), "SignInWithEmail:success", Toast.LENGTH_LONG).show();
                            //pr concerver l'utilisateur actuelle de l app
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(getApplicationContext(), Quiz.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "SignInWithEmail:failure", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }
    private void register() {
        Intent intent=new Intent(this, Register.class);
        startActivity(intent);
    }
    // récupère le résultat de register et effectue le reste de l'opération
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Résultat renvoyé du lancement de l'intention depuis GoogleSignInApi.getSignInIntent (...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "signInWithCredential:success", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(getApplicationContext(), Quiz.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "signInWithCredential:failure", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent intent=new Intent(getApplicationContext(), Quiz.class);
            startActivity(intent);
        }

    }
}