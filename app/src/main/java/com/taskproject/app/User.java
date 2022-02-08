package com.taskproject.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public static final String KEY = "USER";
    public String unic;
    public String name;
    public String email;
    public String social_id;
    public String password;
    public String avatar;

    public User() {

    }

    public User(Map<String, Object> map) {
        this.name = String.valueOf(map.get("name"));
        this.email = String.valueOf(map.get("email"));
        this.avatar = String.valueOf(map.get("avatar"));
    }

    public static User Create(GoogleSignInAccount account) {

        return null;
    }

    public static User Create(Map<String, Object> map, String unic) {
        User user = new User();
        user.unic = unic;
        user.name = String.valueOf(map.get("name"));
        user.email = String.valueOf(map.get("email"));
        user.social_id = String.valueOf(map.get("social_id"));
        user.password = String.valueOf(map.get("password"));
        user.avatar = String.valueOf(map.get("avatar"));

        return user;
    }

    public static void Save(Context context, Long unic) {
    }

    public static void CheckRegister(String email, String social_id, FragmentActivity activity, OnDatabaseListener listener) {
        final CollectionReference ref = App.DB.collection("users");
        final Query query = ref.whereEqualTo("email", email).
                whereEqualTo("social_id", social_id);
        query.addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Long unic = Long.parseLong("0");
                System.out.println("documentes");
                System.out.println("documentes " + value.getDocuments());
                if(!value.isEmpty()) {
                    List<DocumentSnapshot> documentReference = value.getDocuments();
                    if(documentReference.size() > 0) {
                        DocumentSnapshot documentSnapshot = documentReference.get(0);
                        if(documentSnapshot.getId() != null) {
                            unic = Long.parseLong(documentSnapshot.getId());
                        }
                    }
                }
                listener.onRead(unic);
            }
        });
    }

    public interface OnDatabaseListener<T> {
        void onRead(T value);
    }

    public static void GetFromDatabase(Context context, OnDatabaseListener<User> listener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        if(sharedPreferences.contains(User.KEY)) {
            String unic = String.valueOf(sharedPreferences.getLong(User.KEY, Long.parseLong("0")));
            System.out.println("unic " + unic);
            App.DB.collection("users").document(unic)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(Task<DocumentSnapshot> task) {
                            System.out.println("onComplete");
                            User user = null;
                            if (task.isSuccessful() && task.getResult() != null) {
                                Map<String, Object> map = task.getResult().getData();
                                if(map != null) {
                                    user = User.Create(map, task.getResult().getId());
                                }
                            } else {
                                Log.w(App.TAG, "Error getting documents.", task.getException());
                            }
                            System.out.println("user");
                            System.out.println(user);
                            listener.onRead(user);
                        }
                    });
        } else {
            listener.onRead(null);
        }
    }

    public Map<String, Object> getMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("social_id", social_id);
        data.put("password", password);
        data.put("avatar", avatar);
        return data;
    }

    public String getKey() {
        return String.valueOf(unic);
    }
}
