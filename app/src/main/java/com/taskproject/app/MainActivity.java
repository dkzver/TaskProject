package com.taskproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taskproject.app.ui.gallery.GalleryFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SIGN_IN = 1000;
    private GoogleSignInClient googleSignInClient;
    private View header_view;
    private View view_user;
    private ImageView image_view_user_avatar;
    private TextView text_view_user_name;
    private TextView text_view_user_email;
    private Button button_login;
    public MainViewModel viewModel;

    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            navController.navigate(R.id.nav_login);
            closeDrawer();
        }
    };

    private View.OnClickListener view_user_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show(); 
        }
    };

    private NavigationView.OnNavigationItemSelectedListener navigation_item_selected = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    navController.navigate(R.id.nav_home);
                    closeDrawer();
                    return true;
                case R.id.nav_users:
                    navController.navigate(R.id.nav_users);
                    closeDrawer();
                    return true;
                case R.id.nav_gallery:
                    navController.navigate(R.id.nav_gallery);
                    closeDrawer();
                    return true;
                case R.id.nav_slideshow:
                    navController.navigate(R.id.nav_slideshow);
                    closeDrawer();
                    return true;
                default:
                    return true;
            }
        }
    };

    private FragmentManager fragmentManager;
    private NavController navController;
    private DrawerLayout drawer_layout;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.GetToken(new App.TokenListener() {
            @Override
            public void onTken(String token) {
                Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        fragmentManager = getSupportFragmentManager();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.bind(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(navigation_item_selected);
        header_view = nav_view.getHeaderView(0);
        view_user = header_view.findViewById(R.id.view_user);
        view_user.setVisibility(View.GONE);
        view_user.setOnClickListener(view_user_click_listener);
        button_login = header_view.findViewById(R.id.button_login);
        button_login.setVisibility(View.GONE);
        button_login.setOnClickListener(login_click_listener);
        image_view_user_avatar = header_view.findViewById(R.id.image_view_user_avatar);
        text_view_user_name = header_view.findViewById(R.id.text_view_user_name);
        text_view_user_email = header_view.findViewById(R.id.text_view_user_email);
        setSupportActionBar(toolbar);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        viewModel.userMutableLiveData.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user == null) {
                    view_user.setVisibility(View.GONE);
                    button_login.setVisibility(View.VISIBLE);
                } else {
                    view_user.setVisibility(View.VISIBLE);
                    button_login.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = App.GetBitmapFromURL(user.avatar);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(bitmap != null) {
                                        image_view_user_avatar.setImageBitmap(bitmap);
                                    }
                                    text_view_user_name.setText(user.name);
                                    text_view_user_email.setText(user.email);
                                }
                            });

                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        logout(account);
    }

    private void logout(GoogleSignInAccount account) {
        if (account == null) return;
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final FragmentActivity activity = this;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    final CollectionReference ref = App.DB.collection("users");
                    final Query query = ref.whereEqualTo("email", account.getEmail()).
                            whereEqualTo("social_id", account.getId());
                    query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            String unic = "0";
                            if(value != null && !value.isEmpty()) {
                                List<DocumentSnapshot> documentReference = value.getDocuments();
                                if(documentReference.size() > 0) {
                                    DocumentSnapshot documentSnapshot = documentReference.get(0);
                                    if(documentSnapshot != null) {
                                        unic = documentSnapshot.getId();
                                    }
                                }
                            }
                            final User user = new User();
                            user.name = account.getDisplayName();
                            user.email = account.getEmail();
                            user.social_id = account.getId();
                            user.password = "";
                            user.avatar = String.valueOf(account.getPhotoUrl());
                            if(unic.equals("0")) {
                                user.unic = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                final Map<String, Object> map = user.getMap();
                                final DocumentReference documentReference = App.DB.collection("users").document(user.getKey());
                                documentReference.set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                viewModel.setUser(activity, user);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(App.TAG, "Error adding document", e);
                                            }
                                        });
                            } else {
                                user.unic = unic;
                                viewModel.setUser(activity, user);
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

    public void closeDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        }
    }

    public void attemptSignInGoogle() {
        navController.navigate(R.id.nav_home);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_SIGN_IN);
    }

    public void attemptLogin() {
    }
}