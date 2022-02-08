package com.taskproject.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private static final int REQUEST_SIGN_IN = 1000;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textView = findViewById(R.id.textView);
        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_SIGN_IN);
            }
        });
        findViewById(R.id.buttonRead).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.DB.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(App.TAG, document.getId() + " => " + document.getData().get("name"));
                                    }
                                } else {
                                    Log.w(App.TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });
        findViewById(R.id.buttonAddTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.buttonAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.buttonAddList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.buttonAddCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        User.GetFromDatabase(getApplicationContext(), new User.OnDatabaseListener<User>() {

            @Override
            public void onRead(User user) {
                if(user != null) {
                    textView.setText(user.name);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        logout(account);
    }

    private void logout(GoogleSignInAccount account) {
        if (account == null) return;
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            System.out.println(task);
            System.out.println("task");


            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    final User user = User.Create(account);

                    User.CheckRegister(user.email, user.social_id, this, new User.OnDatabaseListener<Long>() {
                        @Override
                        public void onRead(Long unic) {
                            if (unic == null || unic.equals(Long.parseLong("0"))) {
                                final Map<String, Object> map = user.getMap();
                                final DocumentReference documentReference = App.DB.collection("users").document(user.getKey());
                                documentReference.set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                textView.setText(user.name);
//                                                User.Save(getApplicationContext(), user.unic);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(App.TAG, "Error adding document", e);
                                            }
                                        });
                            } else {
                                textView.setText(user.name);
                                User.Save(getApplicationContext(), unic);
                            }
                        }
                    });



                }

                logout(account);
            } catch (ApiException e) {
                Log.w(App.TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
}