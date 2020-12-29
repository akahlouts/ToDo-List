package com.akahlouts.todo_list;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import util.ToDoListApi;

public class SignupActivity extends AppCompatActivity {
    private EditText editText_userName;
    private EditText editText_password;
    private AutoCompleteTextView editText_email;
    private Button btn_createProfile;
    private TextView textView_login;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        editText_userName = findViewById(R.id.editText_userName);
        editText_password = findViewById(R.id.editText_password);
        editText_email = findViewById(R.id.editText_email);

        textView_login = findViewById(R.id.textView_login);
        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        btn_createProfile = findViewById(R.id.btn_createProfile);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already loggedin..
                } else {
                    //no user yet...
                }
            }
        };

        btn_createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText_email.getText().toString())
                        && !TextUtils.isEmpty(editText_password.getText().toString())
                        && !TextUtils.isEmpty(editText_userName.getText().toString())){

                    String email = editText_email.getText().toString().trim();
                    String password = editText_password.getText().toString().trim();
                    String username = editText_userName.getText().toString().trim();

                    createUserEmailAccount(email, password, username);

                }else {
                    Toast.makeText(SignupActivity.this,
                            "Empty Fields Not Allowed!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void createUserEmailAccount(String email, String password, String username) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)) {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //we take user to ListActivity
                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                //Create a user Map so we can create a user in the User collection
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);

                                //save to our firestore database
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.getResult().exists()){
                                                                    String name = task.getResult()
                                                                            .getString("username");

                                                                    ToDoListApi toDoListApi = ToDoListApi.getInstance(); //Global API
                                                                    toDoListApi.setUserId(currentUserId);
                                                                    toDoListApi.setUsername(name);

                                                                    Intent intent = new Intent(SignupActivity.this,ListActivity.class);
                                                                    intent.putExtra("username",name);
                                                                    intent.putExtra("userId",currentUserId);
                                                                    startActivity(intent);

                                                                }else {

                                                                }

                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            } else {
                                //something went wrong
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}