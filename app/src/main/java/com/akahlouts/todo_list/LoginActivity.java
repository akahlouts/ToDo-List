package com.akahlouts.todo_list;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.ToDoListApi;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private TextView textView_create;
    private AutoCompleteTextView email;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        textView_create = findViewById(R.id.textView_create);
        textView_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginEmailPasswordUser(email.getText().toString().trim(),
                        password.getText().toString().trim());

            }
        });

    }

    private void loginEmailPasswordUser(String email, String pwd) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String currentUserId = user.getUid();
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                }
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        ToDoListApi toDoListApi = ToDoListApi.getInstance();
                                                        toDoListApi.setUsername(snapshot.getString("username"));
                                                        toDoListApi.setUserId(snapshot.getString("userId"));

                                                        //Go to ListActivity
                                                        startActivity(new Intent(LoginActivity.this,
                                                                ListActivity.class));
                                                    }
                                                }

                                            }
                                        });

                            }else {
                                Toast.makeText(LoginActivity.this, "Email or password uncorrected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LoginActivity", "onFailure: error");

                        }
                    });

        } else {
            Toast.makeText(LoginActivity.this, "Please Enter email and password",
                    Toast.LENGTH_SHORT).show();
        }
    }
}